package com.accounting.dto;

import lombok.Data;

/**
 * 统一响应结果封装类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /** 响应码：200成功，其他为失败 */
    private Integer code;

    /** 响应消息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /**
     * 成功响应
     * @return Result对象
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        return result;
    }

    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     * @param msg 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败响应（自定义状态码）
     * @param code 状态码
     * @param msg 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
