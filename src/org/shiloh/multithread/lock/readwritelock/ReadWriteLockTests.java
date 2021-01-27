package org.shiloh.multithread.lock.readwritelock;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lxlei
 * @date 2021/1/27 9:43
 * @description ReentrantLock保证了只有一个线程可以执行临界区的代码，这种保护机制有些时候有点过头，比如：
 * 在任何时刻只允许一个线程修改数据，是必须获取锁的，但是在读取数据时实际上允许多个线程同时进行。我们实际想要的是：
 *       读        写
 * 读    允许      不允许
 * 写    不允许    不允许
 * 使用ReadWriteLock可以解决上述问题，它保证“
 * 只允许一个线程写入（其他线程既不能写入也不能读取）
 * 没有写入时，多个线程允许同时读取（可以提供性能）
 *
 * 使用ReadWriteLock时，适用条件是同一个数据，有大量线程读取，但仅有少数线程修改。
 * 例如：一个论坛的帖子，回复可以看做写入操作，它是不频繁的，但是，浏览可以看做读取操作，是非常频繁的
 * 这种情况就可以使用ReadWriteLock。
 *
 * ReadWriteLock适合读取操作多写入操作少的场景
 */
public class ReadWriteLockTests {
}

class Counter {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 可读锁
     */
    private final Lock readLock = readWriteLock.readLock();

    /**
     * 可写锁
     */
    private final Lock writeLock = readWriteLock.writeLock();

    private final int[] counts = new int[10];

    public void increment(int index) {
        // 加可写锁
        writeLock.lock();
        try {
            // 写入数据
            counts[index] += 1;
        } finally {
            // 释放可写锁
            writeLock.unlock();
        }
    }

    public int[] getCounts() {
        // 加可读锁
        readLock.lock();
        try {
            // 返回数据
            return Arrays.copyOf(counts, counts.length);
        } finally {
            // 关闭可读锁
            readLock.unlock();
        }
    }
}
