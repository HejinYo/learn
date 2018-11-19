package cn.hejinyo.learn.base;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/19 14:02
 */
public class TreadTest {

    public static void main(String[] args) {
        Thread thread = new Thread(new Thread1());
        thread.run();
        thread.start();

    }


    private static class Thread1 implements Runnable {
        @Override
        public void run() {
            System.out.println("this is Thread1");
            System.out.println(Thread.currentThread().getName());
        }
    }
}
