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
