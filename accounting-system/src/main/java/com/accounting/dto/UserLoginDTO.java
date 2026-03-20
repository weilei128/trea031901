package com.accounting.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名（手机号或邮箱）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
