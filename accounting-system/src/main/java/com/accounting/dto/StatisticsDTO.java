package com.accounting.dto;

import lombok.Data;

/**
 * 统计查询请求DTO
 */
@Data
public class StatisticsDTO {

    /**
     * 统计类型：WEEK-周统计，MONTH-月统计
     */
    private String type;

    /**
     * 指定日期（格式：yyyy-MM-dd），用于周统计
     * 或指定月份（格式：yyyy-MM），用于月统计
     */
    private String date;
}
