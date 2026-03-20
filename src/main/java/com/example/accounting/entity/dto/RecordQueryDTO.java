package com.example.accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class RecordQueryDTO {
    private String startTime;
    private String endTime;

    @Pattern(regexp = "^(收入|支出)$", message = "类型只能是收入或支出")
    private String type;

    private String category;
    private Integer page = 1;
    private Integer pageSize = 10;
}
