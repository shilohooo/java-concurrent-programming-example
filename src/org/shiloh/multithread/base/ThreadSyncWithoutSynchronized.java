package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 12:22
 * @description 线程同步：不需要使用Synchronized的示例
 * java规范定义了几种原子操作：
 * 基本类型（long和boolean除外）赋值，例如：int n = m
 * 引用类型赋值，例如：List<String> dataList = anotherList
 * <p>
 * long和double是64位数据，JVM没有明确规定64位赋值操作是不是一个原子操作，不过在x64平台的JVM是把long和double的赋值作为原子操作实现的。
 */
public class ThreadSyncWithoutSynchronized {
}
