# lingxin-framework

**技术组件装配层**：把鉴权、密码加密、线程池等通用技术能力封装成 Spring 配置，供业务模块直接使用。

这里只有 `@Configuration` 配置类，**不含任何业务逻辑**，也不含 Controller / Service / Entity。

---

## 一、职责范围

| 职责 | 对应类 |
|---|---|
| 登录鉴权拦截 | `SaTokenConfig` |
| 密码加密与安全过滤链 | `PasswordBCrypt` |
| 操作日志专用线程池 | `AsyncConfig` |

---

## 二、包与类

| 包 | 类 | 职责 |
|---|---|---|
| `framework.config` | `SaTokenConfig` | Sa-Token 登录拦截器 |
| `framework.config` | `PasswordBCrypt` | BCrypt 加密 + 关闭 Spring Security 自带鉴权 |
| `framework.config` | `AsyncConfig` | `logExecutor` 线程池 |

---

## 三、核心类说明

### SaTokenConfig

注册 `SaInterceptor`，拦截 `/**`，并放行登录类接口：

```java
registry.addInterceptor(new SaInterceptor())
        .addPathPatterns("/**")
        .excludePathPatterns("/api/admin/login", "/api/user/login", "/api/user/login");
```

**实际效果**：除放行清单外，所有接口在未登录时抛 `NotLoginException`。登录态由 Sa-Token 统一管理，参数见 `lingxin-server` 的 `application.yml`（token 名称、有效期 30 天、闲置 30 分钟失效等）。

> **待修正**：放行清单里 `/api/user/login` 写了两遍，第三项应该是商家登录入口 `/api/merchant/login`。另外 C 端与商家账号如果要同时上线，`module-member` 的两个登录接口实现后必须补进放行清单，否则会被拦。
>
> 历史坑：这里的路径最初写成 `/admin/login`，与 Controller 实际的 `/api/admin` 前缀不匹配，导致登录接口被自己拦住，现已改为带 `/api` 前缀。

### PasswordBCrypt

两件事：

1. 注册 `BCryptPasswordEncoder` Bean，登录时用 `passwordEncoder.matches(明文, 密文)` 校验；
2. 注册 `SecurityFilterChain`，**关闭 CSRF 并放行全部请求**——即主动禁用 Spring Security 自带的鉴权过滤链，把鉴权完全交给 Sa-Token。

这样做的原因：只想要 Spring Security 的 BCrypt 工具，不想要它整套过滤链，避免两套鉴权体系打架。

### AsyncConfig

`logExecutor` 线程池，专供操作日志异步写库使用：

| 参数 | 值 | 说明 |
|---|---|---|
| corePoolSize | 15 | 常驻线程数，空闲不回收 |
| maxPoolSize | 30 | 核心线程的 2 倍，队列满后才扩容 |
| queueCapacity | 500 | 队列最多排 500 个日志任务 |
| keepAliveSeconds | 60 | 非核心线程空闲 60 秒回收 |
| rejectedExecutionHandler | `CallerRunsPolicy` | 队列满时由调用线程执行，不丢任务 |
| waitForTasksToCompleteOnShutdown | true | 停机前处理完队列 |
| awaitTerminationSeconds | 10 | 最多等 10 秒 |
| threadNamePrefix | `log-thread-` | 日志里便于区分 |

使用方：`OperationLogServiceImpl.saveAsync()` 上的 `@Async("logExecutor")`。需要在启动类上开启 `@EnableAsync`（已在 `BsLingxinApplication` 上）。

参数取舍：队列 500 + 核心 15 意味着并发要压到 515 个日志任务才会扩容到 30 线程，日常写库基本在 15 个常驻线程内消化。拒绝策略特意没用默认的 `AbortPolicy`——那会直接丢任务，审计日志丢了就查不回来。

---

## 四、依赖

### 直接依赖

| 依赖 | 用途 |
|---|---|
| `lingxin-common` | 配置类引用 common 里的类型 |
| `spring-boot-starter-security` | `BCryptPasswordEncoder`、`SecurityFilterChain` |
| `sa-token-spring-boot4-starter` | Sa-Token 的 Boot 4 专用 starter |
| `lombok` | 编译期，`optional` |

### 模块依赖

| 方向 | 模块 |
|---|---|
| 本模块依赖 | `lingxin-common` |
| 依赖本模块 | `lingxin-module-system`、`lingxin-server` |
| 尚未依赖 | `lingxin-module-member`、`lingxin-module-product`、`lingxin-module-trade` |

三个尚未依赖的模块，需要取当前登录人时会碰到 `StpUtil` 找不到类的问题。两种解法：

- 在其 pom 中加 `lingxin-framework` 依赖；
- 或由 `lingxin-module-system` 暴露 `LoginApi`（取当前登录人 ID），业务模块依赖接口而不依赖 Sa-Token 实现。

第二种更干净，避免鉴权实现扩散到所有模块。若走第一种，注意 `sa-token-spring-boot4-starter` 会随之传递进来，各模块都能直接调 `StpUtil`，长期容易失控。

### 为什么传递依赖能拿到 MVC

`spring-boot-starter-webmvc` 由 `lingxin-common` 引入，framework 依赖 common 后即可传递获得，因此本模块不需要重复声明 web 相关 starter。

---

## 五、后续扩展位置

以下能力都应当加在本模块，而不是散落到业务模块：

- Redis 序列化配置、缓存管理器
- 跨域 CORS 配置
- 全局日期格式、Jackson 3 的 `JsonMapper` 定制
- MyBatis-Plus 分页插件 `MybatisPlusInterceptor`
- 全局请求日志 / TraceId 过滤器
- 文件上传、OSS 客户端

**注意**：`MybatisPlusInterceptor` 现在还没有配置，因此 MyBatis-Plus 的分页不会自动生效，分页查询目前是手写 `LIMIT`。等要用 `Page<T>` 时，需要在 `lingxin-module-system` 或本模块补一个配置类——它需要 `mybatis-plus` 依赖，放本模块会让 framework 反向依赖持久层，建议放在 system 里更合适。
