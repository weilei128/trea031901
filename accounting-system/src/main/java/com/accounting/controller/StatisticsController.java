package com.accounting.controller;

import com.accounting.dto.Result;
import com.accounting.dto.StatisticsDTO;
import com.accounting.service.RecordService;
import com.accounting.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 统计控制器
 * 处理收支统计相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private RecordService recordService;

    /**
     * 按周统计收支
     * @param dto 统计请求DTO
     * @param request HTTP请求
     * @return 统计结果
     */
    @PostMapping("/week")
    public Result<Map<String, Object>> statisticsByWeek(@RequestBody StatisticsDTO dto,
                                                        HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Map<String, Object> result = recordService.statisticsByWeek(userId, dto.getDate());
        return Result.success(result);
    }

    /**
     * 按月统计收支
     * @param dto 统计请求DTO
     * @param request HTTP请求
     * @return 统计结果
     */
    @PostMapping("/month")
    public Result<Map<String, Object>> statisticsByMonth(@RequestBody StatisticsDTO dto,
                                                         HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Map<String, Object> result = recordService.statisticsByMonth(userId, dto.getDate());
        return Result.success(result);
    }

    /**
     * 获取当前登录用户ID
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new com.accounting.exception.BusinessException(401, "用户未登录");
        }

        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new com.accounting.exception.BusinessException(401, "登录已过期，请重新登录");
        }

        return userId;
    }
}
