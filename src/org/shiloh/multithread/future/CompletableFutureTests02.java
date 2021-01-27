package org.shiloh.multithread.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author lxlei
 * @date 2021/1/27 12:29
 * @description 如果只是实现了异步回调机制，可能看不出{@link CompletableFuture}相比{@link Future}的优势。
 * CompletableFuture更强大的功能是，多个CompletableFuture可以串行执行
 * 例如：
 * 定义2个{@link CompletableFuture}，第1个{@link CompletableFuture}根据证券名称查询证券代码，
 * 第二个{@link CompletableFuture}根据证券代码查询证券价格。
 */
public class CompletableFutureTests02 {

    public static void main(String[] args) throws InterruptedException {
        // 第一个任务：根据证券名称查询证券代码
        final var queryCode = CompletableFuture.supplyAsync(
                () -> CompletableFutureTests02.queryCodeByName("中国石油")
        );
        // 在第一个任务执行成功后继续执行下一个任务：根据证券代码查询证券价格
        final var fetchPrice = queryCode.thenApplyAsync(
                CompletableFutureTests02::fetchPriceByCode
        );
        // 在获取证券价格成功后打印结果
        fetchPrice.thenAccept(price -> System.out.println("证券价格：" + price));
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭
        Thread.sleep(2000);
    }

    public static String queryCodeByName(String name) {
        try {
            System.out.println("证券名称：" + name);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "code";
    }

    public static Double fetchPriceByCode(String code) {
        try {
            System.out.println("证券代码：" + code);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 5 + Math.random() * 20;
    }
}
