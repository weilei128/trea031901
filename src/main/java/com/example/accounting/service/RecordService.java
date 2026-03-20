package com.example.accounting.service;

import com.example.accounting.entity.Record;
import com.example.accounting.entity.dto.RecordAddDTO;
import com.example.accounting.entity.dto.RecordQueryDTO;
import com.example.accounting.exception.BusinessException;
import com.example.accounting.util.CsvUtil;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecordService {

    private final CsvUtil csvUtil;
    private static final String RECORD_FILE = "records.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<String> VALID_CATEGORIES = Arrays.asList(
            "餐饮", "薪资", "购物", "交通", "娱乐", "医疗", "教育", "住房", "其他收入", "其他支出"
    );

    public RecordService(CsvUtil csvUtil) {
        this.csvUtil = csvUtil;
    }

    public void addRecord(Long userId, RecordAddDTO dto) throws IOException {
        if (!VALID_CATEGORIES.contains(dto.getCategory())) {
            throw new BusinessException("无效的分类，有效分类为：" + String.join(", ", VALID_CATEGORIES));
        }

        long recordId = csvUtil.getNextId(RECORD_FILE);
        Map<String, String> recordMap = new HashMap<>();
        recordMap.put("id", String.valueOf(recordId));
        recordMap.put("userId", String.valueOf(userId));
        recordMap.put("amount", dto.getAmount().toString());
        recordMap.put("type", dto.getType());
        recordMap.put("category", dto.getCategory());
        recordMap.put("remark", dto.getRemark() != null ? dto.getRemark() : "");
        recordMap.put("createTime", LocalDateTime.now().format(FORMATTER));

        csvUtil.appendRecord(RECORD_FILE, recordMap);
    }

    public Map<String, Object> queryRecords(Long userId, RecordQueryDTO dto) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(RECORD_FILE);

        List<Record> filteredRecords = records.stream()
                .map(this::recordToEntity)
                .filter(r -> r.getUserId().equals(userId))
                .filter(r -> dto.getType() == null || dto.getType().isEmpty() || r.getType().equals(dto.getType()))
                .filter(r -> dto.getCategory() == null || dto.getCategory().isEmpty() || r.getCategory().equals(dto.getCategory()))
                .filter(r -> {
                    if (dto.getStartTime() == null || dto.getStartTime().isEmpty()) return true;
                    LocalDateTime startTime = LocalDateTime.parse(dto.getStartTime() + " 00:00:00", FORMATTER);
                    return !r.getCreateTime().isBefore(startTime);
                })
                .filter(r -> {
                    if (dto.getEndTime() == null || dto.getEndTime().isEmpty()) return true;
                    LocalDateTime endTime = LocalDateTime.parse(dto.getEndTime() + " 23:59:59", FORMATTER);
                    return !r.getCreateTime().isAfter(endTime);
                })
                .sorted(Comparator.comparing(Record::getCreateTime).reversed())
                .collect(Collectors.toList());

        int total = filteredRecords.size();
        int start = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(start + dto.getPageSize(), total);

        List<Record> pageRecords = filteredRecords.subList(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageRecords);
        result.put("total", total);
        result.put("page", dto.getPage());
        result.put("pageSize", dto.getPageSize());
        result.put("totalPages", (total + dto.getPageSize() - 1) / dto.getPageSize());

        return result;
    }

    public void updateRecord(Long userId, Long recordId, RecordAddDTO dto) throws IOException {
        if (!VALID_CATEGORIES.contains(dto.getCategory())) {
            throw new BusinessException("无效的分类，有效分类为：" + String.join(", ", VALID_CATEGORIES));
        }

        List<CSVRecord> records = csvUtil.readAllRecords(RECORD_FILE);
        List<Map<String, String>> recordList = records.stream()
                .map(this::recordToMap)
                .collect(Collectors.toList());

        boolean recordFound = false;
        for (Map<String, String> recordMap : recordList) {
            if (String.valueOf(recordId).equals(recordMap.get("id"))) {
                recordFound = true;
                if (!String.valueOf(userId).equals(recordMap.get("userId"))) {
                    throw new BusinessException(403, "无权限修改该记录");
                }
                recordMap.put("amount", dto.getAmount().toString());
                recordMap.put("type", dto.getType());
                recordMap.put("category", dto.getCategory());
                recordMap.put("remark", dto.getRemark() != null ? dto.getRemark() : "");
                break;
            }
        }

        if (!recordFound) {
            throw new BusinessException("记录不存在");
        }

        csvUtil.overwriteRecords(RECORD_FILE, recordList);
    }

    public void deleteRecord(Long userId, Long recordId) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(RECORD_FILE);
        List<Map<String, String>> recordList = records.stream()
                .map(this::recordToMap)
                .collect(Collectors.toList());

        boolean recordFound = false;
        Iterator<Map<String, String>> iterator = recordList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> recordMap = iterator.next();
            if (String.valueOf(recordId).equals(recordMap.get("id"))) {
                recordFound = true;
                if (!String.valueOf(userId).equals(recordMap.get("userId"))) {
                    throw new BusinessException(403, "无权限删除该记录");
                }
                iterator.remove();
                break;
            }
        }

        if (!recordFound) {
            throw new BusinessException("记录不存在");
        }

        csvUtil.overwriteRecords(RECORD_FILE, recordList);
    }

    public Record getRecordById(Long recordId) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(RECORD_FILE);
        for (CSVRecord record : records) {
            if (String.valueOf(recordId).equals(record.get("id"))) {
                return recordToEntity(record);
            }
        }
        return null;
    }

    public List<String> getValidCategories() {
        return VALID_CATEGORIES;
    }

    private Record recordToEntity(CSVRecord record) {
        Record r = new Record();
        r.setId(Long.parseLong(record.get("id")));
        r.setUserId(Long.parseLong(record.get("userId")));
        r.setAmount(new BigDecimal(record.get("amount")));
        r.setType(record.get("type"));
        r.setCategory(record.get("category"));
        r.setRemark(record.get("remark"));
        r.setCreateTime(LocalDateTime.parse(record.get("createTime"), FORMATTER));
        return r;
    }

    private Map<String, String> recordToMap(CSVRecord record) {
        Map<String, String> map = new HashMap<>();
        map.put("id", record.get("id"));
        map.put("userId", record.get("userId"));
        map.put("amount", record.get("amount"));
        map.put("type", record.get("type"));
        map.put("category", record.get("category"));
        map.put("remark", record.get("remark"));
        map.put("createTime", record.get("createTime"));
        return map;
    }

    protected List<Record> getAllUserRecords(Long userId) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(RECORD_FILE);
        return records.stream()
                .map(this::recordToEntity)
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
