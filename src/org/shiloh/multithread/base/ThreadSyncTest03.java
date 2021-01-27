package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 12:11
 * @description 线程同步：错误示范2
 */
public class ThreadSyncTest03 {

    /**
     * 这段代码的4个线程对2个共享变量分别进行读写操作，但是使用的锁都是同一个对象{@link CustomCounter#LOCK}
     * 这就造成了原本可以并发执行的CustomCounter.studentCount += 1和CustomCounter.teacherCount += 1，现在无法并发执行，降低了执行效率
     * 实际上，需要同步的线程可以分成两组：AddStudentThread和DecStudentThread，AddTeacherThread和DecTeacherThread
     * 组之间并不存在竞争关系，因此，应该使用2个不同的锁，这样才能提高程序的运行效率
     * @author lxlei
     * @date 2021/1/26 12:17
     */
    public static void main(String[] args) throws InterruptedException {
        final var start = System.currentTimeMillis();
        final var threads = new Thread[]{
                new AddStudentThread(),
                new DecStudentThread(),
                new AddTeacherThread(),
                new DecTeacherThread()
        };
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(CustomCounter.studentCount);
        System.out.println(CustomCounter.teacherCount);
        final var end = System.currentTimeMillis();
        // 使用同一个锁执行时长大约40ms
        // 使用2个不同的锁执行时长大约为10ms
        System.out.println("耗时：" + (end - start) + "ms");
    }
}

class CustomCounter {

    public static final Object LOCK = new Object();

    public static final Object STUDENT_LOCK = new Object();

    public static final Object TEACHER_LOCK = new Object();

    public static int studentCount = 0;

    public static int teacherCount = 0;
}

class AddStudentThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (CustomCounter.STUDENT_LOCK) {
                CustomCounter.studentCount += 1;
            }
        }
    }
}

class DecStudentThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (CustomCounter.STUDENT_LOCK) {
                CustomCounter.studentCount -= 1;
            }
        }
    }
}

class AddTeacherThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (CustomCounter.TEACHER_LOCK) {
                CustomCounter.teacherCount += 1;
            }
        }
    }
}

class DecTeacherThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (CustomCounter.TEACHER_LOCK) {
                CustomCounter.teacherCount -= 1;
            }
        }
    }
}
