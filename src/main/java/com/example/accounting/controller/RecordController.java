package com.example.accounting.controller;

import com.example.accounting.entity.Result;
import com.example.accounting.entity.Record;
import com.example.accounting.entity.dto.RecordAddDTO;
import com.example.accounting.entity.dto.RecordQueryDTO;
import com.example.accounting.service.RecordService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public Result<Void> addRecord(@Valid @RequestBody RecordAddDTO dto, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        recordService.addRecord(userId, dto);
        return Result.success();
    }

    @GetMapping
    public Result<Map<String, Object>> queryRecords(@Valid RecordQueryDTO dto, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = recordService.queryRecords(userId, dto);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @Valid @RequestBody RecordAddDTO dto, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        recordService.updateRecord(userId, id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        recordService.deleteRecord(userId, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Record> getRecordById(@PathVariable Long id, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Record record = recordService.getRecordById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            return Result.error(403, "无权限查看该记录");
        }
        return Result.success(record);
    }

    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        return Result.success(recordService.getValidCategories());
    }
}
