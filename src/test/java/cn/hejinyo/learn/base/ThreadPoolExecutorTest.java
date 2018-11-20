package cn.hejinyo.learn.base;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/19 23:35
 */
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


        Integer i1 = 40;
        Integer i2 = 40;
        Integer i3 = 0;
        Integer i4 = new Integer(40);
        Integer i5 = new Integer(40);
        Integer i6 = new Integer(0);

        System.out.println("i1=i2   " + (i1 == i2));
        System.out.println("i1=i2+i3   " + (i1 == i2 + i3));
        System.out.println("i1=i4   " + (i1 == i4));
        System.out.println("i4=i5   " + (i4 == i5));
        System.out.println("i4=i5+i6   " + (i4 == i5 + i6));
        System.out.println("40=i5+i6   " + (40 == i5 + i6));

    }
}
