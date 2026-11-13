# lingxin-module-trade

**交易域**：从购物车到下单、按商家拆单、支付流水、收货评价、售后退款，覆盖一笔交易的全生命周期。

这是业务上最重的模块，7 张表、20 个类，也是唯一涉及**状态机**和**跨表事务**的模块。

---

## 一、职责范围

| 子域 | 表 | 说明 |
|---|---|---|
| 购物车 | `cart` | 用户未下单的选购项，同一用户同一 SKU 只有一条 |
| 主订单 | `main_order` | 用户一次下单的总单，含地址快照、应付金额、订单状态 |
| 次订单 | `sub_order` | 主订单按商家拆分，商家独立发货的载体，含物流信息 |
| 订单明细 | `order_item` | 次订单里每一件 SKU，含下单时的商品快照 |
| 支付流水 | `payment` | 支付与退款记录，禁止物理删除 |
| 评价 | `review` | 收货后评价，支持初评 + 多条追评 |
| 售后 | `return_order` | 仅退款 / 退货退款申请，含审核与退款时间 |

**不负责**：商品信息与库存（在 `lingxin-module-product`）、用户与商家主体（在 `lingxin-module-member`）。

---

## 二、当前状态

**只有数据层。** Entity / DTO / VO 已就绪（20 个类），但**没有 Controller、Service、Mapper、XML**。

| 业务包 | 实体 | DTO | VO |
|---|---|---|---|
| `trade.cart` | `Cart` | 暂无 | 暂无 |
| `trade.order` | `MainOrder`、`SubOrder`、`OrderItem` | `OrderCreateDTO`、`OrderPageQueryDTO` | `MainOrderVO`、`SubOrderVO`、`OrderItemVO` |
| `trade.payment` | `Payment` | 暂无 | 暂无 |
| `trade.aftersale` | `ReturnOrder` | `ReturnOrderCreateDTO`、`ReturnOrderAuditDTO`、`ReturnOrderPageQueryDTO` | `ReturnOrderVO` |
| `trade.review` | `Review` | `ReviewCreateDTO`、`ReviewAddReplyDTO`、`ReviewPageQueryDTO` | `ReviewVO` |

---

## 三、核心模型：主订单 → 次订单 → 明细

这是整个模块最重要的设计，理解它才能写对业务：

```
main_order（用户一次下单，1 条）
   │  按 merchant_id 拆分
   ├── sub_order（商家 A，1 条）── order_item（SKU 1、SKU 2…）
   └── sub_order（商家 B，1 条）── order_item（SKU 3…）
```

**为什么要拆**：一个购物车里可能有多家店的商品，但发货、物流、售后都是按商家独立进行的。如果只有一张订单表，商家 A 发货了、商家 B 没发，订单状态无法表达。

**两个快照字段**是下单时必须写入的：

| 字段 | 位置 | 为什么必须存 |
|---|---|---|
| `address_snapshot`（JSON） | `main_order` | 用户改地址或删地址后，历史订单的收货信息不能变 |
| `sku_snapshot`（JSON） | `order_item` | 商品改名、改价、下架后，订单里要保留当时的名称、规格、图片、价格 |

这是电商系统的铁律：**订单数据一旦生成，不能再依赖商品表和地址表的当前值**。

---

## 四、状态机（落地时必须先定清楚）

| 状态字段 | 典型取值 |
|---|---|
| `main_order.order_status` | 待付款 → 待发货 → 待收货 → 已完成 / 已取消 |
| `main_order.pay_status` | 未支付 / 已支付 / 已退款 |
| `sub_order.shipping_status` | 待发货 → 已发货 → 已签收 |
| `return_order.status` | 待审核 → 已同意 / 已拒绝 → 待收货 → 已退款 |

建议用枚举 + 状态流转校验方法（`canTransit(from, to)`）集中管理，不要在每个 Service 方法里散着写 `if (status == 1)`。

---

## 五、依赖

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
| **待补** | `lingxin-module-product`（下单必需） |
| 待补 | `lingxin-framework` 或 `LoginApi`（取当前用户） |

**当前 pom 缺少 `lingxin-module-product`**。下单时必须读 SKU 的价格和库存、并扣减库存，落地时先补上：

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>lingxin-module-product</artifactId>
    <version>${project.version}</version>
</dependency>
```

**依赖方向固定**：`trade → product` 允许，`product → trade` 绝对禁止。否则会出现循环依赖，Maven 直接构建失败。

依赖加进来之后，`server` 不需要改——它已经同时声明了 trade 与 product，Maven 会自动去重。

---

## 六、与其他模块的边界

| 要用的数据 | 正确做法 | 错误做法 |
|---|---|---|
| SKU 价格、库存 | 依赖 `lingxin-module-product` 的 `ProductApi` | 直接查 `product_sku` 表 |
| 商家名称 | 依赖 `lingxin-module-member` 的 `MerchantApi` | 在 trade 里 join `merchant` |
| 当前登录用户 | 依赖 `lingxin-module-system` 的 `LoginApi`，或加 framework 依赖用 `StpUtil` | 自己解析 token |

---

## 七、待实现清单（按依赖顺序）

1. Mapper + XML（7 个）
2. 购物车：加入 / 修改数量 / 删除 / 列表（需按 SKU 合并数量，考虑下架商品置灰）
3. 下单：校验库存 → 计算金额（活动价优先）→ 写主单 + 拆次单 + 写明细（**同一事务**）→ 扣减库存 → 清购物车
4. 支付：创建支付单、回调处理（**必须做幂等**，第三方会重复回调）、超时未支付自动取消
5. 发货与收货：商家发货填物流、用户确认收货（或超时自动确认）
6. 评价：初评 + 追评（`parent_review_id` 自关联）、图片上传、匿名
7. 售后：申请 → 审核 → 退货收货 → 退款（退款要联动 `payment` 写退款流水）
8. 对外 API：`TradeApi#getOrderByNo()`、`#countByMerchant()` 等，供其他模块调用

**最需要注意的两点**：

- **支付回调必须幂等**。同一笔回调可能重复到达，用 `payment_no` 唯一索引 + 状态判断拦住，否则会重复发货、重复加积分。
- **库存扣减要防超卖**。不要先 `select` 再 `update`，要用 `update product_sku set stock = stock - #{n} where id = ? and stock >= #{n}`，靠影响行数判断成功与否。
