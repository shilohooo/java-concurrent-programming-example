package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 10:18
 * @description 1个线程可以等待另外1个线程直到其运行结束。
 * <p>
 * Java线程对象的状态包括以下几种：
 * 1.New: 新创建的线程，尚未执行
 * 2.Runnable: 运行中的线程，正在执行run()方法的java代码
 * 3.Blocked: 运行中的线程，因为某些操作被阻塞而挂起
 * 4.Waiting: 运行中的线程，因为某些操作在等待中
 * 5.Timed Waiting: 运行中的线程，因为执行了sleep()方法正在计时等待
 * 6.Terminated: 线程被终止，因为run()方法执行完毕
 */
public class WaitForOtherThreadsToEndTests {

    /**
     * 当主线程调用线程对象thread01的join()方法时，主线程将等待线程thread01运行结束，
     * 即join是指等待该线程结束，然后才继续往下执行自身线程。
     * 下面代码的运行顺序为：
     * 1. 主线程会先打印"start"
     * 2. 接着开始运行thread01线程，thread01线程打印"hello"后就结束了，
     * 3. 最后再到主线程打印"end"
     * 如果thread01线程已经结束，调用thread01实例的join()方法会立刻返回。
     * 此外，使用thread01.join(long)重载方法可以指定一个等待时间，如果线程的执行时间超过了指定的等待时间就不会继续等待。
     *
     * @author lxlei
     * @date 2021/1/26 10:23
     */
    public static void main(String[] args) throws InterruptedException {
        final var thread01 = new Thread(() -> {
            System.out.println("Hello");
        });
        System.out.println("start");
        thread01.start();
        // 主线程在启动thread01线程后，可以通过调用thread01.join()方法等待thread01线程结束后再继续运行
        thread01.join();
        System.out.println("end");
    }
}
