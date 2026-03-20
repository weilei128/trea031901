package com.accounting.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV文件操作工具类
 * 提供CSV文件的读写功能
 */
@Slf4j
public class CsvUtil {

    /**
     * 读取CSV文件并转换为对象列表
     * @param filePath 文件路径
     * @param clazz 目标类
     * @param <T> 泛型类型
     * @return 对象列表
     */
    public static <T> List<T> read(String filePath, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            log.info("CSV文件不存在，返回空列表: {}", filePath);
            return list;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            list = csvToBean.parse();
        } catch (Exception e) {
            log.error("读取CSV文件失败: {}", filePath, e);
        }

        return list;
    }

    /**
     * 写入对象列表到CSV文件
     * @param filePath 文件路径
     * @param list 对象列表
     * @param clazz 目标类
     * @param <T> 泛型类型
     */
    public static <T> void write(String filePath, List<T> list, Class<T> clazz) {
        File file = new File(filePath);

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build();
            beanToCsv.write(list);
            log.info("写入CSV文件成功: {}", filePath);
        } catch (Exception e) {
            log.error("写入CSV文件失败: {}", filePath, e);
            throw new RuntimeException("写入CSV文件失败", e);
        }
    }

    /**
     * 追加数据到CSV文件（如果文件不存在则创建）
     * @param filePath 文件路径
     * @param data 数据数组
     */
    public static void append(String filePath, String[] data) {
        File file = new File(filePath);
        boolean fileExists = file.exists();

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.writeNext(data);
            log.info("追加数据到CSV文件成功: {}", filePath);
        } catch (Exception e) {
            log.error("追加数据到CSV文件失败: {}", filePath, e);
            throw new RuntimeException("追加数据到CSV文件失败", e);
        }
    }

    /**
     * 读取CSV文件所有行
     * @param filePath 文件路径
     * @return 所有行数据
     */
    public static List<String[]> readAll(String filePath) {
        List<String[]> list = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return list;
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            list = reader.readAll();
        } catch (Exception e) {
            log.error("读取CSV文件失败: {}", filePath, e);
        }

        return list;
    }

    /**
     * 写入CSV文件（带表头）
     * @param filePath 文件路径
     * @param headers 表头
     * @param dataList 数据列表
     */
    public static void writeWithHeader(String filePath, String[] headers, List<String[]> dataList) {
        File file = new File(filePath);

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.writeNext(headers);
            writer.writeAll(dataList);
            log.info("写入CSV文件成功: {}", filePath);
        } catch (Exception e) {
            log.error("写入CSV文件失败: {}", filePath, e);
            throw new RuntimeException("写入CSV文件失败", e);
        }
    }
}
