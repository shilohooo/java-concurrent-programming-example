package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 11:37
 * @description 守护线程
 * 守护线程是指为其他线程服务的线程。在JVM中，所有非守护线程都执行完毕后，无论有没有守护线程，虚拟机都会自动退出。
 */
public class DaemonThread {

    /**
     * 守护线程是为其他线程服务的线程，主要作用是用来关闭一些无限循环运行的线程，例如一个定时触发任务的线程
     * 在所有非守护线程执行完毕后，无论是否有守护线程，JVM都会自动退出
     *
     * @author lxlei
     * @date 2021/1/26 11:40
     */
    public static void main(String[] args) {
        System.out.println("main start");
        final var daemonThread = new Thread(() -> {
            System.out.println("俺是守护线程");
        });
        // 启动守护线程需要在调用start()方法前调用setDaemon(true)来把该线程标记为一个守护线程
        daemonThread.setDaemon(true);
        daemonThread.start();
        // 守护线程不能持有任何需要关闭的资源，例如流，因为虚拟机推出时守护线程没有任何机会去关闭这些资源，这会导致数据丢失
        System.out.println("main end");
    }
}
