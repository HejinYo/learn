package cn.hejinyo.learn.java8;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/17 23:43
 */
public class TestThread {
    public static void main(String[] args) {
        new Thread(() -> System.out.println("1231")).start();
        new Thread(() -> System.out.println("1232")).start();
        new Thread(() -> System.out.println("1233")).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ddd");
            }
        }).start();
    }

}
