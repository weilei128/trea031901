package com.example.accounting.controller;

import com.example.accounting.entity.Result;
import com.example.accounting.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/week")
    public Result<Map<String, Object>> getWeekStatistics(HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = statisticsService.getWeekStatistics(userId);
        return Result.success(result);
    }

    @GetMapping("/month")
    public Result<Map<String, Object>> getMonthStatistics(HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = statisticsService.getMonthStatistics(userId);
        return Result.success(result);
    }

    @GetMapping("/custom")
    public Result<Map<String, Object>> getCustomPeriodStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = statisticsService.getCustomPeriodStatistics(userId, startDate, endDate);
        return Result.success(result);
    }

    @GetMapping("/category")
    public Result<Map<String, Object>> getCategoryStatistics(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = statisticsService.getCategoryStatistics(userId, type, startDate, endDate);
        return Result.success(result);
    }
}
