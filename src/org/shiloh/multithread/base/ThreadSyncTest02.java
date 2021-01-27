package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 12:04
 * @description 线程同步：错误示范1
 */
public class ThreadSyncTest02 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");
        final var myAddThread = new MyAddThread();
        final var myDecThread = new MyDecThread();
        myAddThread.start();
        myDecThread.start();
        myAddThread.join();
        myDecThread.join();
        // 每次得到的结果并不是预想的0，因为2个线程各自的synchronized锁住的不是同一个对象
        // 这使得2个线程各自都可以同时获得锁：因为JVM只保证同一个锁在任意时刻只能被一个线程获取
        // 但2个不同的锁在同一时刻可以被2个线程分别获取，这就造成了2个线程是并行的
        // 因此，使用synchronized的时候，获取到的是哪个锁非常重要，锁对象如果不对，代码逻辑就不对。
        System.out.println(MyCounter.count);
        System.out.println("main end");
    }
}

class MyCounter {

    public static final Object LOCK01 = new Object();

    public static final Object LOCK02 = new Object();

    public static int count = 0;
}

class MyAddThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (MyCounter.LOCK01) {
                MyCounter.count += 1;
            }
        }
    }
}

class MyDecThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (MyCounter.LOCK02) {
                MyCounter.count -= 1;
            }
        }
    }
}
