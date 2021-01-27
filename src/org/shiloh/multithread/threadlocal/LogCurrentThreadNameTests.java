package org.shiloh.multithread.threadlocal;

/**
 * @author lxlei
 * @date 2021/1/27 15:12
 * @description 打印日志的同时打印出当前线程的名称
 */
public class LogCurrentThreadNameTests {

    public static void main(String[] args) {
        logSomething("start main thread...");
        new Thread(() -> {
            logSomething("run task...");
        }).start();
        logSomething("end main thread...");
    }

    public static void logSomething(String msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);

    }
}
