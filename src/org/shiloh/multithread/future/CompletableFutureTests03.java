package org.shiloh.multithread.future;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * @author lxlei
 * @date 2021/1/27 14:20
 * @description 除了串行执行外，多个{@link CompletableFuture}还可以并行执行。
 * 例如：
 * 同时从新浪和网易查询证券代码，只要任意一个返回结果，就进行下一步查询价格，查询价格也同时从新浪和网易查询
 * 只要任意一个返回结果，就完成操作。
 * 具体代码的执行顺序请参考当前目录下的readme.md文件
 */
public class CompletableFutureTests03 {

    /**
     * 除了{@link CompletableFuture#anyOf(CompletableFuture[])}可以实现“任意个CompletableFuture只要一个成功”，
     * {@link CompletableFuture#allOf(CompletableFuture[])}可以实现“所有CompletableFuture都必须成功”，
     * 这些组合操作可以实现非常复杂的异步流程控制。
     *
     * 注意CompletableFuture的命名规则：
     * xxx()：表示该方法将继续在已有的线程中执行；
     * xxxAsync()：表示将异步在线程池中执行。
     *
     * @author lxlei
     * @date 2021/1/27 14:35
     */
    public static void main(String[] args) throws InterruptedException {
        // 新建2个异步任务：1个查询新浪，1个查询网易
        final var queryCodeFromSina = CompletableFuture.supplyAsync(() -> queryCodeByNameAndUrl("中国石油",
                "https://finance.sina.com.cn/code/"));
        final var queryCodeFrom163 = CompletableFuture.supplyAsync(() -> queryCodeByNameAndUrl("中国石油",
                "https://money.163.com/code/"));
        // 使用anyOf()方法将2个异步任务合并为1个新的CompletableFuture
        final var mergeQueryCode = CompletableFuture.anyOf(
                queryCodeFromSina, queryCodeFrom163
        );
        // 同时执行2个查询证券代码的异步任务
        final var fetchPriceFromSina = mergeQueryCode.thenApplyAsync(code -> fetchPriceByCodeAndUrl((String) code,
                "https://finance.sina.com.cn/price/"));
        final var fetchPriceFrom163 = mergeQueryCode.thenApplyAsync(code -> fetchPriceByCodeAndUrl((String) code,
                "https://money.163.com/price/"));
        // 合并
        final var mergeFetchPrice = CompletableFuture.anyOf(
                fetchPriceFromSina, fetchPriceFrom163
        );
        // 获取最终结果
        mergeFetchPrice.thenAccept(price -> System.out.println("证券价格：" + price));
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭
        Thread.sleep(3000);

    }

    public static String queryCodeByNameAndUrl(String name, String url) {
        System.out.printf("query code from %s, name is %s%n", url, name);
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "601857";
    }

    public static Double fetchPriceByCodeAndUrl(String code, String url) {
        System.out.printf("query price from %s, code is %s%n", url, code);
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 5 + Math.random() * 20;
    }
}
