package cn.hejinyo.learn.condition;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/24 17:04
 */
public class ThreadNotifyTest implements Runnable {
    private final Object object;
    private List<String> list;

    public ThreadNotifyTest(Object object, List<String> list) {
        this.object = object;
        this.list = list;
    }

    @Override
    public void run() {
        while (true) {
            if (list.size() > 10) {
                try {
                    synchronized (object) {
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String s = UUID.randomUUID().toString();
            System.out.println("生产者：" + list.size());
            list.add(s);
        }

    }
}
