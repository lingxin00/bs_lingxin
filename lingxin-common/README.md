# lingxin-common

全项目共享的**基础公共组件**：统一响应体、状态码枚举、业务异常、全局异常处理、分页基类、自定义注解。

本模块**不含任何业务逻辑**，也不依赖任何其他模块。所有模块都可以依赖它，它不依赖任何模块。

---

## 一、职责范围

| 职责 | 说明 |
|---|---|
| 统一返回格式 | 所有接口的响应体都是 `Result<T>` |
| 统一状态码 | `ResultCode` 枚举集中管理 200 / 400 / 401 / 500 等业务码 |
| 统一异常出口 | 业务代码只负责 `throw`，由 `GlobalExceptionHandler` 转成 JSON |
| 分页参数基类 | 所有分页查询 DTO 继承 `BasePageQueryDTO`，复用页码校验 |
| 自定义注解 | `@OperationRecord` 供操作日志切面识别 |

**这个模块不做的事**：不放 Entity、不放 Mapper、不放 Controller、不放任何 Spring 配置类（配置在 `lingxin-framework`）。

---

## 二、包与类

所有类都在 `org.example.bs_lingxin.common` 之下。

| 包 | 类 | 职责 |
|---|---|---|
| `common` | `Result` | 统一响应体 |
| `common.enums` | `ResultCode` | 全局状态码枚举 |
| `common.annotation` | `OperationRecord` | 操作日志注解 |
| `common.dto` | `BasePageQueryDTO` | 分页查询基类 |
| `common.exception` | `BusinessException` | 自定义业务异常 |
| `common.exception` | `GlobalExceptionHandler` | 全局异常处理 |

---

## 三、核心类说明

### Result\<T\>

统一响应体，固定三个字段：

```json
{ "code": 200, "msg": "成功", "data": {} }
```

全部构建方法都走 `ResultCode` 枚举，不再硬编码数字：

| 方法 | 返回 code | 返回 msg |
|---|---|---|
| `success()` | 200 | 成功 |
| `success(data)` | 200 | 成功 |
| `fail()` | 999 | 未知错误 |
| `fail(ResultCode)` | 取枚举 | 取枚举 |
| `fail(ResultCode, String)` | 取枚举 | 自定义文案 |
| `fail(Integer, String)` | 自定义 | 自定义 |

**约定**：HTTP 状态码恒为 200，业务成败看 `code` 字段，前端统一判 `code === 200`。

最后那个 `fail(Integer, String)` 是兼容历史调用的出口，新代码应优先用 `fail(ResultCode, ...)`，避免裸数字散落。

### ResultCode

14 个状态码，按区间分段：200 成功；4xx 请求侧错误；423 / 429 并发与限流；5xx 服务端错误；9xx 业务兜底。

| 枚举 | code | 含义 |
|---|---|---|
| `SUCCESS` | 200 | 成功 |
| `BAD_REQUEST` | 400 | 请求参数不正确（`@Valid` 校验失败） |
| `UNAUTHORIZED` | 401 | 账号未登录，未带 token 或已失效 |
| `FORBIDDEN` | 403 | 没有该操作权限 |
| `NOT_FOUND` | 404 | 请求未找到，接口路径错误 |
| `METHOD_NOT_ALLOWED` | 405 | 请求方法不正确 |
| `LOCKED` | 423 | 请求失败请稍后重试（并发锁竞争） |
| `TOO_MANY_REQUESTS` | 429 | 请求过于频繁（限流） |
| `INTERNAL_SERVER_ERROR` | 500 | 系统异常 |
| `NOT_IMPLEMENTED` | 501 | 功能未实现 / 未开启 |
| `BAD_CONFIGURATION` | 502 | 错误的配置项 |
| `REPEAT_REQUEST` | 900 | 重复请求（幂等拦截） |
| `DEMO_MODE` | 901 | 演示模式，禁止写操作 |
| `UNKNOWN` | 999 | 未知错误，兜底 |

两个静态工具：`of(Integer)` 按 code 反查枚举（找不到返回 `null`），`isSuccess(Integer)` 判断是否成功。

### BusinessException

自定义业务异常，继承 `RuntimeException`，带一个 `code` 字段：

```java
throw new BusinessException("账号不存在", 401);   // 指定业务码
throw new BusinessException("数据为空");          // 默认 500
```

三点设计意图：

1. 继承 `RuntimeException` 而非 `Exception`，调用方不必 `throws`，且 `@Transactional` 默认会对它回滚；
2. 多带一个 `code` 字段，让前端能区分错误类型；
3. 加 `@Getter`，让全局处理器能取到 `getCode()`。

### GlobalExceptionHandler

`@RestControllerAdvice`，靠 Spring MVC 回调生效，**没有任何地方主动调用它**。

| 处理方法 | 捕获类型 | 返回 |
|---|---|---|
| `businessException()` | `BusinessException` | `Result.fail(code, message)`，保留业务码 |
| `allException()` | `Exception` | `fail(INTERNAL_SERVER_ERROR)` + 自定义文案，兜底 |

匹配规则是**最具体的类型优先**，所以 `BusinessException` 不会被兜底方法抢走。

### @OperationRecord

标注在 Controller 方法上，由 `lingxin-module-system` 的 `OperationService` 切面读取并异步写库。

```java
@OperationRecord(module = "管理员操作", type = "添加", desc = "添加管理员")
```

三个属性：`module`（模块名）、`type`（操作类型）、`desc`（描述），全部有默认空串。

### BasePageQueryDTO

分页查询基类，含 `current`（页码，默认 1、最小 1）和 `size`（每页条数，默认 10、范围 1~1000），带 `@NotNull` / `@Min` / `@Max` 校验。

所有 `*PageQueryDTO` 都应 `extends BasePageQueryDTO`，并使用 `@EqualsAndHashCode(callSuper = true)`。

---

## 四、依赖

### 直接依赖

| 依赖 | 用途 |
|---|---|
| `spring-boot-starter-webmvc` | `GlobalExceptionHandler` 需要 MVC 注解 |
| `spring-boot-starter-validation` | `BasePageQueryDTO` 的校验注解 |
| `lombok` | 编译期，`optional` |

### 模块依赖

| 方向 | 模块 |
|---|---|
| 本模块依赖 | 无 |
| 依赖本模块 | `lingxin-framework`、`lingxin-module-system`、`lingxin-module-member`、`lingxin-module-product`、`lingxin-module-trade`、`lingxin-server`（全部 6 个） |

因为引了 `spring-boot-starter-webmvc`，其余模块通过 common 就能拿到 MVC 与 Jackson 的传递依赖，不必各自重复声明。

---

## 五、注意事项与待办

1. **操作日志切面不在这里**。`OperationService` 注入 `OperationLogService`，若放在 common 会形成 `common → system` 的循环依赖，因此它被放在 `lingxin-module-system` 的 `aspect` 包。注解留在这里，其他模块引用注解时只依赖 common。
2. **缺参数校验异常处理**。Controller 上大量使用 `@Valid`，校验失败抛的是 `MethodArgumentNotValidException`，目前会掉进 `allException()`，前端只会看到 500「服务器内部错误」，看不到具体哪个字段错了。建议补一个 handler 返回 `BAD_REQUEST` 并带上字段提示。
3. **缺 Sa-Token 异常处理**。未登录抛 `NotLoginException`、无权限抛 `NotPermissionException`，目前同样被兜底成 500。前端需要 401 才能跳登录页，建议补两个 handler 分别返回 `UNAUTHORIZED` / `FORBIDDEN`。
4. **`BusinessException` 缺 `ResultCode` 构造器**。目前只能传 `Integer`，建议加 `BusinessException(ResultCode)`，让抛异常的地方也用上枚举，避免裸数字。
5. **日志级别可优化**。`BusinessException` 属于预期内的业务失败，用 `log.warn` 比 `log.error` 更合适，避免污染错误告警。
