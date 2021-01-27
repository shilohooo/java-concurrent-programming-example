# ThreadLocal
多线程是Java实现多任务的基础，Thread对象代表一个线程，我们可以在代码中调用Thread.currentThread()获取当前线程。
例如：打印日志时，可以同时打印出当前线程的名称：
```java
public class LogCurrentThreadNameTests {
    
    public static void main(String[] args) {
        logSomething("start main thread...");
        new Thread(() -> {
            logSomething("run task...");
        }).start();
        logSomething("end main thread...");
    }
    
    public static void logSomething(String msg) {
        System.out.println(Thread.currentThread().getName() + ":" + msg);
    }
}
```
----
对于多任务，Java标准库提供的线程池可以方便地执行这些任务，同时复用线程。  
Web应用程序就是典型的多任务应用，每个用户请求页面时都会创建一个任务，比如：
```java
public class UserRequestHandler {
    public void handleRequest(User user) {
        checkUserPermission();
        doWork();
        saveStatus();
        sendResponse();
    }
}
```
然后通过线程池去执行这些任务。

----
观察上面的handleRequest()方法，在方法内部需要调用若干个其他方法。  
同时，我们遇到一个问题：如何在一个线程内传递状态呢？  
handleRequest()方法需要传递的状态就是User实例，一种简单的方法就是传入到每个调用的方法中：
```java
public class UserRequestHandler {
    public void handleRequest(User user) {
        checkUserPermission(user);
        doWork(user);
        saveStatus(user);
        sendResponse(user);
    }
}
```
但是在这些被调用的方法内部，又会调用其他很多的方法，这样会导致user传递到所有地方：
```java
public class UserService {
    public void doWork(User user) {
        queryStatus();
        checkStatus();
        setNewStatus();
        log();
    }
}
```
这种在一个线程中，横跨若干个方法调用，需要传递的对象，我们通常称之为上下文（Context），它是一种状态，可以是用户身份、任务信息等。
给每个方法增加一个context参数非常麻烦，而且有些时候，如果调用链有无法修改源码的第三方库，User对象就传不进去了。  
----
Java标准库提供了一个特殊的ThreadLocal，它可以在一个线程中传递同一个对象。  
ThreadLocal实例通常总是以静态字段初始化如下：
```java
public class ThreadLocalDemo {
    public static ThreadLocal<User> threadLocalUser = new ThreadLocal<>();
}
```
ThreadLocal的典型用法如下：
```java
public class UserService {
    
    public static ThreadLocal<User> threadLocalUser = new ThreadLocal<>();
    
    public void doWork(User user) {
        try {
            // 通过设置一个User实例关联到ThreadLocal中，在移除之前，所有方法都可以随时获取到该User实例：
            threadLocalUser.set(user);
            queryStatus();
            checkStatus();
            setNewStatus();
            log();
        } finally {
            threadLocalUser.remove();
        }
    }
    
    public UserStatus queryStatus() {
        User user = threadLocalUser.get();
    }
    
    public void setNewStatus() {
        User user = threadLocalUser.get();
    }
}
```
注意到普通的方法调用一定是同一个线程执行的，所以queryStatus，setNewStatus方法内，threadLocalUser.get()获取的User对象是同一个实例。

----
实际上，可以把ThreadLocal看成一个全局Map<Thread, Object>: 每个线程获取TheadLocal变量时，总是使用Thread自身作为key：
```java
public class Test() {
    public static void main(String[] args) {
        Object threadLocalValue = threadLocalMap.get(Thread.currentThread());
    }
}
```
因此，ThreadLocal相当于给每个线程都开辟了一个独立的存储空间，各个线程的ThreadLocal关联的实例互不干扰。
还有一个需要特别注意的地方是：ThreadLocal一定要在finally块中清除：
```java
public class Test() {

    public static ThreadLocal<String> threadLocalString = new ThreadLocal<>();
    
    public static void main(String[] args) {
        try {
            threadLocalString.set(val);
            // do something...
        } finally {
            threadLocalString.remove();
        }
    }
} 
```
这是因为当前线程执行完相关代码后，很可能会被重新放入线程池中，如果ThreadLocal没有被清除，该线程执行其他代码时，会把上一次的状态带进去。

----
为了保证能释放ThreadLocal关联的实例，可以通过实现AutoCloseable接口配合try (resource) {...}结构，来让编译器自动关闭。  
例如：一个保存了当前用户名的ThreadLocal可以封装为一个UserContext对象：

```java
public class UserContext implements AutoCloseable {

    static final ThreadLocal<User> USER_CONTEXT = new ThreadLocal<>();

    public UserContext(User user) {
        USER_CONTEXT.set(user);
    }

    public static String getCurrentUser() {
        USER_CONTEXT.get();
    }

    @Override
    public void close() {
        USER_CONTEXT.remove();
    }

    // 使用示例:
    public static void main(String[] args) {
        try (final UserContext userContext = new UserContext("shiloh")) {
            // 可任意调用UserContext.getCurrentUser()方法
            final User user = UserContext.getCurrentUser();
        }
        // 在上面的代码执行结束后程序会自动调用UserContext中的close()方法释放ThreadLocal对象
    }
}
```
这样就在UserContext中完全封装了ThreadLocal，外部代码在try (resource) {...}内部可以随时调用UserContext.currentUser()获取当前线程绑定的用户名。

----
总结：  
ThreadLocal表示线程的“局部变量”，它确保每个线程的ThreadLocal变量都是各自独立的

ThreadLocal适合在一个线程的处理流程中保持上下文（避免了同一参数在所有方法中传递）

使用ThreadLocal要用try ... finally结构，并在finally中清除
