package org.example.bs_lingxin.trade.cart.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 购物车项展示对象
 *
 * 数据方向：后端 → 前端。除购物车表本身字段外，其余为跨表字段，由 Service 关联 SKU / 商品 / 商家后填充。
 */
@Data
public class CartVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; //版本号

    private Long id;
    private Long userId;
    private Long skuId;
    private Integer quantity;
    private Integer selected; //1下单勾选 0未选中

    //跨表字段：SKU与商品信息
    private Long productId;
    private String productName;
    private Map<String, Object> specAttrs;
    //SKU展示图：优先取 SKU 图片数组第一张，没有则用商品主图
    private String image;
    private BigDecimal price;
    //活动价，无活动时为 null，前端可据此显示划线价
    private BigDecimal promotionPrice;
    private Integer stock;
    private Integer skuStatus; //1上架 0下架
    private Long merchantId;
    private String merchantName;

    //小计：成交单价 × 数量（成交价优先取 promotionPrice）
    private BigDecimal subtotal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
