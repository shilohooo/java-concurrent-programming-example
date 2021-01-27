package org.shiloh.multithread.forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author lxlei
 * @date 2021/1/27 14:38
 * @description 使用Fork/Join对大数据进行并行求和
 */
public class ForkJoinTests {

    private static final Random RANDOM = new Random(0);

    public static void main(String[] args) {
        // 创建2000个随机数组组成的数组
        final var array = new long[2000];
        var expectedSum = 0;
        for (int i = 0; i < array.length; i++) {
            array[i] = random();
            expectedSum += array[i];
        }
        System.out.printf("Expected sum: %d%n", expectedSum);
        // fork / join
        final var task = new SumTask(array, 0, array.length);
        final var startTime = System.currentTimeMillis();
        final var result = ForkJoinPool.commonPool().invoke(task);
        final var endTime = System.currentTimeMillis();
        System.out.printf("Fork/Join sum: %d in %d ms", result, (endTime - startTime));
    }

    public static long random() {
        return RANDOM.nextInt(10000);
    }

}

class SumTask extends RecursiveTask<Long> {

    public static final int THRESHOLD = 500;

    private final long[] array;

    private final int start;

    private final int end;

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * 首先判断任务量是否够小（小于阈值500），主线程给的是2000
     * 然后执行1个大的计算任务0~2000，首先拆分成两个小任务0~1000和1000~2000
     * 这两个小任务仍然大于阈值500，继续拆分成四个小任务0~500，500~1000，1000~1500，1500~2000
     * 最后计算结果被依次合并，得到最终的结果
     *
     * @author lxlei
     * @date 2021/1/27 14:58
     */
    @Override
    protected Long compute() {
        if ((end - start) <= THRESHOLD) {
            // 如果任务足够小，直接计算
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += this.array[i];
                // 放慢计算速度
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return sum;
        }
        // 核心代码
        // 任务太大则进行拆分
        int middle = (end + start) / 2;
        System.out.printf("split %d ~ %d ===> %d ~ %d, %d ~ %d%n", start, end,
                start, middle, middle, end);
        final var sumTask01 = new SumTask(this.array, start, middle);
        final var sumTask02 = new SumTask(this.array, middle, end);
        // 调用拆分任务
        invokeAll(sumTask01, sumTask02);
        // 获取计算结果
        final var subResult01 = sumTask01.join();
        final var subResult02 = sumTask02.join();
        final var finalResult = (subResult01 + subResult02);
        System.out.printf("result = %d + %d ===> %d%n", subResult01, subResult02, finalResult);
        return finalResult;
    }
}
