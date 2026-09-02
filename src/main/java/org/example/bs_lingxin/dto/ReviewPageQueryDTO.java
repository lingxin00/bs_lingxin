package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long skuId;
}
