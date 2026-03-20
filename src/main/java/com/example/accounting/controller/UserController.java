package com.example.accounting.controller;

import com.example.accounting.entity.Result;
import com.example.accounting.entity.User;
import com.example.accounting.entity.dto.ChangePasswordDTO;
import com.example.accounting.entity.dto.UserLoginDTO;
import com.example.accounting.entity.dto.UserRegisterDTO;
import com.example.accounting.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) throws IOException {
        if ((dto.getPhone() == null || dto.getPhone().isEmpty()) &&
                (dto.getEmail() == null || dto.getEmail().isEmpty())) {
            return Result.error("手机号或邮箱至少填写一个");
        }
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) throws IOException {
        String token = userService.login(dto);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, dto);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        return Result.success(user);
    }
}
