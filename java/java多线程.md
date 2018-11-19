# Tread常用方法
> join，yiled，sellp，notify，wait，interrupt

## synchronized
> 可重入锁:自己可以再次获取自己的内部锁,指的是可重复可递归调用的锁，在外层使用锁之后，在内层仍然可以使用，并且不发生死锁（前提得是同一个对象或者class），这样的锁就叫做可重入锁。ReentrantLock和synchronized都是可重入锁

synchronized取得的锁都是对象锁，而不是把一段代码或方法当做锁。 如果多个线程访问的是同一个对象，哪个线程先执行带synchronized关键字的方法，则哪个线程就持有该方法，那么其他线程只能呈等待状态。如果多个线程访问的是多个对象则不一定，因为多个对象会产生多个锁。

+ synchronized（this）同步代码块的使用
> synchronized (this) {
                  getData1 = privateGetData1;
                  getData2 = privateGetData2;
              }
              

+ synchronized（object）代码块间使用

> synchronized关键字加到static静态方法和synchronized(class)代码块上都是是给Class类上锁，而synchronized关键字加到非static静态方法上是给对象上锁。


## volatile关键字的可见性
每次被线程访问时，都强迫从主存（共享内存）中重读该成员变量的值。
而且，当成员变量发生变化时，强迫线程将变化值回写到主存（共享内存）。
这样在任何时刻，两个不同的线程总是看到某个成员变量的同一个值，这样也就保证了同步数据的可见性
+ volatile无法保证对变量原子性的

## synchronized关键字和volatile关键字比较
> synchronized关键字在JavaSE1.6之后进行了主要包括为了减少获得锁和释放锁带来的性能消耗而引入的偏向锁和轻量级锁以及其它各种优化之后执行效率有了显著提升，实际开发中使用synchronized关键字还是更多一些。
+ volatile关键字是线程同步的轻量级实现，所以volatile性能肯定比synchronized关键字要好。
+ volatile关键字只能用于变量而synchronized关键字可以修饰方法以及代码块。
+ 多线程访问volatile关键字不会发生阻塞，而synchronized关键字可能会发生阻塞
+ volatile关键字能保证数据的可见性，但不能保证数据的原子性。synchronized关键字两者都能保证。
+ volatile关键字用于解决变量在多个线程之间的可见性，而synchronized关键字解决的是多个线程之间访问资源的同步性。

## 等待/通知机制(wait/notify)
是指一个线程A调用了对象O的wait()方法进入等待状态，

而另一个线程B调用了对象O的notify()/notifyAll()方法，

线程A收到通知后退出等待队列，进入可运行状态，进而执行后续操作。

上诉两个线程通过对象O来完成交互，

而对象上的wait()方法和notify()/notifyAll()方法的关系就如同开关信号一样，用来完成等待方和通知方之间的交互工作。
![image](https://raw.githubusercontent.com/HejinYo/learn/master/assets/img/thread.png)

+ notify()锁不释放
  当方法wait()被执行后，锁自动被释放，但执行完notify()方法后，锁不会自动释放。必须执行完notify()方法所在的synchronized代码块后才释放。
 

## 线程池
+ 降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗。
+ 提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。
+ 提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。

## Executor 框架结构(主要由三大部分组成)

### 1、任务
执行任务需要实现的Runnable接口或Callable接口。
Runnable接口或Callable接口实现类都可以被ThreadPoolExecutor或ScheduledThreadPoolExecutor执行。

> Runnable接口不会返回结果但是Callable接口可以返回结果。后面介绍Executors类的一些方法的时候会介绍到两者的相互转换。

### 2、任务的执行
ScheduledThreadPoolExecutor和ThreadPoolExecutor这两个关键类实现了ExecutorService接口

### 3、异步计算的结果
Future接口以及Future接口的实现类FutureTask类。

当我们把Runnable接口或Callable接口的实现类提交（调用submit方法）给ThreadPoolExecutor或ScheduledThreadPoolExecutor时，会返回一个FutureTask对象。


```java
public class ThreadPoolExecutorTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10)
                );

        executor.execute(() -> {
            System.out.println("123");
        });

        Future future = executor.submit(() -> 12);
        System.out.println(future.get());

        executor.shutdown();

    }
}

```

## 各种线程池的适用场景介绍
FixedThreadPool： 适用于为了满足资源管理需求，而需要限制当前线程数量的应用场景。它适用于负载比较重的服务器；

SingleThreadExecutor： 适用于需要保证顺序地执行各个任务并且在任意时间点，不会有多个线程是活动的应用场景。

CachedThreadPool： 适用于执行很多的短期异步任务的小程序，或者是负载较轻的服务器；

ScheduledThreadPoolExecutor： 适用于需要多个后台执行周期任务，同时为了满足资源管理需求而需要限制后台线程的数量的应用场景，

SingleThreadScheduledExecutor： 适用于需要单个后台线程执行周期任务，同时保证顺序地执行各个任务的应用场景。



