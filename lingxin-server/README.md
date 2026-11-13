# lingxin-server

**唯一可执行模块**：启动类、全部运行时配置、以及最终打包出来的可执行 jar。

其他 6 个模块都是库（普通 jar），只有本模块配置了 `spring-boot-maven-plugin`，把依赖全部打成一个 fat jar。

---

## 一、职责范围

| 职责 | 内容 |
|---|---|
| 应用启动 | `BsLingxinApplication` 启动类 |
| 全局配置 | `application.yml`，以及本地的 `application-dev.yml`（不提交） |
| 依赖聚合 | 声明依赖全部业务模块，决定哪些模块最终进入 jar |
| 打包出口 | 唯一产出可运行 jar 的模块 |

**这个模块不写业务代码**：没有 Controller、Service、Mapper、Entity。它只是个壳。

---

## 二、模块组成

| 组成 | 内容 |
|---|---|
| 启动类 | `BsLingxinApplication` |
| 主配置 | `application.yml`，提交仓库 |
| 本地配置模板 | `application-dev.yml.template`，复制成同目录下的 `application-dev.yml` 后填写 |
| 测试 | `BsLingxinApplicationTests` 的 `contextLoads` |
| 冗余文件 | `application.properties`，空文件，可删 |

---

## 三、启动类

```java
@EnableAsync
@SpringBootApplication
public class BsLingxinApplication {
    public static void main(String[] args) {
        SpringApplication.run(BsLingxinApplication.class, args);
    }
}
```

- **`@SpringBootApplication`** 位于根包 `org.example.bs_lingxin`，默认扫描范围覆盖 `common`、`framework`、`system`、`member`、`product`、`trade` 全部子包，因此**不需要写 `scanBasePackages`**。前提是所有模块的包名都必须以 `org.example.bs_lingxin` 开头。
- **`@EnableAsync`** 开启异步，供操作日志的 `@Async("logExecutor")` 使用。去掉它，日志会退化成同步写库。

---

## 四、application.yml 逐段说明

### 应用与 profile

```yaml
spring:
  profiles:
    active: dev
  application:
    name: bs_lingxin
```

`active: dev` 意味着启动时会加载 `application-dev.yml` 覆盖主配置。**这个文件不存在时，应用仍能启动，但没有任何数据源配置，一访问数据库就报错。**

### MyBatis-Plus

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: org.example.bs_lingxin.**.entity
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

| 配置 | 为什么必须这么写 |
|---|---|
| `classpath*:` | **星号不能少**。`classpath:` 只扫当前 jar，多模块下其他模块的 XML 全部加载不到，启动即报 `Invalid bound statement` |
| `**.entity` | 实体分散在 4 个模块的 `entity` 包下，必须用通配 |
| `logic-delete-field: isDeleted` | 全局逻辑删除字段名。实体里叫 `isDeleted` 即可自动生效，不用逐个加 `@TableLogic` |

### Sa-Token

```yaml
sa-token:
  token-name: lingxin
  token-style: simple-uuid
  timeout: 2592000
  is-concurrent: true
  is-share: false
  active-timeout: 1800
```

| 配置 | 值 | 含义 |
|---|---|---|
| `token-name` | `lingxin` | 前端请求头 key 是 `lingxin` |
| `token-style` | `simple-uuid` | token 生成格式（去掉连字符的 UUID） |
| `timeout` | 2592000 | 有效期 30 天 |
| `is-concurrent` | true | 允许同一账号多端同时登录 |
| `is-share` | false | 每次登录都签发新 token，不共用 |
| `active-timeout` | 1800 | 闲置 30 分钟失效，有请求则自动续期 |

> **历史坑**：这行的值原本是 `simple‑uuid`，中间是 U+2011 非断行连字符，Sa-Token 匹配不到任何内置样式，静默回退成默认 uuid。已修正为 ASCII 连字符。

---

## 五、本地启动步骤

1. 把 `application-dev.yml.template` 复制一份、改名为 `application-dev.yml`，两者放在同一个 `resources` 目录下。

2. `application-dev.yml` 至少需要：MySQL 连接、Redis 连接（Sa-Token 与在线管理员缓存都依赖 Redis）。

3. 启动方式二选一：

```bash
# IDEA：直接运行 BsLingxinApplication

# 命令行：先把 JAVA_HOME 指向 JDK 25 的安装目录
mvn -pl lingxin-server -am spring-boot:run
```

> 系统默认 `JAVA_HOME` 如果是 JDK 21，而项目 `maven.compiler.release=25`，不切换会直接编译失败。切换方式：IDE 里改项目 SDK，命令行里导出 `JAVA_HOME` 指向 JDK 25 根目录。

---

## 六、打包与产物

```bash
mvn clean package -DskipTests
```

产物是 `target` 目录下的 `bs_lingxin.jar`，约 50 MB，已包含 6 个业务 jar。

验证内容是否完整（在 `target` 目录下执行）：

```bash
unzip -l bs_lingxin.jar | grep "BOOT-INF/lib/lingxin-"
unzip -p bs_lingxin.jar META-INF/MANIFEST.MF | grep Start-Class
```

应当看到 6 个 `lingxin-*` jar，且 `Start-Class: org.example.bs_lingxin.BsLingxinApplication`。

运行：

```bash
java -jar bs_lingxin.jar --spring.profiles.active=prod
```

---

## 七、依赖

### 直接依赖

| 依赖 | 用途 |
|---|---|
| `lingxin-common` | 基础类型 |
| `lingxin-framework` | 鉴权、加密、线程池配置 |
| `lingxin-module-system` | 后台账号与日志 |
| `lingxin-module-member` | 用户与商家 |
| `lingxin-module-product` | 商品与 SKU |
| `lingxin-module-trade` | 订单与交易 |
| `mysql-connector-j` | JDBC 驱动，`runtime` |
| `spring-boot-starter-amqp` | RabbitMQ，已引入但暂未使用 |
| `spring-boot-starter-test`、`spring-rabbit-test` | `test` 作用域 |

### 模块依赖

| 方向 | 模块 |
|---|---|
| 本模块依赖 | 全部 6 个 `lingxin-*` 模块 |
| 依赖本模块 | 无（它是终点） |

**为什么六个模块都要显式声明**：`common` 虽然被其他模块传递引入，但仍要写——它提供 `Result`、异常这些直接使用的类型；业务模块则必须逐个声明，否则不会被打进 jar，启动时找不到类。

`trade` 补上对 `product` 的依赖后，本模块**不需要任何改动**：两个模块都已声明，Maven 会自动去重。

`spring-boot-maven-plugin` 只在本模块声明，`mainClass` 已显式指定。**其他模块绝不能加这个插件**，否则会把自己打成 fat jar，被依赖时类加载不到，报 `NoClassDefFoundError`。

---

## 八、新增模块时的三件事

1. 在**根 pom** 的 `<modules>` 中加一行（顺序放在 `lingxin-server` 之前）；
2. 在**本模块 pom** 中加依赖，否则不会被打进 jar，启动时找不到类；
3. 确保新模块的包名以 `org.example.bs_lingxin` 开头，否则组件扫描不到。

---

## 九、待办

- `application.properties` 是空文件，可删除避免混淆。
- 尚无 `application-prod.yml`，上线前需要补（数据源、Redis、Sa-Token 的 `is-concurrent`、日志级别）。
- `spring-boot-starter-amqp` 已引入但代码中无任何 `RabbitTemplate` / `@RabbitListener`，未使用时建议移除，减少启动开销与依赖体积。
- `application-dev.yml` 目前不存在，因此 `mvn test` 的 `contextLoads` 会因缺数据源失败，属预期现象。
- 如果确定用 Nginx 反向代理，需要补 `server.forward-headers-strategy`，否则 `getRemoteAddr()` 只能拿到 Nginx 的地址，操作日志里的 IP 会全是代理机。
