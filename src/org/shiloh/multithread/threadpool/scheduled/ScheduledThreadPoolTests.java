package org.shiloh.multithread.threadpool.scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lxlei
 * @date 2021/1/27 11:32
 * @description 有一种任务是需要定期反复执行的，例如：每秒刷新证券价格。
 * 这种任务本身固定，需要反复执行的，可以使用{@link java.util.concurrent.Executors#newScheduledThreadPool(int)}
 * 放入ScheduledThreadPool的任务可以定期反复执行
 *
 * Java标准库还提供了一个java.util.Timer类，这个类也可以定期执行任务，
 * 但是，一个Timer会对应一个Thread，所以，一个Timer只能定期执行一个任务，多个定时任务必须启动多个Timer，
 * 而一个ScheduledThreadPool就可以调度多个定时任务，所以，我们完全可以用ScheduledThreadPool取代旧的Timer。
 */
public class ScheduledThreadPoolTests {

    /**
     * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}与
     * {@link ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}的区别：
     * FixedRate是指任务总是以固定时间间隔触发，不管任务执行多长时间，
     * 而FixedDelay是指，上一次任务执行完毕后，等待固定的时间间隔，再执行下一次任务。
     * 因此，使用ScheduledThreadPool时，要根据需求选择执行一次、FixedRate执行还是FixedDelay执行。
     *
     * @author lxlei
     * @date 2021/1/27 11:48
     */
    public static void main(String[] args) {
        // 创建一个ScheduledThreadPool类仍然是通过Executors类
        final var scheduledThreadPool = Executors.newScheduledThreadPool(4);
        // 提交一次性任务，它会在指定延迟后只执行一次
        // 5秒后执行一次性任务
//        scheduledThreadPool.schedule(new ScheduledTask("onceScheduledTask"), 5,
//                TimeUnit.SECONDS);
        // 2秒后开始执行定时任务，每3秒执行一次
        scheduledThreadPool.scheduleAtFixedRate(new ScheduledTask("fixedRateTask"), 2,
                1, TimeUnit.SECONDS);
        // 2秒后开始执行，以每3秒为间隔执行一次任务
//        scheduledThreadPool.scheduleWithFixedDelay(new ScheduledTask("fixedDelayTask"), 2,
//                3, TimeUnit.SECONDS);
        // 关闭线程池
//        scheduledThreadPool.shutdown();
    }
}

class ScheduledTask implements Runnable {

    private final String taskName;

    public ScheduledTask(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println("start scheduled task: " + taskName);
        System.out.println(new Date());
        // 如果使用fixedRate每秒执行一次任务，在发生异常时会终止任务执行
        // 如果任务执行时间超过了1秒那么后续任务不会并发执行，而是延迟执行
        throw new RuntimeException("发生错误啦");
//        System.out.println("end scheduled task: " + taskName);
    }
}
