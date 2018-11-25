package cn.hejinyo.learn.condition;

import java.util.Iterator;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/24 16:53
 */
public class TreadWaitTest implements Runnable {
    private final Object object;
    private List<String> list;

    public TreadWaitTest(Object object, List<String> list) {
        this.object = object;
        this.list = list;
    }

    @Override
    public void run() {
        while (true) {
            Iterator<String> iterator = list.iterator();
            if (iterator.hasNext()) {
                System.out.println("消费=> " + iterator.next());
                iterator.remove();
                if (list.size() < 5) {
                    synchronized (object) {
                        object.notify();
                    }
                }
            } else {
                synchronized (object) {
                    object.notify();
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
