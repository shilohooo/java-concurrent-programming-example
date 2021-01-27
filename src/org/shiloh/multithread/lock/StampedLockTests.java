package org.shiloh.multithread.lock;

import java.util.concurrent.locks.StampedLock;

/**
 * @author lxlei
 * @date 2021/1/27 9:54
 * @description ReadWriteLock可以解决多线程同时读，但只有一个线程能写的问题。
 * 但它有一个潜在问题：如果有线程正在读，写线程需要等待读线程释放锁后才能获取写锁，即在读取数据的过程中不允许写入
 * 这是一种悲观的读锁。
 * <p>
 * 如果要进一步的提升并发执行效率，可以使用java8引入的新的读写锁：StampedLock。
 * 与ReadWriteLock相比，StampedLock改进之处在于：读取数据的过程中也允许获取写锁然后执行写入操作！
 * 不过这样一来我们读取到的数据可能不一致，所以需要一点额外的代码来判断在读取数据的过程中是否有写入操作，
 * 这种读锁匙一种乐观锁。
 * <p>
 * 乐观锁的意思就是乐观地估计读的过程中大概率不会有写入，因此被称为乐观锁。
 * 反过来，悲观锁则是读的过程中拒绝有写入，也就是写入必须等待。
 * 显然乐观锁的并发效率更高，但一旦有小概率的写入导致读取的数据不一致，需要能检测出来，再读一遍就行。
 */
public class StampedLockTests {

}

class Point {

    private final StampedLock stampedLock = new StampedLock();

    private double x;

    private double y;

    /**
     * 与ReadWriteLock相比，写入的加锁方式是一样的
     *
     * @param deltaX
     * @param deltaY
     * @author lxlei
     * @date 2021/1/27 10:07
     */
    public void move(double deltaX, double deltaY) {
        // 获取写锁
        final var stamp = stampedLock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            // 释放写锁
            stampedLock.unlockWrite(stamp);
        }
    }

    /**
     * 首先通过tryOptimisticRead()方法获取一个乐观读锁，并返回锁的版本号。接着进行数据读取
     * 读取完成后通过validate()方法去验证版本号，如果在读取过程中没有写入操作发生，版本号不变，验证成功，说明获取到的数据是正确的。
     * 如果在读取过程中发生了写入操作，那么版本号就会发生变化，验证将失败。在验证失败时，再通过获取悲观读锁去再次读取数据。
     * 由于写入的概率不高，程序在绝大部分情况下可以通过乐观读锁获取数据，极少数情况下使用悲观读锁获取数据。
     * <p>
     * 可见，StampedLock把读锁细分为乐观读和悲观读，能进一步提升并发效率。
     * 但这也是有代价的：一是代码更加复杂，二是StampedLock是不可重入锁，不能在一个线程中反复获取同一个锁。
     * <p>
     * StampedLock还提供了更复杂的将悲观读锁升级为写锁的功能，它主要使用在if-then-update的场景：
     * 即先读，如果读的数据满足条件，就返回，如果读的数据不满足条件，再尝试写。
     *
     * @return 正平方根
     * @author lxlei
     * @date 2021/1/27 10:08
     */
    public double distanceFromOrigin() {
        // 获取一个乐观读锁
        var stamp = stampedLock.tryOptimisticRead();
        // 注意下面2行代码不是原子操作
        // 假设这里的x = 100，y = 200
        double currentX = x;
        // 此处已读取到x = 100, 但x，y可能被写线程修改为x = 300，y = 400
        double currentY = y;
        // 此处已读取到y = 200，如果没有写入操作，那么读取到的是正确的x = 100，y = 200
        // 如果有写入，读取到的是错误的x = 100，y = 400
        // 这里需要检查乐观读锁后是偶发有其他写锁发生
        if (!stampedLock.validate(stamp)) {
            // 如果有则获取一个悲观读锁
            stamp = stampedLock.readLock();
            try {
                // 获取正确的数据
                currentX = x;
                currentY = y;
            } finally {
                // 释放悲观读锁
                stampedLock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
