package com.example.accounting.service;

import com.example.accounting.entity.Record;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final RecordService recordService;

    public StatisticsService(RecordService recordService) {
        this.recordService = recordService;
    }

    public Map<String, Object> getWeekStatistics(Long userId) throws IOException {
        List<Record> records = recordService.getAllUserRecords(userId);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return calculatePeriodStatistics(records, weekStart.atStartOfDay(), weekEnd.atTime(23, 59, 59));
    }

    public Map<String, Object> getMonthStatistics(Long userId) throws IOException {
        List<Record> records = recordService.getAllUserRecords(userId);
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());

        return calculatePeriodStatistics(records, monthStart.atStartOfDay(), monthEnd.atTime(23, 59, 59));
    }

    public Map<String, Object> getCustomPeriodStatistics(Long userId, String startDate, String endDate) throws IOException {
        List<Record> records = recordService.getAllUserRecords(userId);
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        return calculatePeriodStatistics(records, start, end);
    }

    public Map<String, Object> getCategoryStatistics(Long userId, String type, String startDate, String endDate) throws IOException {
        List<Record> records = recordService.getAllUserRecords(userId);

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate).atTime(23, 59, 59);
        }

        LocalDateTime finalStart = start;
        LocalDateTime finalEnd = end;
        List<Record> filteredRecords = records.stream()
                .filter(r -> type == null || type.isEmpty() || r.getType().equals(type))
                .filter(r -> finalStart == null || !r.getCreateTime().isBefore(finalStart))
                .filter(r -> finalEnd == null || !r.getCreateTime().isAfter(finalEnd))
                .collect(Collectors.toList());

        BigDecimal totalAmount = filteredRecords.stream()
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryAmounts = filteredRecords.stream()
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Record::getAmount, BigDecimal::add)
                ));

        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryAmounts.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("category", entry.getKey());
            stat.put("amount", entry.getValue());
            stat.put("percentage", totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().multiply(new BigDecimal("100")).divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO);
            categoryStats.add(stat);
        }

        categoryStats.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("type", type != null ? type : "全部");
        result.put("categoryStats", categoryStats);

        return result;
    }

    private Map<String, Object> calculatePeriodStatistics(List<Record> records, LocalDateTime start, LocalDateTime end) {
        List<Record> periodRecords = records.stream()
                .filter(r -> !r.getCreateTime().isBefore(start) && !r.getCreateTime().isAfter(end))
                .collect(Collectors.toList());

        BigDecimal totalIncome = periodRecords.stream()
                .filter(r -> "收入".equals(r.getType()))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = periodRecords.stream()
                .filter(r -> "支出".equals(r.getType()))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        Map<String, Object> result = new HashMap<>();
        result.put("startDate", start.toLocalDate().toString());
        result.put("endDate", end.toLocalDate().toString());
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", balance);
        result.put("recordCount", periodRecords.size());

        return result;
    }
}
