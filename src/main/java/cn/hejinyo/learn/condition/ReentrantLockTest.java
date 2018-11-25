package cn.hejinyo.learn.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/24 17:35
 */
public class ReentrantLockTest implements Runnable {
    ReentrantLock lock;

    private List<String> list;

    public ReentrantLockTest(List<String> list, ReentrantLock lock) {
        this.list = list;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            if (list.size() < 2) {
                list.add(UUID.randomUUID().toString());
            }
        } finally {
            lock.unlock();
        }
        System.out.println(list.size());
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        condition.await();
        condition.signal();
        ReentrantLockTest t1 = new ReentrantLockTest(list, lock);
        ReentrantLockTest t2 = new ReentrantLockTest(list, lock);
        ReentrantLockTest t3 = new ReentrantLockTest(list, lock);
        ReentrantLockTest t4 = new ReentrantLockTest(list, lock);
        ReentrantLockTest t5 = new ReentrantLockTest(list, lock);

        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
        new Thread(t4).start();
        new Thread(t5).start();
        System.out.println(list.size());


    }

}
