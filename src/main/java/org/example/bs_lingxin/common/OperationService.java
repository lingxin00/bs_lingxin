package org.example.bs_lingxin.common;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.bs_lingxin.config.operation;
import org.example.bs_lingxin.entity.OperationLog;
import org.example.bs_lingxin.service.OperationLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class OperationService {

    private final OperationLogService operationLogService;

    public OperationService(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Pointcut("@annotation(org.example.bs_lingxin.config.operation)")
    public void logPointcut(){}

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point)throws Throwable{
        long startTime = System.currentTimeMillis();

        Object result = point.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        try {
            saveLog(point,costTime);
        }catch (Exception e){
            log.error("记录操作日志失败", e);
        }
        return result;
    }

    private void saveLog(ProceedingJoinPoint point,long costTime){
        // 获取方法签名，拿到被拦截的Method对象
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        operation operation = method.getAnnotation(operation.class);
        if (operation == null) {
            return;
        }
        // 判断注解是否存在
        if (method.getAnnotation(operation.class) == null){
            return;
        }
        //创建日志对象，添加注解对应信息
        OperationLog operationLog = new OperationLog();
        operationLog.setModule(operation.moudle());
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

            Object[] arg = point.getArgs();
            try {
                operationLog.setRequestParams(JSON.toJSONString(arg));
            }catch (Exception e){
                operationLog.setRequestParams("参数序列化失败");
            }
        }
        //获取当前登录管理员ID（Sa-Token获取登录用户） ==========
        Long adminId = StpUtil.getLoginIdAsLong();
        operationLog.setAdminId(adminId);
        // 设置接口耗时
        operationLog.setCostTime((int) costTime);
        //异步保存日志
        operationLogService.saveAsync(operationLog);
    }
    /**
     * 获取IP
     * */
    private String getIpAddr(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
