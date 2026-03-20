package com.accounting.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 修改收支记录请求DTO
 */
@Data
public class RecordUpdateDTO {

    /**
     * 记录ID
     */
    @NotNull(message = "记录ID不能为空")
    private Long id;

    /**
     * 金额
     * 必须大于0
     */
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    /**
     * 类型
     * INCOME-收入，EXPENSE-支出
     */
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "类型必须是INCOME(收入)或EXPENSE(支出)")
    private String type;

    /**
     * 分类
     * 餐饮/薪资/购物/交通/娱乐/医疗/教育/其他
     */
    @Pattern(regexp = "^(餐饮|薪资|购物|交通|娱乐|医疗|教育|其他)$", message = "分类必须是：餐饮、薪资、购物、交通、娱乐、医疗、教育、其他")
    private String category;

    /**
     * 备注
     */
    private String remark;
}
