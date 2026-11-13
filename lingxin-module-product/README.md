# lingxin-module-product

**商品中心**：分类树、商品主信息、SKU 规格、商品图片，以及满减 / 折扣 / 秒杀类营销活动。

这是电商链路的源头模块——订单、购物车、库存扣减都以这里的 SKU 为基准。

---

## 一、职责范围

| 子域 | 表 | 说明 |
|---|---|---|
| 分类 | `category` | 树形分类（一级 / 二级），前台按分类展示商品 |
| 商品 | `product` | 商品公共信息，一个商品对应多条 SKU |
| 商品图片 | `product_image` | 多图，可设主图，物理删除 |
| **SKU** | `product_sku` | **核心表**：规格、价格、库存、生产日期、保质期、临期预警 |
| 活动 | `promotion` | 满减 / 折扣 / 秒杀 / 优惠券活动主表，规则存 JSON |
| 活动关联 | `product_promotion` | 活动与 SKU 的绑定，含活动价与限购数量 |

**不负责**：下单与订单流转（在 `lingxin-module-trade`）、商家主体信息（在 `lingxin-module-member`）。

---

## 二、当前状态

**只有数据层。** Entity / DTO / VO 已就绪（16 个类），但**没有 Controller、Service、Mapper、XML**。

| 业务包 | 实体 | DTO | VO |
|---|---|---|---|
| `product.product` | `Product`（product）、`ProductImage`（product_image）、`ProductSku`（product_sku） | `ProductSaveDTO`、`ProductPageQueryDTO`、`ProductSkuSaveDTO`、`ProductSkuPageQueryDTO` | `ProductVO`、`ProductSkuVO`、`ProductImageVO` |
| `product.category` | `Category`（category） | `CategorySaveDTO`、`CategoryPageQueryDTO` | `CategoryVO` |
| `product.promotion` | `Promotion`（promotion）、`ProductPromotion`（product_promotion） | 暂无 | 暂无 |

---

## 三、这个模块的设计重点

### 1. SKU 是食品业务的核心

`product_sku` 字段设计明显针对食品行业：

| 字段 | 用途 |
|---|---|
| `spec_attrs`（Map，JSON） | 规格属性，如 `{"weight":"500g","flavor":"原味"}` |
| `production_date` / `expiry_days` / `expiration_date` | 生产日期 + 保质期天数 + 到期日 |
| `warning_threshold` | 临期预警阈值（剩余天数），用于后台预警与前台打折 |
| `price` / `market_price` / `cost_price` | 售价 / 划线价 / 成本价 |

`spec_attrs` 和 `images` 是 JSON 字段，因此 `ProductSku` 用了 `@TableName(autoResultMap = true)` + `JacksonTypeHandler`，MyBatis-Plus 才能正确读出 `Map` 和 `List`。**改这个实体的字段类型时要注意保持 `autoResultMap`**，否则查出来是 null。

### 2. 为什么活动和商品在同一个模块

`promotion` 完全可以独立成模块，但这里没有拆。原因是：**活动价最终要参与 SKU 的价格计算**。如果拆开，商品详情页每次展示价格都要跨模块调用，下单时算价更是要跨模块拿规则。等秒杀、优惠券规则真的复杂到需要独立发版时再拆，那时是搬家而不是重写。

### 3. 价格的三层来源

展示价格时有优先级，落地时要统一口径：

```
活动价（product_promotion.promotion_price）
  > SKU 促销价（product_sku.promotion_price，is_promotion = 1）
  > SKU 常规售价（product_sku.price）
```

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
| 依赖本模块 | `lingxin-server`（`lingxin-module-trade` 待补） |
| 传递获得 | `spring-boot-starter-webmvc`、`spring-boot-starter-validation`、Jackson（经 common） |
| 明确禁止 | `lingxin-module-trade` |

**被 `lingxin-module-trade` 依赖是本模块的唯一下游**（下单要读 SKU 价格与库存）。反向绝对不允许：product 不能依赖 trade，否则 Maven 循环依赖直接构建失败。

`product` 表里有 `merchant_id`，但本模块**不 join `merchant` 表**。需要店铺名时，应由 `lingxin-module-member` 暴露 `MerchantApi#getName(id)`，本模块只依赖这个接口。

---

## 五、待实现清单

- Mapper + XML（6 个）
- 分类：树形查询、增删改（含子分类校验）
- 商品：分页、详情（含 SKU 列表与图片）、上下架、新增 / 修改（主表 + SKU + 图片一次性提交）
- SKU：库存增减与扣减（下单要用，需考虑并发，建议乐观锁或 `stock >= n` 条件更新）、临期预警查询
- 活动：活动 CRUD、按 SKU 查有效活动价
- 对外 API：`ProductApi#getSku(id)`、`#deductStock(skuId, count)`，供 trade 模块调用

**注意**：`ProductApi` 一旦被 trade 依赖，就成跨模块契约，改动要谨慎——加字段可以，改方法签名会让 trade 编译失败。
