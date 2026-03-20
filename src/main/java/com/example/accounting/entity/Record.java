package com.example.accounting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String type;
    private String category;
    private String remark;
    private LocalDateTime createTime;
}
