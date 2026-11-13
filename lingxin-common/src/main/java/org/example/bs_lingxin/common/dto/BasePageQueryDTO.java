package org.example.bs_lingxin.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础DTO，所有分页查询继承
 */
@Data
public class BasePageQueryDTO implements Serializable {

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    @Min(value = 1, message = "页码不能小于1")
    @NotNull(message = "页码不能为空")
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 1000, message = "每页条数最大值为 1000")
    @NotNull(message = "每页条数不能为空")
    private Long size = 10L;
}
