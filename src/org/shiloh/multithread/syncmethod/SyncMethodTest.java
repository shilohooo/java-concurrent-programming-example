package org.shiloh.multithread.syncmethod;

/**
 * @author lxlei
 * @date 2021/1/26 12:27
 * @description 同步方法测试
 */
public class SyncMethodTest {

    /**
     * 将同步操作逻辑封装，在线程调用计数器的add(),dec()方法时，它不必关心同步逻辑，因为synchronized代码块在add()，dec()方法内部。
     * 并且synchronized锁住的对象是this，即当前实例，这又使得创建多个Counter实例的时候，它们之间互不影响，可以并发执行
     * @author lxlei
     * @date 2021/1/26 14:13
     */
    public static void main(String[] args) {
        final var counter01 = new Counter();
        final var counter02 = new Counter();
        final var thread01 = new Thread(() -> counter01.add(1));
        thread01.start();
        final var thread02 = new Thread(() -> counter01.dec(1));
        thread02.start();
        final var thread03 = new Thread(() -> counter02.add(1));
        thread03.start();
        final var thread04 = new Thread(() -> counter02.dec(1));
        thread04.start();
        System.out.println("counter01.getCount() = " + counter01.get());
        System.out.println("counter02.getCount() = " + counter02.get());
    }
}
