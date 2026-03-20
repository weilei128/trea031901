package com.accounting.dto;

import lombok.Data;

/**
 * 查询收支记录请求DTO
 */
@Data
public class RecordQueryDTO {

    /**
     * 开始时间（格式：yyyy-MM-dd）
     */
    private String startTime;

    /**
     * 结束时间（格式：yyyy-MM-dd）
     */
    private String endTime;

    /**
     * 类型：INCOME-收入，EXPENSE-支出
     */
    private String type;

    /**
     * 分类：餐饮/薪资/购物/交通/娱乐/医疗/教育/其他
     */
    private String category;

    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页大小，默认10
     */
    private Integer pageSize = 10;
}
