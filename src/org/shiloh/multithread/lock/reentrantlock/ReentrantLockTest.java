package org.shiloh.multithread.lock.reentrantlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lxlei
 * @date 2021/1/27 9:01
 * @description 从java5开始引入了一个高级的处理并发的包java.util.concurrent，它提供了大量更高级的并发功能，能大大简化多线程程序的编写。
 * synchronized关键字加的锁有2个缺点：
 * 1.重
 * 2.获取时一直等待，没有额外的尝试机制
 * java.util.concurrent.locks包提供的ReentrantLock用于替代synchronized加锁。
 */
public class ReentrantLockTest {

}

class Counter01 {
    private int count;

    /**
     * 传统的synchronized加锁示例
     * @author lxlei
     * @date 2021/1/27 9:04
     */
    public void add(int val) {
        synchronized (this) {
            this.count += val;
        }
    }
}

/**
 * 使用ReentrantLock替代synchronized
 * @author lxlei
 * @date 2021/1/27 9:06
 */
class Counter02 {
    private final Lock lock = new ReentrantLock();

    private int count;

    /**
     * 与synchronized不同的是，ReentrantLock是java代码实现的锁，使用时必须先获取锁，然后在finally代码块中正确释放锁
     * ReentrantLock与synchronized一样都是可重入锁，一个线程可以多次获取同一个锁
     * 不一样的地方在于ReentrantLock是可以尝试去获取锁的：
     * if (lock.tryLock(1, TimeUnit.SECONDS)) {
     *     try {
     *         ...
     *     } finally {
     *         lock.unlock()
     *     }
     * }
     * 上述代码在尝试获取锁时，最多等待1秒，如果1秒后仍未获取到锁，tryLock()方法就会返回false，此时可进行一些额外处理，而不是无限等待下去。
     * 可以看出使用ReentrantLock比synchronized更安全，线程在tryLock()失败的时候不会发生死锁的问题。
     * @author lxlei
     * @date 2021/1/27 9:08
     */
    public void add(int val) {
        // 加锁
        lock.lock();
        try {
            this.count += val;
        } finally {
            // 解锁
            lock.unlock();
        }
    }
}



