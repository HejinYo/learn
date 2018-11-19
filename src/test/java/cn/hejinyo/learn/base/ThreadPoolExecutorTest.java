package cn.hejinyo.learn.base;

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

        Future future = executor.submit(() -> 12);
        System.out.println(future.get());

    }
}
