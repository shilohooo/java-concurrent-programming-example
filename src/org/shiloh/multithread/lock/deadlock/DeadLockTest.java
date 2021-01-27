package org.shiloh.multithread.lock.deadlock;

import static org.shiloh.multithread.lock.deadlock.DeadLockTest.LOCK01;
import static org.shiloh.multithread.lock.deadlock.DeadLockTest.LOCK02;

/**
 * @author lxlei
 * @date 2021/1/26 14:31
 * @description 死锁
 */
public class DeadLockTest {

    static final Object LOCK01 = new Object();

    static final Object LOCK02 = new Object();

    /**
     * 在获取多个锁的时候，不同线程获取多个不同对象的锁可能导致死锁。对于下面的代码，线程1和线程2如果分别执行add()和dec()方法时：
     * <p>
     * 线程1：进入add()，获得lockA；
     * 线程2：进入dec()，获得lockB。
     * 随后：
     * <p>
     * 线程1：准备获得lockB，失败，等待中；
     * 线程2：准备获得lockA，失败，等待中。
     * 此时，两个线程各自持有不同的锁，然后各自试图获取对方手里的锁，造成了双方无限等待下去，这就是死锁。
     * 死锁发生后，没有任何机制能解除死锁，只能强制结束JVM进程。
     * 实际开发需注意线程获取锁的顺序要一致：即严格按照先获取LOCK01，再获取LOCK02的顺序，避免死锁问题。
     *
     * @author lxlei
     * @date 2021/1/26 14:48
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        final var calculator = new Calculator();
        final var thread01 = new Thread(() -> calculator.add(1));
        final var thread02 = new Thread(() -> calculator.dec(1));
        thread01.start();
        thread02.start();
        System.out.println("calculator.getValue() = " + calculator.getValue());
        System.out.println("calculator.getAnother() = " + calculator.getAnother());
        thread02.join();
        System.out.println("end");
    }

    public static void sleep1s() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Calculator {

    private int value;

    private int another;

    public void add(int val) {
        // 获取LOCK01的锁
        synchronized (LOCK01) {
            this.value += val;
            // 睡眠1S
            DeadLockTest.sleep1s();
            // 获取LOCK02的锁
            synchronized (LOCK02) {
                this.another += val;
            }
            // 释放LOCK02的锁
        }
        // 释放LOCK01的锁
    }

    public void dec(int val) {
        // 获取LOCK02的锁
        synchronized (LOCK02) {
            this.another -= val;
            // 睡眠1S
            DeadLockTest.sleep1s();
            // 获取LOCK01的锁
            synchronized (LOCK01) {
                this.value -= val;
            }
            // 释放LOCK01的锁
        }
        // 释放LOCK02的锁
    }

    public int getValue() {
        return value;
    }

    public int getAnother() {
        return another;
    }
}
