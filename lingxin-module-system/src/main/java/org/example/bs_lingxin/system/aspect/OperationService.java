package org.example.bs_lingxin.system.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.bs_lingxin.common.annotation.OperationRecord;
import org.example.bs_lingxin.system.operationlog.entity.OperationLog;
import org.example.bs_lingxin.system.operationlog.service.OperationLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 操作日志切面
 *
 * 拦截标注了 {@link OperationRecord} 的方法，采集请求信息、登录人、耗时和异常，
 * 交给 {@link OperationLogService} 异步落库。日志记录失败不影响业务返回。
 */
@Slf4j
@Aspect
@Component
public class OperationService {

    /**
     * 入参中的敏感字段：命中后值统一替换为 ***，避免密码、令牌被明文写进审计表。
     * 兼容带引号的字符串值和不带引号的数字/布尔值。
     */
    private static final Pattern SENSITIVE_JSON = Pattern.compile(
            "(\"(?:password|passwd|pwd|oldPassword|newPassword|confirmPassword|"
                    + "token|accessToken|refreshToken|secret|idCard|bankCard)\"\\s*:\\s*)"
                    + "(\"(?:[^\"\\\\]|\\\\.)*\"|[^,}\\]]+)",
            Pattern.CASE_INSENSITIVE);

    /** request_params 落库长度上限，超出截断，避免超长入参撑爆字段 */
    private static final int MAX_PARAM_LENGTH = 1000;

    private final OperationLogService operationLogService;

    public OperationService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Pointcut("@annotation(org.example.bs_lingxin.common.annotation.OperationRecord)")
    public void logPointcut(){}

    /**
     * 环绕通知
     *
     * 日志放在 finally 里：目标方法正常返回、抛异常都会记录，
     * 否则「删除失败」「修改报错」这类最需要留痕的操作反而没有日志。
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point)throws Throwable{
        long startTime = System.currentTimeMillis();
        Throwable error = null;
        try {
            return point.proceed();
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            try {
                saveLog(point, costTime, error);
            }catch (Exception e){
                // 记日志失败不能影响业务，也不能覆盖业务异常
                log.error("记录操作日志失败", e);
            }
        }
    }

    /**
     * 组装并提交日志
     *
     * 请求上下文来自 ThreadLocal，必须在业务线程取完再交给异步方法。
     *
     * @param costTime 目标方法耗时（毫秒），不含写库时间
     * @param error    目标方法抛出的异常，正常返回时为 null
     */
    private void saveLog(ProceedingJoinPoint point, long costTime, Throwable error){
        OperationRecord operation = resolveAnnotation(point);
        if (operation == null) {
            // 切点已限定注解，这里防御性返回
            return;
        }

        OperationLog operationLog = new OperationLog();
        operationLog.setModule(operation.module());
        operationLog.setOperationType(operation.type());
        operationLog.setOperationDesc(operation.desc());

        //获取当前 HTTP 请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null){
            HttpServletRequest request = attributes.getRequest();
            operationLog.setRequestUrl(request.getRequestURI());
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setIp(getIpAddr(request));
            operationLog.setUserAgent(request.getHeader("User-Agent"));
            operationLog.setRequestParams(serializeArgs(point.getArgs()));
        }

        // 未登录或 token 失效时不能抛异常，否则整条日志丢失
        operationLog.setAdminId(StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null);
        operationLog.setCostTime((int) costTime);

        if (error != null) {
            // 失败详情先落到应用日志，便于按时间/URL 关联排查
            log.warn("接口执行异常，已记录操作日志 module={} type={} desc={} url={} adminId={}",
                    operation.module(), operation.type(), operation.desc(),
                    operationLog.getRequestUrl(), operationLog.getAdminId(), error);
        }

        //异步保存日志
        operationLogService.saveAsync(operationLog);
    }

    /**
     * 取方法上的注解
     *
     * 走接口代理时 {@code signature.getMethod()} 拿到的是接口方法，
     * 注解一般标在实现类上，所以取不到时再回退到目标对象查一次。
     */
    private OperationRecord resolveAnnotation(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationRecord operation = method.getAnnotation(OperationRecord.class);
        if (operation != null) {
            return operation;
        }
        try {
            Method targetMethod = point.getTarget().getClass()
                    .getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.getAnnotation(OperationRecord.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 序列化入参：跳过请求/响应/文件对象，敏感字段脱敏，超长截断
     */
    private String serializeArgs(Object[] args){
        if (args == null || args.length == 0){
            return "[]";
        }
        List<Object> printable = new ArrayList<>(args.length);
        for (Object arg : args) {
            // 请求、响应、上传文件既无法序列化，也不该进审计表
            if (arg == null || arg instanceof ServletRequest || arg instanceof ServletResponse
                    || arg instanceof MultipartFile) {
                continue;
            }
            printable.add(arg);
        }
        try {
            String json = JSON.toJSONString(printable);
            json = SENSITIVE_JSON.matcher(json).replaceAll("$1\"***\"");
            return json.length() > MAX_PARAM_LENGTH
                    ? json.substring(0, MAX_PARAM_LENGTH) + "..."
                    : json;
        }catch (Exception e){
            log.warn("操作日志入参序列化失败", e);
            return "参数序列化失败";
        }
    }

    /**
     * 获取IP
     *
     * X-Forwarded-For 可能是「客户端IP, 代理1, 代理2」，取第一段才是客户端真实地址。
     * */
    private String getIpAddr(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            int comma = ip.indexOf(',');
            return normalize(comma > 0 ? ip.substring(0, comma) : ip);
        }
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return normalize(ip);
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return normalize(ip);
        }
        return normalize(request.getRemoteAddr());
    }

    private boolean isValidIp(String ip){
        return ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip);
    }

    private String normalize(String ip){
        if (ip == null) {
            return null;
        }
        ip = ip.trim();
        // 本机访问时是 IPv6 回环地址，转成 IPv4 便于阅读
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
