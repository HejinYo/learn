package cn.hejinyo.learn.condition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/24 16:58
 */
public class TreadMainTest {

    public static void main(String[] args) throws InterruptedException {
        Object object = new Object();
        List<String> list = new LinkedList<>();
        TreadWaitTest treadWaitTest = new TreadWaitTest(object, list);
        ThreadNotifyTest threadNotifyTest = new ThreadNotifyTest(object, list);
        TreadWaitTest treadWaitTest2 = new TreadWaitTest(object, list);
        TreadWaitTest treadWaitTest3 = new TreadWaitTest(object, list);

        new Thread(threadNotifyTest).start();
        new Thread(treadWaitTest).start();
        new Thread(treadWaitTest2).start();
        new Thread(treadWaitTest3).start();
    }
}
