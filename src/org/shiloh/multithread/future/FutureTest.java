package org.shiloh.multithread.future;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author lxlei
 * @date 2021/1/27 11:56
 * @description 在执行多个任务的时候，使用Java标准库提供的线程池是非常方便的。
 * 我们提交的任务只需要实现Runnable接口，就可以让线程池去执行：
 * class Task implements Runnable {
 *     private String result;
 *     @Override
 *     public void run() {
 *         this.result = ....;
 *     }
 * }
 *
 * 但是Runnable接口有个问题，它的方法没有返回值。如果任务需要一个返回结果，那么只能保存到变量，还要提供额外的方法读取，非常不便。
 * 所以，Java标准库还提供了一个Callable接口，和Runnable接口比，它多了一个返回值，
 * 具体可查看{@link java.util.concurrent.Callable}接口中的call()方法：
 * V call() throws Exception;
 * 此时上面的任务类可以修改成这样：
 * class Task implements Callable<String> {
 *     @Override
 *     public String call() throws Exception {
 *         return ...;
 *     }
 * }
 * 并且Callable接口是一个泛型接口，可以返回指定类型的结果。
 *
 * 那么如何获取异步执行的结果呢？
 * 查看{@link java.util.concurrent.ExecutorService#submit(Runnable)}方法：
 * 可以发现它返回了一个{@link java.util.concurrent.Future}类型，这个类型的实例代表一个未来能获取结果的对象。
 *
 * 示例代码：
 */
public class FutureTest {

    /**
     * 当提交一个{@link Callable}任务后，会同时获得一个{@link Future}对象。
     * 然后在主线程某个时刻调用{@link Future#get()}方法就可以获得异步执行的结果。
     * 在调用{@link Future#get()}时，如果异步任务已经执行完成，那么可以直接获得结果。
     * 如果异步任务还没有完成的话，{@link Future#get()}方法会阻塞，直到任务完成后才返回结果。
     *
     * 一个{@link Future}接口表示一个未来可能会返回的结果，常用方法有：
     * 1.{@link Future#get()} 获取结果（可能会阻塞）
     * 2.{@link Future#get(long, TimeUnit)} 获取结果，但只等待指定的时间
     * 3.{@link Future#cancel(boolean)} 取消当前的任务
     * 4.{@link Future#isDone()} 判断任务是否已完成
     *
     * @author lxlei
     * @date 2021/1/27 12:05
     */
    public static void main(String[] args) {
        final var threadPool = Executors.newFixedThreadPool(3);
        final var future = threadPool.submit(new TimeTask());
        try {
            // 获取结果时可能会阻塞
            final var date = future.get();
            System.out.println("date = " + date);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }
}

class TimeTask implements Callable<Date> {

    @Override
    public Date call() throws InterruptedException {
        Thread.sleep(2000);
        return new Date();
    }
}