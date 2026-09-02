package org.example.bs_lingxin.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础DTO，所有分页查询继承
 */
@Data
public class BasePageQueryDTO implements Serializable {

    @Min(value = 1, message = "页码不能小于1")
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    private Long size = 10L;
}
