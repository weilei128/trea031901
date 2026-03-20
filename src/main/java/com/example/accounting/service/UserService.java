package com.example.accounting.service;

import com.example.accounting.entity.User;
import com.example.accounting.entity.dto.ChangePasswordDTO;
import com.example.accounting.entity.dto.UserLoginDTO;
import com.example.accounting.entity.dto.UserRegisterDTO;
import com.example.accounting.exception.BusinessException;
import com.example.accounting.util.CsvUtil;
import com.example.accounting.util.JwtUtil;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final CsvUtil csvUtil;
    private final JwtUtil jwtUtil;
    private static final String USER_FILE = "users.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserService(CsvUtil csvUtil, JwtUtil jwtUtil) {
        this.csvUtil = csvUtil;
        this.jwtUtil = jwtUtil;
    }

    public void register(UserRegisterDTO dto) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(USER_FILE);

        for (CSVRecord record : records) {
            if (record.get("username").equals(dto.getUsername())) {
                throw new BusinessException("用户名已存在");
            }
            if (dto.getPhone() != null && !dto.getPhone().isEmpty() && dto.getPhone().equals(record.get("phone"))) {
                throw new BusinessException("手机号已被注册");
            }
            if (dto.getEmail() != null && !dto.getEmail().isEmpty() && dto.getEmail().equals(record.get("email"))) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        long userId = csvUtil.getNextId(USER_FILE);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("id", String.valueOf(userId));
        userMap.put("username", dto.getUsername());
        userMap.put("password", encryptPassword(dto.getPassword()));
        userMap.put("phone", dto.getPhone() != null ? dto.getPhone() : "");
        userMap.put("email", dto.getEmail() != null ? dto.getEmail() : "");
        userMap.put("createTime", LocalDateTime.now().format(FORMATTER));

        csvUtil.appendRecord(USER_FILE, userMap);
    }

    public String login(UserLoginDTO dto) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(USER_FILE);
        String encryptedPassword = encryptPassword(dto.getPassword());

        for (CSVRecord record : records) {
            String username = record.get("username");
            String phone = record.get("phone");
            String email = record.get("email");
            String password = record.get("password");

            if ((dto.getAccount().equals(username) || dto.getAccount().equals(phone) || dto.getAccount().equals(email))
                    && encryptedPassword.equals(password)) {
                return jwtUtil.generateToken(Long.parseLong(record.get("id")));
            }
        }

        throw new BusinessException("账号或密码错误");
    }

    public void changePassword(Long userId, ChangePasswordDTO dto) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(USER_FILE);
        List<Map<String, String>> userList = records.stream()
                .map(this::recordToMap)
                .toList();

        boolean userFound = false;
        for (Map<String, String> userMap : userList) {
            if (String.valueOf(userId).equals(userMap.get("id"))) {
                userFound = true;
                String oldEncrypted = encryptPassword(dto.getOldPassword());
                if (!oldEncrypted.equals(userMap.get("password"))) {
                    throw new BusinessException("原密码错误");
                }
                userMap.put("password", encryptPassword(dto.getNewPassword()));
                break;
            }
        }

        if (!userFound) {
            throw new BusinessException("用户不存在");
        }

        csvUtil.overwriteRecords(USER_FILE, userList);
    }

    public User getUserById(Long userId) throws IOException {
        List<CSVRecord> records = csvUtil.readAllRecords(USER_FILE);
        for (CSVRecord record : records) {
            if (String.valueOf(userId).equals(record.get("id"))) {
                User user = new User();
                user.setId(Long.parseLong(record.get("id")));
                user.setUsername(record.get("username"));
                user.setPhone(record.get("phone"));
                user.setEmail(record.get("email"));
                user.setCreateTime(LocalDateTime.parse(record.get("createTime"), FORMATTER));
                return user;
            }
        }
        return null;
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    private Map<String, String> recordToMap(CSVRecord record) {
        Map<String, String> map = new HashMap<>();
        map.put("id", record.get("id"));
        map.put("username", record.get("username"));
        map.put("password", record.get("password"));
        map.put("phone", record.get("phone"));
        map.put("email", record.get("email"));
        map.put("createTime", record.get("createTime"));
        return map;
    }
}
