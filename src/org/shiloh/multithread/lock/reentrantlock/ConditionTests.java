package org.shiloh.multithread.lock.reentrantlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lxlei
 * @date 2021/1/27 9:14
 * @description 使用Condition对象来实现notify和notifyAll的功能
 */
public class ConditionTests {
    public static void main(String[] args) throws InterruptedException {
        final var taskQueue = new TaskQueue();
        final var threads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            final var thread = new Thread(() -> {
                while (true) {
                    try {
                        final var task = taskQueue.getTask();
                        System.out.println("execute task " + task);
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
                final var taskName = String.format("task-%s", Math.random());
                System.out.println("add task " + taskName);
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

    private final Lock lock = new ReentrantLock();

    /**
     * 使用Condition时，引用的Condition对象必须从Lock实例的newCondition()返回
     * 这样才能获得一个绑定了Lock实例的Condition实例。
     * Condition对象的await()，signal()，signalAll()方法与synchronized锁对象的wait()，notify()，notifyAll()方法作用是一致的:
     * await()会释放当前锁，进入等待状态
     * signal()会唤醒某个等待线程
     * signalAll()会唤醒所有等待线程
     * 唤醒线程从await()返回后需要重新获得锁。
     * 此外，和tryLock()类似，await()方法可以在等待指定时间后，如果还没有被其他线程通过signal()或signalAll()方法唤醒，可以自己醒来：
     * if (await(1, TimeUnit.SECONDS)) {
     *     // 被其他线程唤醒
     * } else {
     *     // 在指定时间内（这里是1秒）没有被其他线程唤醒
     * }
     */
    private final Condition condition = lock.newCondition();

    private final Queue<String> tasks = new LinkedList<>();

    public void addTask(String taskName) {
        // 加锁
        lock.lock();
        try {
            // 添加任务到队列中
            this.tasks.add(taskName);
            // 唤醒当前锁对象的所有线程
            this.condition.signalAll();
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    public String getTask() throws InterruptedException {
        // 加锁
        lock.lock();
        try {
            // 尝试获取队列中的任务
            while (tasks.isEmpty()) {
                // 如果没有任务则释放当前锁，并将线程设为等待状态
                System.out.println("tasks is empty...");
                this.condition.await();
            }
            return tasks.remove();
        } finally {
            // 释放锁
            lock.unlock();
        }
    }
}