package com.accounting.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑错误
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private Integer code;

    /**
     * 构造业务异常
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
