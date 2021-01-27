package org.shiloh.multithread.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author lxlei
 * @date 2021/1/27 12:16
 * @description 使用Future获得异步执行结果时，要么调用阻塞方法get()，要么轮询看isDone()是否为true，
 * 这两种方法都不是很好，因为主线程也会被迫等待。
 * 从Java 8开始引入了CompletableFuture，它针对Future做了改进，可以传入回调对象，
 * 当异步任务完成或者发生异常时，自动调用回调对象的回调方法。
 * 示例：获取股票价格
 */
public class CompletableFutureTests {

    /**
     * 创建一个{@link CompletableFuture}是通过{@link CompletableFuture#supplyAsync(Supplier)}实现的，
     * 它需要一个实现了{@link Supplier}接口的对象，查看{@link Supplier}接口可以看到只有一个方法：
     * public interface Supplier<T> {
     *     T get()
     * }
     * 示例代码使用了lambda语法简化了传参，直接传入了{@link CompletableFutureTests#fetchPrice()}
     * 因为静态方法{@link CompletableFutureTests#fetchPrice()}的签名符合{@link Supplier}接口的定义
     * 紧接着，{@link CompletableFuture}已经被提交给默认的线程池执行了
     * 之后需要定义的是{@link CompletableFuture}完成时和发生异常时需要回调的实例。
     * 在完成时，{@link CompletableFuture}会调用{@link java.util.function.Consumer}对象
     * 发生异常时，{@link CompletableFuture}则会调用{@link java.util.function.Function}对象
     * 示例使用了lambda表达式去简化代码
     *
     * {@link CompletableFuture}的优点：
     * 1.异步任务结束时，会自动回调某个对象的方法
     * 2.异步任务出错时，会自动回调某个对象的方法
     * 3.主线程设置好回调后，不再关心异步任务的执行
     *
     * @author lxlei
     * @date 2021/1/27 12:23
     */
    public static void main(String[] args) throws InterruptedException {
        // 创建1个异步执行的任务
        final var async = CompletableFuture.supplyAsync(
                CompletableFutureTests::fetchPrice
        );
        // 执行成功时的回调：输出获取到的股票价格
        async.thenAccept(price -> System.out.println("获取到的股票价格为：" + price));
        // 执行时发生异常导致失败的回调：打印异常信息，返回null
        async.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭
        Thread.sleep(2000);
    }

    public static Double fetchPrice() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }
}
