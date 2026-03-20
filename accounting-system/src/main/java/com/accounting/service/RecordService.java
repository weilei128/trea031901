package com.accounting.service;

import com.accounting.dto.PageResult;
import com.accounting.dto.RecordAddDTO;
import com.accounting.dto.RecordQueryDTO;
import com.accounting.dto.RecordUpdateDTO;
import com.accounting.entity.Record;
import com.accounting.exception.BusinessException;
import com.accounting.util.CsvUtil;
import com.accounting.util.DateUtil;
import com.accounting.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 记账服务类
 * 处理收支记录相关的业务逻辑
 */
@Slf4j
@Service
public class RecordService {

    /** 数据文件路径 */
    @Value("${data.path:./data}")
    private String dataPath;

    private String recordFilePath;

    @PostConstruct
    public void init() {
        recordFilePath = dataPath + "/records.csv";
    }

    /**
     * 添加收支记录
     * @param userId 用户ID
     * @param dto 添加请求DTO
     * @return 新记录ID
     */
    public Long addRecord(Long userId, RecordAddDTO dto) {
        Record record = new Record();
        record.setId(IdUtil.generateId());
        record.setUserId(userId);
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
        String now = DateUtil.now();
        record.setCreateTime(now);
        record.setUpdateTime(now);

        // 保存到CSV
        saveRecord(record);

        log.info("添加收支记录成功: userId={}, recordId={}", userId, record.getId());
        return record.getId();
    }

    /**
     * 修改收支记录
     * @param userId 用户ID
     * @param dto 修改请求DTO
     */
    public void updateRecord(Long userId, RecordUpdateDTO dto) {
        // 查找记录
        Record record = findById(dto.getId());
        if (record == null) {
            throw new BusinessException("记录不存在");
        }

        // 验证权限
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此记录");
        }

        // 更新字段
        if (dto.getAmount() != null) {
            record.setAmount(dto.getAmount());
        }
        if (dto.getType() != null) {
            record.setType(dto.getType());
        }
        if (dto.getCategory() != null) {
            record.setCategory(dto.getCategory());
        }
        if (dto.getRemark() != null) {
            record.setRemark(dto.getRemark());
        }
        record.setUpdateTime(DateUtil.now());

        // 保存到CSV
        updateRecordInFile(record);

        log.info("修改收支记录成功: userId={}, recordId={}", userId, record.getId());
    }

    /**
     * 删除收支记录
     * @param userId 用户ID
     * @param recordId 记录ID
     */
    public void deleteRecord(Long userId, Long recordId) {
        // 查找记录
        Record record = findById(recordId);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }

        // 验证权限
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此记录");
        }

        // 从CSV中删除
        deleteRecordFromFile(recordId);

        log.info("删除收支记录成功: userId={}, recordId={}", userId, recordId);
    }

    /**
     * 查询收支记录列表（分页）
     * @param userId 用户ID
     * @param dto 查询请求DTO
     * @return 分页结果
     */
    public PageResult<Record> queryRecords(Long userId, RecordQueryDTO dto) {
        // 读取所有记录
        List<Record> allRecords = readAllRecords();

        // 过滤当前用户的记录
        List<Record> userRecords = allRecords.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());

        // 按条件筛选
        if (dto.getStartTime() != null && !dto.getStartTime().isEmpty()) {
            LocalDate startDate = DateUtil.parseDate(dto.getStartTime());
            userRecords = userRecords.stream()
                    .filter(r -> {
                        LocalDate recordDate = LocalDate.parse(r.getCreateTime().substring(0, 10));
                        return !recordDate.isBefore(startDate);
                    })
                    .collect(Collectors.toList());
        }

        if (dto.getEndTime() != null && !dto.getEndTime().isEmpty()) {
            LocalDate endDate = DateUtil.parseDate(dto.getEndTime());
            userRecords = userRecords.stream()
                    .filter(r -> {
                        LocalDate recordDate = LocalDate.parse(r.getCreateTime().substring(0, 10));
                        return !recordDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        }

        if (dto.getType() != null && !dto.getType().isEmpty()) {
            userRecords = userRecords.stream()
                    .filter(r -> r.getType().equals(dto.getType()))
                    .collect(Collectors.toList());
        }

        if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
            userRecords = userRecords.stream()
                    .filter(r -> r.getCategory().equals(dto.getCategory()))
                    .collect(Collectors.toList());
        }

        // 按创建时间倒序排序
        userRecords.sort((r1, r2) -> r2.getCreateTime().compareTo(r1.getCreateTime()));

        // 分页
        int total = userRecords.size();
        int pageNum = dto.getPageNum();
        int pageSize = dto.getPageSize();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Record> pageList = start < total ? userRecords.subList(start, end) : new java.util.ArrayList<>();

        PageResult<Record> result = new PageResult<>();
        result.setTotal((long) total);
        result.setList(pageList);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));

        return result;
    }

    /**
     * 根据ID查找记录
     * @param id 记录ID
     * @return 记录对象
     */
    public Record findById(Long id) {
        List<Record> records = readAllRecords();
        return records.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 按周统计收支
     * @param userId 用户ID
     * @param date 指定日期
     * @return 统计结果
     */
    public Map<String, Object> statisticsByWeek(Long userId, String date) {
        LocalDate targetDate = date != null ? DateUtil.parseDate(date) : LocalDate.now();
        LocalDate weekStart = DateUtil.getWeekStart(targetDate);
        LocalDate weekEnd = DateUtil.getWeekEnd(targetDate);

        return calculateStatistics(userId, weekStart, weekEnd);
    }

    /**
     * 按月统计收支
     * @param userId 用户ID
     * @param month 指定月份（格式：yyyy-MM）
     * @return 统计结果
     */
    public Map<String, Object> statisticsByMonth(Long userId, String month) {
        LocalDate targetDate;
        if (month != null && !month.isEmpty()) {
            targetDate = LocalDate.parse(month + "-01", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            targetDate = LocalDate.now();
        }

        LocalDate monthStart = DateUtil.getMonthStart(targetDate);
        LocalDate monthEnd = DateUtil.getMonthEnd(targetDate);

        return calculateStatistics(userId, monthStart, monthEnd);
    }

    /**
     * 计算统计数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    private Map<String, Object> calculateStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Record> records = readAllRecords().stream()
                .filter(r -> r.getUserId().equals(userId))
                .filter(r -> {
                    LocalDate recordDate = LocalDate.parse(r.getCreateTime().substring(0, 10));
                    return DateUtil.isBetween(recordDate, startDate, endDate);
                })
                .collect(Collectors.toList());

        // 计算总收入和总支出
        BigDecimal totalIncome = records.stream()
                .filter(r -> "INCOME".equals(r.getType()))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = records.stream()
                .filter(r -> "EXPENSE".equals(r.getType()))
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按分类统计
        Map<String, BigDecimal> incomeByCategory = records.stream()
                .filter(r -> "INCOME".equals(r.getType()))
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Record::getAmount, BigDecimal::add)
                ));

        Map<String, BigDecimal> expenseByCategory = records.stream()
                .filter(r -> "EXPENSE".equals(r.getType()))
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Record::getAmount, BigDecimal::add)
                ));

        // 计算占比
        Map<String, Object> incomePercent = new java.util.HashMap<>();
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : incomeByCategory.entrySet()) {
                double percent = entry.getValue().multiply(new BigDecimal("100"))
                        .divide(totalIncome, 2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("amount", entry.getValue());
                item.put("percent", percent);
                incomePercent.put(entry.getKey(), item);
            }
        }

        Map<String, Object> expensePercent = new java.util.HashMap<>();
        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : expenseByCategory.entrySet()) {
                double percent = entry.getValue().multiply(new BigDecimal("100"))
                        .divide(totalExpense, 2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("amount", entry.getValue());
                item.put("percent", percent);
                expensePercent.put(entry.getKey(), item);
            }
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("startDate", DateUtil.format(startDate));
        result.put("endDate", DateUtil.format(endDate));
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        result.put("incomeByCategory", incomePercent);
        result.put("expenseByCategory", expensePercent);

        return result;
    }

    /**
     * 读取所有记录
     * @return 记录列表
     */
    private List<Record> readAllRecords() {
        List<String[]> lines = CsvUtil.readAll(recordFilePath);
        if (lines.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 跳过表头
        return lines.stream()
                .skip(1)
                .map(this::parseRecord)
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    /**
     * 解析记录数据
     * @param data CSV行数据
     * @return 记录对象
     */
    private Record parseRecord(String[] data) {
        if (data.length < 8) {
            return null;
        }
        try {
            Record record = new Record();
            record.setId(Long.valueOf(data[0]));
            record.setUserId(Long.valueOf(data[1]));
            record.setAmount(new BigDecimal(data[2]));
            record.setType(data[3]);
            record.setCategory(data[4]);
            record.setRemark(data[5]);
            record.setCreateTime(data[6]);
            record.setUpdateTime(data[7]);
            return record;
        } catch (Exception e) {
            log.error("解析记录数据失败: {}", String.join(",", data), e);
            return null;
        }
    }

    /**
     * 保存记录（新增）
     * @param record 记录对象
     */
    private void saveRecord(Record record) {
        java.io.File file = new java.io.File(recordFilePath);

        // 如果文件不存在，先写入表头
        if (!file.exists()) {
            CsvUtil.append(recordFilePath, new String[]{
                    "id", "userId", "amount", "type", "category", "remark", "createTime", "updateTime"
            });
        }

        CsvUtil.append(recordFilePath, new String[]{
                String.valueOf(record.getId()),
                String.valueOf(record.getUserId()),
                record.getAmount().toString(),
                record.getType(),
                record.getCategory(),
                record.getRemark(),
                record.getCreateTime(),
                record.getUpdateTime()
        });
    }

    /**
     * 更新记录
     * @param record 记录对象
     */
    private void updateRecordInFile(Record record) {
        List<Record> records = readAllRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(record.getId())) {
                records.set(i, record);
                break;
            }
        }
        writeAllRecords(records);
    }

    /**
     * 从文件中删除记录
     * @param recordId 记录ID
     */
    private void deleteRecordFromFile(Long recordId) {
        List<Record> records = readAllRecords();
        records.removeIf(r -> r.getId().equals(recordId));
        writeAllRecords(records);
    }

    /**
     * 写入所有记录
     * @param records 记录列表
     */
    private void writeAllRecords(List<Record> records) {
        List<String[]> dataList = records.stream()
                .map(r -> new String[]{
                        String.valueOf(r.getId()),
                        String.valueOf(r.getUserId()),
                        r.getAmount().toString(),
                        r.getType(),
                        r.getCategory(),
                        r.getRemark(),
                        r.getCreateTime(),
                        r.getUpdateTime()
                })
                .collect(Collectors.toList());

        CsvUtil.writeWithHeader(recordFilePath,
                new String[]{"id", "userId", "amount", "type", "category", "remark", "createTime", "updateTime"},
                dataList);
    }
}
