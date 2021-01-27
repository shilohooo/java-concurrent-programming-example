package org.shiloh.multithread.threadlocal;

/**
 * @author lxlei
 * @date 2021/1/27 16:28
 * @description ThreadLocal测试，具体内容请看readme文档
 */
public class ThreadLocalTests {

    private static final ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) {
        try {
            // 设置一个值
            STRING_THREAD_LOCAL.set("shiloh");
        } finally {
            // 释放
            STRING_THREAD_LOCAL.remove();
        }
    }
}
