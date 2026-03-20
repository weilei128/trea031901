package com.accounting.service;

import com.accounting.dto.UserLoginDTO;
import com.accounting.dto.UserRegisterDTO;
import com.accounting.dto.UserUpdatePasswordDTO;
import com.accounting.entity.User;
import com.accounting.exception.BusinessException;
import com.accounting.util.CsvUtil;
import com.accounting.util.DateUtil;
import com.accounting.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 处理用户相关的业务逻辑
 */
@Slf4j
@Service
public class UserService {

    /** 用户CSV文件路径 */
    @Value("${data.path:./data}")
    private String dataPath;

    private String userFilePath;

    /** 手机号正则 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /** 邮箱正则 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @PostConstruct
    public void init() {
        userFilePath = dataPath + "/users.csv";
    }

    /**
     * 用户注册
     * @param dto 注册请求DTO
     * @return 新用户ID
     */
    public Long register(UserRegisterDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        // 判断用户类型
        String userType;
        if (PHONE_PATTERN.matcher(username).matches()) {
            userType = "PHONE";
        } else if (EMAIL_PATTERN.matcher(username).matches()) {
            userType = "EMAIL";
        } else {
            throw new BusinessException("用户名必须是手机号或邮箱格式");
        }

        // 检查用户是否已存在
        List<User> users = readAllUsers();
        boolean exists = users.stream()
                .anyMatch(u -> u.getUsername().equals(username));
        if (exists) {
            throw new BusinessException("用户已存在");
        }

        // 创建新用户
        User user = new User();
        user.setId(IdUtil.generateId());
        user.setUsername(username);
        user.setPassword(password); // 实际项目中应该加密
        user.setUserType(userType);
        String now = DateUtil.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 保存到CSV
        saveUser(user);

        log.info("用户注册成功: {}", username);
        return user.getId();
    }

    /**
     * 用户登录
     * @param dto 登录请求DTO
     * @return 用户对象
     */
    public User login(UserLoginDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        // 查找用户
        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!user.getPassword().equals(password)) {
            throw new BusinessException("密码错误");
        }

        log.info("用户登录成功: {}", username);
        return user;
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param dto 修改密码请求DTO
     */
    public void updatePassword(Long userId, UserUpdatePasswordDTO dto) {
        // 查找用户
        User user = findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(dto.getNewPassword());
        user.setUpdateTime(DateUtil.now());

        // 保存到CSV
        updateUser(user);

        log.info("用户修改密码成功: {}", user.getUsername());
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    public User findByUsername(String username) {
        List<User> users = readAllUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    public User findById(Long id) {
        List<User> users = readAllUsers();
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 读取所有用户
     * @return 用户列表
     */
    private List<User> readAllUsers() {
        List<String[]> lines = CsvUtil.readAll(userFilePath);
        if (lines.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 跳过表头
        return lines.stream()
                .skip(1)
                .map(this::parseUser)
                .collect(Collectors.toList());
    }

    /**
     * 解析用户数据
     * @param data CSV行数据
     * @return 用户对象
     */
    private User parseUser(String[] data) {
        if (data.length < 6) {
            return null;
        }
        User user = new User();
        user.setId(Long.valueOf(data[0]));
        user.setUsername(data[1]);
        user.setPassword(data[2]);
        user.setUserType(data[3]);
        user.setCreateTime(data[4]);
        user.setUpdateTime(data[5]);
        return user;
    }

    /**
     * 保存用户（新增）
     * @param user 用户对象
     */
    private void saveUser(User user) {
        java.io.File file = new java.io.File(userFilePath);
        boolean fileExists = file.exists();

        // 如果文件不存在，先写入表头
        if (!fileExists) {
            CsvUtil.append(userFilePath, new String[]{"id", "username", "password", "userType", "createTime", "updateTime"});
        }

        CsvUtil.append(userFilePath, new String[]{
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getPassword(),
                user.getUserType(),
                user.getCreateTime(),
                user.getUpdateTime()
        });
    }

    /**
     * 更新用户
     * @param user 用户对象
     */
    private void updateUser(User user) {
        List<User> users = readAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        writeAllUsers(users);
    }

    /**
     * 写入所有用户
     * @param users 用户列表
     */
    private void writeAllUsers(List<User> users) {
        List<String[]> dataList = users.stream()
                .map(u -> new String[]{
                        String.valueOf(u.getId()),
                        u.getUsername(),
                        u.getPassword(),
                        u.getUserType(),
                        u.getCreateTime(),
                        u.getUpdateTime()
                })
                .collect(Collectors.toList());

        CsvUtil.writeWithHeader(userFilePath,
                new String[]{"id", "username", "password", "userType", "createTime", "updateTime"},
                dataList);
    }
}
