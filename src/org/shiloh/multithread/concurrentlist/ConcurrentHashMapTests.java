package org.shiloh.multithread.concurrentlist;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lxlei
 * @date 2021/1/27 10:18
 * @description 并发集合：ConcurrentHashMap测试
 */
public class ConcurrentHashMapTests {

    public static void main(String[] args) {
        final var data = new ConcurrentHashMap<String, String>();
        // 模拟在同步的线程读写
        data.put("name", "tom");
        data.put("gender", "male");
        final var name = data.get("name");
    }
}
