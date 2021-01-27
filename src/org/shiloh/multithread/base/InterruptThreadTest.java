package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 10:33
 * @description 中断线程测试
 * 如果线程需要执行一个长时间任务，就可能需要能中断线程。
 * 中断线程就是其他线程给该线程发一个信号，该线程收到信号后结束执行run()方法，使得自身线程能立刻结束运行。
 */
public class InterruptThreadTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("start");
        final var customThread = new CustomThread();
        customThread.start();
        // 睡眠1s
        Thread.sleep(1);
        // 中断customThread线程，此处只是向customThread线程发送了一个"中断请求"
        // 至于customThread线程是否能立刻响应，需要看具体代码。
        customThread.interrupt();
        customThread.join();
        System.out.println("end");
    }

}

class CustomThread extends Thread {
    @Override
    public void run() {
        int n = 0;
        // 检测线程状态：
        // 当线程处于非中断状态时，输出n的值
        // 在接收到"中断请求"后给出响应，结束运行
        while (!super.isInterrupted()) {
            n++;
            System.out.println("Hello, n = " + n);
        }
    }
}
