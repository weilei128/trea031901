package com.accounting.controller;

import com.accounting.dto.*;
import com.accounting.entity.Record;
import com.accounting.service.RecordService;
import com.accounting.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 记账控制器
 * 处理收支记录相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * 添加收支记录
     * @param dto 添加请求DTO
     * @param request HTTP请求
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result<Map<String, Object>> addRecord(@Validated @RequestBody RecordAddDTO dto,
                                                  HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long recordId = recordService.addRecord(userId, dto);

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("recordId", recordId);

        return Result.success(data);
    }

    /**
     * 修改收支记录
     * @param dto 修改请求DTO
     * @param request HTTP请求
     * @return 修改结果
     */
    @PostMapping("/update")
    public Result<Void> updateRecord(@Validated @RequestBody RecordUpdateDTO dto,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        recordService.updateRecord(userId, dto);
        return Result.success();
    }

    /**
     * 删除收支记录
     * @param recordId 记录ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @PostMapping("/delete/{recordId}")
    public Result<Void> deleteRecord(@PathVariable Long recordId,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        recordService.deleteRecord(userId, recordId);
        return Result.success();
    }

    /**
     * 查询收支记录列表（分页）
     * @param dto 查询请求DTO
     * @param request HTTP请求
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<PageResult<Record>> queryRecords(@RequestBody RecordQueryDTO dto,
                                                   HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        PageResult<Record> result = recordService.queryRecords(userId, dto);
        return Result.success(result);
    }

    /**
     * 获取记录详情
     * @param recordId 记录ID
     * @param request HTTP请求
     * @return 记录详情
     */
    @GetMapping("/detail/{recordId}")
    public Result<Record> getRecordDetail(@PathVariable Long recordId,
                                          HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Record record = recordService.findById(recordId);

        if (record == null) {
            return Result.error("记录不存在");
        }

        if (!record.getUserId().equals(userId)) {
            return Result.error(403, "无权查看此记录");
        }

        return Result.success(record);
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
