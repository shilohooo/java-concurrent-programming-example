package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 11:47
 * @description 线程同步
 */
public class ThreadSyncTest {

    public static void main(String[] args) throws InterruptedException {
        final var addThread = new AddThread();
        final var decThread = new DecThread();
        addThread.start();
        decThread.start();
        addThread.join();
        decThread.join();
        System.out.println(Counter.count);
    }
}

class Counter {

    public static final Object LOCK = new Object();

    public static int count = 0;
}

class AddThread extends Thread {

    /**
     * 使用synchronized关键字解决了多线程同步访问共享变量的正确性问题，但是它的缺点是会影响程序的性能。
     * 因为synchronized代码块无法并发执行，此外加锁和解锁都需要消耗一定的时间，所以synchronized会降低程序的执行效率。
     * 使用synchronized关键字的步骤：
     * 1.找出修改共享变量的线程代码块
     * 2.选择一个共享实例作为锁（共享实例必须为同一个对象）
     * 3.使用synchronized (lockObject) {...（修改共享变量的线程代码块）}
     * 注意：如果代码执行间不存在竞争则不需要使用同共享实例作为锁，否则会影响效率
     *
     * 在使用synchronized的时候，无论是否发生异常都可以在结束除正确释放锁
     *
     * @author lxlei
     * @date 2021/1/26 11:58
     */
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            // 加锁保证变量一致性
            // 获取锁
            // 这里表示使用Counter.LOCK实例作为锁，2个线程在执行各自的synchronized (Counter.LOCK) {...}代码块时
            // 必须先获得锁，才能进入代码块。执行结束后，在synchronized语句块结束后会自动释放锁。
            // 这样一来，对Counter.count变量进行读写就不可能同步进行
            synchronized (Counter.LOCK) {
                Counter.count += 1;
            }
            // 释放锁~~
        }
    }
}

class DecThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            // 加锁保证变量的一致性
            // 等其他线程释放锁后，再获取锁
            synchronized (Counter.LOCK) {
                Counter.count -= 1;
            }
            // 释放锁
        }
    }
}
