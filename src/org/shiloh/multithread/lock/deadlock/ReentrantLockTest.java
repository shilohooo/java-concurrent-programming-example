package org.shiloh.multithread.lock.deadlock;

/**
 * @author lxlei
 * @date 2021/1/26 14:54
 * @description 可重入锁测试
 */
public class ReentrantLockTest {

    private int count = 0;

    /**
     * 在线程执行到了add()方法内部时，说明它已经获取了当前实例的this锁。如果传入的val小于0，则会去调用dec()方法。
     * 由于dec()方法也需要获取this锁，问题来了：
     * 对同一个线程，能否在获取到锁以后继续获取同一个锁？
     * <p>
     * 答案是肯定的。JVM允许同一个线程重复获取同一个锁，这种能被同一个线程反复获取的锁，就叫做可重入锁。
     * 由于Java的线程锁是可重入锁，所以，获取锁的时候，不但要判断是否是第一次获取，还要记录这是第几次获取。
     * 每获取一次锁，记录+1，每退出synchronized块，记录-1，减到0的时候，才会真正释放锁。
     *
     * @author lxlei
     * @date 2021/1/26 14:55
     */
    public synchronized void add(int val) {
        if (val < 0) {
            dec(-val);
        } else {
            this.count += val;
        }
    }

    public synchronized void dec(int val) {
        this.count += val;
    }
}