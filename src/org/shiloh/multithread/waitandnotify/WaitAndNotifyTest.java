package org.shiloh.multithread.waitandnotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author lxlei
 * @date 2021/1/26 15:01
 * @description
 */
public class WaitAndNotifyTest {

    public static void main(String[] args) throws InterruptedException {
        final var taskQueue = new TaskQueue();
        final var threads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            final var thread = new Thread(() -> {
                // 执行task
                while (true) {
                    try {
                        final var task = taskQueue.getTask();
                        System.out.println("execute task = " + task);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }
        final var addTaskThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                // 添加task
                final var taskName = String.format("task-%s", Math.random());
                System.out.println("add task: " + taskName);
                taskQueue.addTask(taskName);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        addTaskThread.start();
        addTaskThread.join();
        Thread.sleep(1000);
        threads.forEach(Thread::interrupt);
    }
}

class TaskQueue {

    private final Queue<String> tasks = new LinkedList<>();

    public synchronized void addTask(String task) {
        this.tasks.add(task);
        // 在相同的锁对象上调用notify()方法，唤醒正在等待的线程
        // 在往队列中添加了任务后，线程立刻对this锁对象调用notify()方法，
        // 这个方法会唤醒一个正在this锁等待的线程（就是在getTask()中位于this.wait()的线程），从而使得等待线程从this.wait()方法返回。
        // 这里调用了notifyAll()而不是notify()，使用notify()将唤醒所有当前正在this锁等待的线程，而notify()只会唤醒其中1个，
        // 具体是哪一个依赖操作系统，有一定的随机性
        // 这是因为可能有多个线程正在getTask()方法内部的wait()中等待，使用notifyAll()将一次性全部唤醒。通常来说，notifyAll()更安全。
        // 有些时候，如果代码逻辑考虑不周，用notify()会导致只唤醒了一个线程，而其他线程可能永远等待下去醒不过来了。
//        this.notify();
        this.notifyAll();
    }

    /**
     * 先判断队列是否为空，如果为空，就循环等待，直到另一个线程往队列中放入了一个任务，while()循环退出，然后返回任务。
     * 但实际上while()循环永远不会退出。因为线程在执行while()循环时，已经在getTask()入口获取了this锁，
     * 其他线程根本无法调用addTask()，因为addTask()执行条件也是获取this锁。
     * 这样会造成死循环
     * <p>
     * 深入思考一下，我们想要的执行效果是：
     * 线程1可以调用addTask()不断往队列中添加任务；
     * 线程2可以调用getTask()从队列中获取任务。如果队列为空，则getTask()应该等待，直到队列中至少有一个任务时再返回。
     * 因此，多线程协调运行的原则就是：当条件不满足时，线程进入等待状态；当条件满足时，线程被唤醒，继续执行任务。
     *
     * @return 当前任务队列中的第一个任务
     * @author lxlei
     * @date 2021/1/26 15:03
     */
    public synchronized String getTask() throws InterruptedException {
        // 当一个线程执行到getTask()方法内部的while循环时，它必定已经获取到了this锁
        // 此时，线程执行while条件判断，如果条件成立（队列为空），线程将执行this.wait()，进入等待状态。
        // 这里的关键是：wait()方法必须在当前获取的锁对象上调用，这里获取的是this锁，因此调用this.wait()。
        // 调用wait()方法后，线程进入等待状态，wait()方法不会返回，
        // 直到将来某个时刻，线程从等待状态被其他线程唤醒后，wait()方法才会返回，然后，继续执行下一条语句。
        // 必须在synchronized块中才能调用wait()方法，因为wait()方法调用时，会释放线程获得的锁，wait()方法返回后，线程又会重新试图获得锁。
        while (tasks.isEmpty()) {
            System.out.println("tasks is empty");
            // 等待唤醒并释放锁
            this.wait();
            // 重新获取锁
        }
        return tasks.remove();
    }
}
