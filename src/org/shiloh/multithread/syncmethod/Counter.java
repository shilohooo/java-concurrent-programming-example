package org.shiloh.multithread.syncmethod;

/**
 * @author lxlei
 * @date 2021/1/26 12:28
 * @description 线程安全的计数器
 * java程序依靠synchronized关键字对线程加锁进行同步，使用synchronized关键字时锁住的是哪个对象是非常重要的
 * 如果让线程自己选择锁对象往往会让代码逻辑混乱，也不利于封装。
 * 更好的办法是synchronized逻辑封装起来。
 * <p>
 * 如果一个类被设计为允许多线程正确访问，我们就说这个类就是“线程安全”的（thread-safe）。
 * 下面的Counter类就是线程安全的。Java标准库的java.lang.StringBuffer也是线程安全的。
 * 还有一些不变类，例如String，Integer，LocalDate，它们的所有成员变量都是final，多线程同时访问时只能读不能写，这些不变类也是线程安全的。
 * 最后，类似Math这些只提供静态方法，没有成员变量的类，也是线程安全的。
 * 除了上述几种少数情况，大部分类，例如ArrayList，都是非线程安全的类，我们不能在多线程中修改它们。
 * 但是，如果所有线程都只读取，不写入，那么ArrayList是可以安全地在线程间共享的。
 * <p>
 * tips: 没有特殊说明时，一个类默认是非线程安全的
 */
public class Counter {

    private int count = 0;

    /**
     * synchronized给方法加锁：
     * 如果锁住的是this实例时，实际上可以用synchronized修饰当前方法。
     * 因此，用synchronized修饰的方法就是同步方法，它表示整个方法都必须用this实例加锁
     * 如果对一个静态方法加锁的话，锁住的将是Class实例，因为静态方法针对的是类而不是实例。
     * 但我们知道任何一个类都有一个由JVM创建的Class实例。
     * 举例：
     * public synchronized static void test(int val) {
     * ....
     * }
     * 等价于下面的代码：
     * public static void test(int val) {
     *     synchronized (Counter.class) {
     *         ....
     *     }
     * }
     *
     *
     * @author lxlei
     * @date 2021/1/26 14:22
     */
    public synchronized void add(int newValue) {
        // 加锁
//        synchronized (this) {
//            count += newValue;
//        }
        count += newValue;
        // 解锁
    }

    public void dec(int newValue) {
        synchronized (this) {
            count -= newValue;
        }
    }

    /**
     * 这里没有加锁是因为读取1个int变量不需要同步
     *
     * @author lxlei
     * @date 2021/1/26 14:26
     */
    public int get() {
        return count;
    }
}
