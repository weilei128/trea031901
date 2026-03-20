package com.accounting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 收支记录实体类
 * 对应CSV文件中的记账数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    /** 记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 金额 */
    private BigDecimal amount;

    /** 类型：INCOME-收入，EXPENSE-支出 */
    private String type;

    /** 分类：餐饮/薪资/购物/交通/娱乐/医疗/教育/其他 */
    private String category;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;
}
