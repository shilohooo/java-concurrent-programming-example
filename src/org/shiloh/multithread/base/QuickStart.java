package org.shiloh.multithread.base;

import java.util.Scanner;

/**
 * @author lxlei
 * @date 2021/1/1 22:05
 * @description 多线程学习之入门案例1
 */
public class QuickStart {

    public static void main(String[] args) {
//        final var thread01 = new MyThread();
        // 启动新线程，start()方法会在内部自动调用实例的run()方法
//        thread01.start();
        // 启动多线程的方法02：
        // 在创建Thread实例时，传入一个Runnable实例
//        final var thread02 = new Thread(new MyRunnable());
//        thread02.start();
        // 此外，还可以用lambda语法进一步简写：
        final var thread03 = new Thread(() -> {
            System.out.println("start a new thread~");
            final var scanner = new Scanner(System.in);
            System.out.println("请输入您的姓名：\n");
            final var name = scanner.next();
            System.out.println("name = " + name);
        });
        thread03.start();
        System.out.println("我去干点别的");
    }

}

/**
 * 启动多线程的方法01：
 * 通过继承{@link Thread}类，然后重写{@link Thread#run()}方法来实现
 * 在重写{@link Thread#run()}时加入处理逻辑，然后调用{@link Thread#start()}来启动一个新的线程
 * @author lxlei
 * @date 2021/1/1 22:14
 */
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("start a new thread~");
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("start a new Thread~");
    }
}
