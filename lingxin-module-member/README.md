# lingxin-module-member

**账号主体域**：C 端普通消费者账号，以及入驻商家的店铺主体与收款账户。

把「用户」和「商家」放在同一个模块，是因为两者结构完全同构——都是一张主表加一张附属表，都只有登录、资料、状态维护，既没有复杂状态机，也不参与下单事务。

---

## 一、职责范围

| 子域 | 表 | 说明 |
|---|---|---|
| C 端用户 | `user` | 普通消费者账号：注册、登录、资料、收货地址 |
| 收货地址 | `user_address` | 一个用户多条地址，可设默认，物理删除 |
| 商家主体 | `merchant` | 入驻食品店铺，拥有独立登录入口，管理自己的商品与订单 |
| 收款账户 | `settlement_account` | 商家的银行卡 / 支付宝 / 微信收款信息，平台结算打款使用 |

**不负责**：后台员工账号（在 `lingxin-module-system`）、商品与库存（在 `lingxin-module-product`）、订单与售后（在 `lingxin-module-trade`）。

---

## 二、当前状态

**只有数据层，尚无业务层。** 已建好 Entity / DTO / VO 共 15 个类，但**没有 Controller、Service、Mapper、XML**，接口暂不可用。

| 业务包 | 实体 | DTO | VO |
|---|---|---|---|
| `member.user` | `User`（user）、`UserAddress`（user_address） | `UserLoginDTO` 登录、`UserRegisterDTO` 注册、`UserProfileDTO` 改资料、`UserPageQueryDTO` 后台分页 | `UserVO` 脱敏信息、`UserLoginVO` 含 token |
| `member.merchant` | `Merchant`（merchant）、`SettlementAccount`（settlement_account） | `MerchantLoginDTO` 商家登录、`MerchantSaveDTO` 新增/修改、`MerchantPageQueryDTO` 后台分页 | `MerchantVO` 商家信息、`MerchantLoginVO` 含 token |

---

## 三、规划中的能力

从已有 DTO / VO 可以反推出待实现的功能：

- **用户端**：注册、登录（签发 token）、查看与修改个人资料、收货地址增删改查与默认地址切换
- **商家端**：独立登录入口（与 C 端用户、后台管理员三套 loginType 隔离）、入驻资料维护、收款账户绑定
- **后台端**：分页查询用户列表、分页查询商家列表、禁用 / 启用账号

落地时需要补：4 个 Mapper 接口 + 4 份 XML、4 组 Service、3 个 Controller（user / merchant / address）。

---

## 四、依赖

### 直接依赖

| 依赖 | 用途 |
|---|---|
| `lingxin-common` | `Result` / 异常 / `BasePageQueryDTO` |
| `mybatis-plus-spring-boot4-starter` | 实体注解与 Mapper |
| `lombok` | 编译期，`optional` |

### 模块依赖

| 方向 | 模块 |
|---|---|
| 本模块依赖 | `lingxin-common` |
| 依赖本模块 | `lingxin-server` |
| 传递获得 | `spring-boot-starter-webmvc`、`spring-boot-starter-validation`、Jackson（经 common） |
| 明确不依赖 | `lingxin-module-product`、`lingxin-module-trade`、`lingxin-module-system` |

**两个待补项**：

1. **登录缺鉴权依赖**。本模块现在拿不到 `StpUtil`，实现登录时要先解决——加 `lingxin-framework` 依赖，或依赖 `lingxin-module-system` 暴露的 `LoginApi`。后一种更好，避免 Sa-Token 实现渗到每个模块。
2. **不依赖 product / trade 是刻意设计**。商家的商品数、订单数这类统计，应由对方模块提供查询接口，而不是在 member 里直接 join 对方的表。反向依赖同样禁止——`product`/`trade` 需要商家名称时，来这里要接口，不许绕过。

---

## 五、边界与后续演进

- **资金结算不在这里**。`settlement_account` 现在只是收款账户的登记信息。一旦开始做对账、结算单、打款流水，应单独拆出 `lingxin-module-finance`——资金逻辑的安全等级与 C 端登录完全不是一回事，不能混在同一模块。
- **权限不在这里**。商家后台如果要做子账号、店员权限，那属于 `lingxin-module-system` 的角色体系，不要往 member 里加 role 概念。
- 三套账号体系（admin / user / merchant）务必使用**不同的 Sa-Token loginType**，否则会出现「C 端用户 token 能访问后台接口」这类越权问题。
