package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/1 22:26
 * @description 使用main()方法和多线程去执行代码的区别
 */
public class DiffBetweenUsingMainAndMultiThread {

    public static void main(String[] args) {
        System.out.println("main start");
        final var thread = new Thread(() -> {
            System.out.println("new thread run");
            try {
                // 模拟并发执行效果，让新启动的线程睡眠一段时间
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("new thread end");
        });
        // 可以对线程设定优先级，设定优先级的方法是
//        优先级高的线程被操作系统调度的优先级较高，操作系统对高优先级线程可能调度更频繁，
//        但决不能通过设置优先级来确保高优先级的线程一定会先执行。
//        thread.setPriority(priority);
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main end");
        // 直接调用Thread实例的run()方法是无效的
//        直接调用run()方法，相当于调用了一个普通的Java方法，当前线程并没有任何改变，也不会启动新线程。
//        下面的代码实际上是在main()方法内部又调用了run()方法，打印hello语句是在main线程中执行的，没有任何新线程被创建。
//        必须调用Thread实例的start()方法才能启动新线程
//        thread.run();
    }

}
