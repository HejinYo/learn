package cn.hejinyo.learn.thread;

/**
 * @author : heshuangshuang
 * @date : 2017/12/1 15:02
 */
public class ThreadProduce implements Runnable {
    public static int product = 0;
    private int MAX_PRODUCT = 100;
    private int MIN_PRODUCT = 0;

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            produce();
            produce();
            consume();
            produce();
        }
    }

    /**
     * 生产者生产出来的产品交给店员
     */
    public synchronized void produce() {
        if (product >= MAX_PRODUCT) {
            try {
                System.out.println("产品已满,请稍候再生产");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        product++;
        System.out.println("生产者生产第" + product + "个产品.");
        notifyAll();   //通知等待区的消费者可以取出产品了
    }

    /**
     * 消费者从店员取产品
     */
    public synchronized void consume() {
        if (ThreadProduce.product <= MIN_PRODUCT) {
            try {
                System.out.println("缺货,稍候再取");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        System.out.println("消费者取走了第" + ThreadProduce.product + "个产品.");
        ThreadProduce.product--;
        notifyAll();   //通知等待去的生产者可以生产产品了
    }
}
