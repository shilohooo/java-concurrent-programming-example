package org.shiloh.multithread.base;

/**
 * @author lxlei
 * @date 2021/1/26 11:11
 * @description
 */
public class LazySingleton {

    private static volatile LazySingleton instance = null;

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new  LazySingleton();
        }
        return instance;
    }

    public static void main(String[] args) {
        LazySingleton.getInstance();
    }
}
