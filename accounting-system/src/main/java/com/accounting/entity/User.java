package com.accounting.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * 对应CSV文件中的用户数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 用户ID */
    private Long id;

    /** 用户名/手机号/邮箱 */
    private String username;

    /** 密码（加密存储） */
    private String password;

    /** 用户类型：PHONE-手机号，EMAIL-邮箱 */
    private String userType;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;
}
