package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 10:58
 * @description 中断线程测试3：设置标志位
 */
public class InterruptThreadTest03 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main thread start");
        final var helloThread = new HelloThread();
        helloThread.start();
        Thread.sleep(5);
        // end
        helloThread.end();
        // waiting
        helloThread.join();
        System.out.println("main thread end");
    }
}

class HelloThread extends Thread {

    /**
     * 线程运行标志位
     * volatile关键字保证了共享变量在线程间的可见性
     * 参考链接：https://www.jianshu.com/p/3893fb35240f
     */
    private volatile boolean running = true;

    @Override
    public void run() {
        int n = 0;
        while (running) {
            n++;
            System.out.println("Hello, n = " + n);
        }
        System.out.println("HelloThread end");
    }

    public void end() {
        this.running = false;
    }
}
