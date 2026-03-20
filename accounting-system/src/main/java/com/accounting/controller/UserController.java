package com.accounting.controller;

import com.accounting.dto.Result;
import com.accounting.dto.UserLoginDTO;
import com.accounting.dto.UserRegisterDTO;
import com.accounting.dto.UserUpdatePasswordDTO;
import com.accounting.entity.User;
import com.accounting.service.UserService;
import com.accounting.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param dto 注册请求DTO
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Validated @RequestBody UserRegisterDTO dto) {
        Long userId = userService.register(dto);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);

        return Result.success(data);
    }

    /**
     * 用户登录
     * @param dto 登录请求DTO
     * @return 登录结果（包含Token）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody UserLoginDTO dto) {
        User user = userService.login(dto);

        // 生成JWT令牌
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("token", token);

        return Result.success(data);
    }

    /**
     * 修改密码
     * @param dto 修改密码请求DTO
     * @param request HTTP请求
     * @return 修改结果
     */
    @PostMapping("/update-password")
    public Result<Void> updatePassword(@Validated @RequestBody UserUpdatePasswordDTO dto,
                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        userService.updatePassword(userId, dto);
        return Result.success();
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
