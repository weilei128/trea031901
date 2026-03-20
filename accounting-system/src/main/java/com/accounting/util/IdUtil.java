package com.accounting.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成工具类
 * 使用原子长整型生成唯一ID
 */
public class IdUtil {

    /** 原子计数器 */
    private static final AtomicLong counter = new AtomicLong(1);

    /**
     * 生成唯一ID
     * 基于当前时间戳和计数器
     * @return 唯一ID
     */
    public static Long generateId() {
        // 使用时间戳 + 计数器的方式生成ID
        long timestamp = System.currentTimeMillis();
        long count = counter.getAndIncrement();
        // 确保ID唯一性：时间戳左移16位 + 计数器低16位
        return (timestamp << 16) | (count & 0xFFFF);
    }
}
