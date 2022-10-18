package org.shiloh.multithread.countdownlatch;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CountDownLatch Example Code
 *
 * @author shiloh
 * @date 2022/10/18 11:01
 * @see <a href="https://www.jianshu.com/p/128476015902">参考文章</a>
 */
public class CountDownLatchExample {
    /**
     * 任务数量
     */
    private static final int TASK_SIZE = 5;

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor COMMON_POLL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            1024,
            5L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private final AtomicLong counter = new AtomicLong(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "CommonThreadPool-" + counter.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args) {
        final var countDownLatch = new CountDownLatch(TASK_SIZE);
        final var task = new Task(countDownLatch);
        for (int i = 0; i < TASK_SIZE; i++) {
            COMMON_POLL.execute(task::exec);
        }
        try {
            System.out.println("main thread await...");
            final var await = countDownLatch.await(5L, TimeUnit.SECONDS);
            System.out.printf("count down latch await result: %s\n", await);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("main thread finished...");
            COMMON_POLL.shutdown();
        }
    }
}

/**
 * 任务模拟
 *
 * @author shiloh
 * @date 2022/10/18 11:15
 */
class Task {
    private final CountDownLatch countDownLatch;

    public Task(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * 模拟执行任务
     *
     * @author shiloh
     * @date 2022/10/18 11:17
     */
    public void exec() {
        final var threadName = Thread.currentThread().getName();
        try {
            System.out.printf("%s execute task\n", threadName);
            this.sleepInSeconds(2L);
        } finally {
            // 倒计时 - 1
            this.countDownLatch.countDown();
        }
    }

    /**
     * 线程睡眠方法
     *
     * @param seconds 睡眠秒数
     * @author shiloh
     * @date 2022/10/18 11:17
     */
    public void sleepInSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
