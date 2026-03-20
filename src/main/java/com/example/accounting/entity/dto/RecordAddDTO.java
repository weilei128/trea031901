package com.example.accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class RecordAddDTO {
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(收入|支出)$", message = "类型只能是收入或支出")
    private String type;

    @NotBlank(message = "分类不能为空")
    private String category;

    private String remark;
}
