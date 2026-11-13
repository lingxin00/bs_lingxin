# lingxin-module-system

**后台（运营侧）账号与权限体系**：平台管理员的登录、增删改查、角色管理，以及全后台操作日志审计。

本模块是全项目目前**唯一完整实现的业务模块**（Controller → Service → Mapper → XML 全链路打通），其余业务模块只有实体和 DTO。

---

## 一、职责范围

| 子域 | 表 | 说明 |
|---|---|---|
| 管理员 | `admin` | 平台内部员工账号，负责管理商家、用户、商品、订单 |
| 角色 | `role` | 权限角色，区分超级管理员、运营、客服 |
| 操作日志 | `operation_log` | 审计追溯，记录谁在什么时间做了什么 |

**不负责**：C 端用户（在 `lingxin-module-member`）、商家账号（在 `lingxin-module-member`）、商品与订单（各自模块）。

---

## 二、包与类

采用「业务包 + 层子包」结构，每个业务包只服务一个角色，内部再按 controller / service / mapper / entity / dto / vo 分层。

| 业务包 | 内容 | 职责 |
|---|---|---|
| `system.admin` | `AdminController`、`AdminService` + `AdminServiceImpl`、`AdminMapper`、`Admin`、4 个 DTO、2 个 VO | 管理员域 |
| `system.role` | `RoleController`、`RoleService` + `RoleServiceimpl`、`RoleMapper`、`Role`、2 个 DTO、1 个 VO | 角色域 |
| `system.operationlog` | `OperationLogController`、`OperationLogService` + `OperationLogServiceImpl`、`OperationLogMapper`、`OperationLog`、1 个 DTO | 操作日志域 |
| `system.aspect` | `OperationService` | 横切：操作日志切面，不归属任何业务域 |

三个 Mapper 接口分别对应三份 XML（`AdminMapper.xml`、`RoleMapper.xml`、`OperationLogMapper.xml`），写在 `resources` 的 `mapper` 目录下。

---

## 三、对外接口

### 管理员 `/api/admin`

| 方法 | 路径 | 说明 | 操作日志 |
|---|---|---|---|
| POST | `/login` | 登录，返回 token | - |
| POST | `/logout` | 退出登录 | - |
| GET | `/page` | 分页查询 | 有 |
| GET | `/{id}` | 按 ID 查询 | 有 |
| GET | `/getOne` | 多条件查询单个 | 有 |
| POST | `/add` | 新增 | 有 |
| PUT | `/{id}` | 修改 | 有 |
| DELETE | `/{id}` | 删除 | 有 |

### 角色 `/api/role`

`GET /page`、`GET /{id}`、`GET /getOne`、`POST /add`、`PUT /{id}`、`DELETE /{id}`

这 6 个接口**还没加 `@OperationRecord`**，目前不会产生审计日志。

### 操作日志 `/api/operation-log`

`GET /page`：按管理员、操作类型、时间范围分页查询

---

## 四、两个关键机制

### 1. 登录流程

```
POST /api/admin/login
  → AdminServiceImpl.login()        校验账号存在、BCrypt 比对密码
  → StpUtil.login(adminId)          Sa-Token 签发 token
  → 组装 AdminLoginVO（含 token 信息）
  → 序列化成 JSON 写入 Redis          key = admin:online:{token}，TTL 与 token 一致
  → 返回 Result.success(AdminLoginVO)
```

在线管理员列表直接读 Redis 的 `admin:online:*`，不需要查库。日志里区分了「账号不存在」和「密码不匹配」两种失败原因，对外统一返回 401「用户名或密码错误」，不泄露账号是否存在。

序列化用的是 **Jackson 3 的 `JsonMapper`**（Boot 4 起 `ObjectMapper` 已不存在），通过构造器注入 Spring 自动配置的 Bean，而不是手动 `new`，以复用全局配置。

### 2. 操作日志（AOP）

```
请求进入带 @OperationRecord 的 Controller 方法
  → OperationService.around()          @Around 环绕通知
  → 记录开始时间，执行原方法（异常也照常向上抛）
  → finally 里 saveLog()               成功、失败都会记
  → 读取注解三个属性 + 请求 URI / 方法 / IP / User-Agent
  → 入参序列化（跳过文件与流参数、敏感字段脱敏、超长截断）
  → StpUtil.isLogin() 判断后取管理员 ID，未登录记 null
  → OperationLogServiceImpl.saveAsync()   @Async("logExecutor") 异步写库
```

四个设计要点：

- **日志写在 `finally` 里**。目标方法抛异常时同样会留痕——「删除失败」「修改报错」恰恰是审计最需要记录的。记日志本身再套一层 try-catch，保证不会覆盖业务异常。
- **入参脱敏**。`password`、`token`、`secret`、`idCard`、`bankCard` 等字段的值统一替换成 `***`，避免口令被明文写进审计表。
- **未登录不抛异常**。用 `StpUtil.isLogin()` 先判断，token 失效时 `adminId` 记 `null`，不会让整条日志丢失。
- **IP 取真实地址**。`X-Forwarded-For` 取第一段、补 `X-Real-IP` 回退，本机 IPv6 回环归一成 `127.0.0.1`。

**日志写入失败不会影响主流程**：切面里 try-catch 兜住，异步方法内部也自行记录，只打日志不抛异常。

---

## 五、依赖

### 直接依赖

| 依赖 | 用途 |
|---|---|
| `lingxin-common` | `Result` / 异常 / `ResultCode` / `@OperationRecord` |
| `lingxin-framework` | Sa-Token 拦截、BCrypt |
| `mybatis-plus-spring-boot4-starter` | Mapper 与 SQL |
| `spring-boot-starter-data-redis` | 在线管理员缓存 |
| `spring-boot-starter-aspectj` | AOP 切面 |
| `fastjson2` | 日志入参序列化（仅此一处用途） |
| `lombok` | 编译期，`optional` |

### 模块依赖

| 方向 | 模块 |
|---|---|
| 本模块依赖 | `lingxin-common`、`lingxin-framework` |
| 依赖本模块 | `lingxin-server` |
| 传递获得 | `spring-boot-starter-webmvc`、`spring-boot-starter-validation`、`sa-token-*`、`spring-boot-starter-security`、Jackson |

web、validation、Jackson 都是经 `lingxin-common` 传递进来的，Sa-Token 与 Security 经 `lingxin-framework` 传递，本模块不需要重复声明。

---

## 六、注意事项与待办

1. **`RoleServiceimpl` 类名大小写不规范**，建议改为 `RoleServiceImpl`，与另外两个 impl 一致。
2. **`fastjson2` 只用于日志参数序列化**，与 MVC 的 Jackson 3 互不干扰。如果想去掉这个依赖，可注入 `JsonMapper` 序列化参数。
3. **角色接口没有审计**。`RoleController` 的增删改都没加 `@OperationRecord`，改角色属于高权限操作，建议补上。
4. **角色只做了 CRUD**。`admin` 表里有 `role_id` 字段但未使用，「给管理员分配角色」「按角色鉴权」（`@SaCheckRole`）都还没有实现。
5. **`operation_log` 缺失败标记列**。目前日志只能看出「谁在什么时候调了什么接口、耗时多少」，区分不出成功还是失败，异常详情只写进了应用日志。要按成功/失败筛选，需要给表加 `status`（1 成功 / 0 失败）和 `error_msg` 两列，再在切面里赋值。
6. **登录信息写 Redis 这一步要防并发**。同一管理员多端登录时 `is-share: false`，每个 token 一条 key，清理逻辑要跟着 token 走，否则会留下过期残留。
