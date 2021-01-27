package org.shiloh.multithread.atomic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lxlei
 * @date 2021/1/27 10:23
 * @description 原子操作封装类测试
 * Java的java.util.concurrent包除了提供底层锁、并发集合外，还提供了一组原子操作的封装类，它们位于java.util.concurrent.atomic包。
 * <p>
 * 以AtomicInteger为例，它提供的主要操作有：
 * 增加值并返回新值：int addAndGet(int delta)
 * 加1后返回新值：int incrementAndGet()
 * 获取当前值：int get()
 * 用CAS方式设置：int compareAndSet(int expect, int update)
 * Atomic类是通过无锁（lock-free）的方式实现的线程安全（thread-safe）访问。它的主要原理是利用了CAS：Compare and Set。
 * <p>
 * 假设自己通过CAS实现一个incrementAndGet()，可能代码是以下这样的：
 * public int incrementAndGet(AtomicInteger val) {
 * int prev;
 * int next;
 * do {
 * prev = val.get():
 * next = prev + 1;
 * } while (!val.compareAndSet(prev, next));
 * return next;
 * }
 * <p>
 * CAS是指，在这个操作中，如果AtomicInteger的当前值是prev，那么就更新为next，返回true。
 * 如果AtomicInteger的当前值不是prev，就什么也不干，返回false。
 * 通过CAS操作并配合do ... while循环，即使其他线程修改了AtomicInteger的值，最终的结果也是正确的。
 * 通常情况下，我们并不需要直接用do ... while循环调用compareAndSet实现复杂的并发操作，
 * 而是用incrementAndGet()这样的封装好的方法，因此，使用起来非常简单。
 * 在高度竞争的情况下，还可以使用Java 8提供的LongAdder和LongAccumulator。
 * <p>
 * 原子操作实现了无锁的线程安全，适用于计数器，累加器等。
 */
public class AtomicClassTests {


}

/**
 * 利用AtomicLong编写一个线程安全的全局唯一ID生成器
 *
 * @author lxlei
 * @date 2021/1/27 10:51
 */
class IdGenerator {

    private final AtomicLong atomicLong = new AtomicLong();

    public long getNextId() {
        return atomicLong.incrementAndGet();
    }
}
