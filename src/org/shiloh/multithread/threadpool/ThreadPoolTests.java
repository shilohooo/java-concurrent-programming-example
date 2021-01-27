package org.shiloh.multithread.threadpool;

import java.util.concurrent.*;

/**
 * @author lxlei
 * @date 2021/1/27 11:04
 * @description Java标准库提供了 {@link ExecutorService} 接口表示线程池，它的典型用法请看示例代码
 * 因为{@link ExecutorService}只是接口，Java标准库提供的几个常用实现类有：
 * {@link Executors#newFixedThreadPool(int)}：线程数固定的线程池；
 * {@link Executors#newCachedThreadPool()}：线程数根据任务动态调整的线程池；
 * {@link Executors#newScheduledThreadPool(int)}：仅单线程执行的线程池。
 * 创建这些线程池的方法都被封装到{@link Executors}这个类中。
 */
public class ThreadPoolTests {

    /**
     * 从程序执行结果来看，由于我们一次性放入了6个任务到线程池中，而线程池的大小为4
     * 因此，前4个任务会同时执行，等待有线程空闲后才会执行后2个任务。
     * 另外要注意的是线程池在程序结束的时候要关闭：
     * 可以使用{@link ExecutorService#shutdown()}方法关闭线程池，此方法会等待正在执行的任务执行完成再关闭。
     * 或使用{@link ExecutorService#shutdownNow()}方法，此方法会立刻停止正在执行的任务并关闭线程池。
     * 又或者使用{@link ExecutorService#awaitTermination(long, TimeUnit)}方法，此方法会等待指定的时间让线程池关闭。
     *
     * @author lxlei
     * @date 2021/1/27 11:16
     */
    public static void main(String[] args) {
        // 创建固定大小的线程池
//        final var threadPool = Executors.newFixedThreadPool(4);
        // 假设把线程池改为CachedThreadPool，由于这个线程池的实现会根据任务数量动态调整线程池大小
        // 所有6个任务可以一次性全部同时执行
        final var threadPool = Executors.newCachedThreadPool();
        // 将线程池的大小限制在4-10之间动态调整
        // 具体可以参考Executors类的newCachedThreadPool()方法的源码
        final var min = 4;
        final var max = 10;
        final var limitedSizeThreadPool = new ThreadPoolExecutor(min, max, 60L,
                TimeUnit.SECONDS, new SynchronousQueue<>());
        // 模拟提交任务
        for (int i = 0; i < 6; i++) {
            threadPool.submit(new MyTask("task" + i));
        }
        // 执行完后关闭线程池
        threadPool.shutdown();
    }
}

class MyTask implements Runnable {

    private final String name;

    public MyTask(String name) {
        this.name = name;
    }


    @Override
    public void run() {
        System.out.println("start task " + this.name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end task " + this.name);
    }
}
