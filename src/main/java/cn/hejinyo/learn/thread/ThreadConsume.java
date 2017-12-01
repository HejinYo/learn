package cn.hejinyo.learn.thread;

/**
 * @author : heshuangshuang
 * @date : 2017/12/1 15:03
 */
public class ThreadConsume implements Runnable {
    private int MIN_PRODUCT = 0;

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            consume();
        }
    }

    /**
     * 消费者从店员取产品
     */
    public synchronized void consume() {
        if (ThreadProduce.product <= MIN_PRODUCT) {
            try {
                wait();
                System.out.println("缺货,稍候再取");
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
