package org.example.bs_lingxin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryPageQueryDTO extends BasePageQueryDTO implements Serializable {
    private Long parentId;
}
