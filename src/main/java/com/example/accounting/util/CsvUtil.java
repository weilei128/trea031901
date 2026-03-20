package com.example.accounting.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CsvUtil {

    @Value("${csv.data.path:data/}")
    private String dataPath;

    private static final String[] USER_HEADERS = {"id", "username", "password", "phone", "email", "createTime"};
    private static final String[] RECORD_HEADERS = {"id", "userId", "amount", "type", "category", "remark", "createTime"};

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(dataPath));
            createFileIfNotExists("users.csv", USER_HEADERS);
            createFileIfNotExists("records.csv", RECORD_HEADERS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFileIfNotExists(String fileName, String[] headers) throws IOException {
        Path filePath = Paths.get(dataPath + fileName);
        if (!Files.exists(filePath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
                csvPrinter.flush();
            }
        }
    }

    public void appendRecord(String fileName, Map<String, String> record) throws IOException {
        Path filePath = Paths.get(dataPath + fileName);
        String[] headers = getHeaders(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                values.add(record.getOrDefault(header, ""));
            }
            csvPrinter.printRecord(values);
            csvPrinter.flush();
        }
    }

    public List<CSVRecord> readAllRecords(String fileName) throws IOException {
        Path filePath = Paths.get(dataPath + fileName);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            return csvParser.getRecords();
        }
    }

    public void overwriteRecords(String fileName, List<Map<String, String>> records) throws IOException {
        Path filePath = Paths.get(dataPath + fileName);
        String[] headers = getHeaders(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            for (Map<String, String> record : records) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(record.getOrDefault(header, ""));
                }
                csvPrinter.printRecord(values);
            }
            csvPrinter.flush();
        }
    }

    private String[] getHeaders(String fileName) {
        if ("users.csv".equals(fileName)) {
            return USER_HEADERS;
        } else if ("records.csv".equals(fileName)) {
            return RECORD_HEADERS;
        }
        return new String[0];
    }

    public long getNextId(String fileName) throws IOException {
        List<CSVRecord> records = readAllRecords(fileName);
        if (records.isEmpty()) {
            return 1;
        }
        long maxId = 0;
        for (CSVRecord record : records) {
            long id = Long.parseLong(record.get("id"));
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }
}
