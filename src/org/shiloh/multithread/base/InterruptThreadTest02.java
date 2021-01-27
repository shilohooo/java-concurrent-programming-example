package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 10:42
 * @description 中断线程测试02
 */
public class InterruptThreadTest02 {

    /**
     * 主线程启动myThread01线程后，调用myThread01线程的interrupt()方法向myThread01线程发送1个"中断线程"的请求
     * 而此时myThread01线程在启动myThread02线程后正在等待myThread02线程运行结束，此方法会立刻结束并抛出{@link InterruptedException}异常
     * 由于在myThread01线程中捕获了{@link InterruptedException}异常，这里会打印一句"myThread01: Interrupted"
     * 接着myThread01线程准备结束运行，在结束执行myThread01线程向myThread02线程发送了1个"中断线程"请求
     * 而myThread02线程接收到"中断线程"请求便会结束运行，并没有进入到while循环中，如果去掉myThread02.interrupt();这行代码
     * myThread02线程则会一直运行下去，JVM也不会退出。
     *
     * @author lxlei
     * @date 2021/1/26 10:51
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        final var myThread01 = new MyThread01();
        // start
        myThread01.start();
        // 中断myThread01线程
        myThread01.interrupt();
        myThread01.join();
        System.out.println("end");
    }
}

class MyThread01 extends Thread {
    /**
     * 当线程接收到"中断线程"请求后，如果线程处于等待状态则会捕获到{@link InterruptedException}异常
     * 当{@link Thread#isInterrupted()}为true或捕获到{@link InterruptedException}异常后都应该立即结束自身线程运行
     * @author lxlei
     * @date 2021/1/26 11:34
     */
    @Override
    public void run() {
        final var myThread02 = new MyThread02();
        // start
        myThread02.start();
        try {
            // 等待myThread02线程执行结束
            myThread02.join();
        } catch (InterruptedException e) {
            System.out.println("myThread01: Interrupted");
        }
        // 中断myThread02线程
        myThread02.interrupt();
    }
}

class MyThread02 extends Thread {
    @Override
    public void run() {
        int n = 0;
        while (!super.isInterrupted()) {
            System.out.println("Hello, n = " + n);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                break;
            }
        }
    }
}
