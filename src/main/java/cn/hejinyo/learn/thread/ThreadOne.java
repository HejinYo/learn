package cn.hejinyo.learn.thread;

/**
 * @author : heshuangshuang
 * @date : 2017/12/1 14:49
 */
public class ThreadOne {

    public static void main(String[] args) {
        ThreadProduce produce = new ThreadProduce();
       // ThreadConsume consume = new ThreadConsume();
        Thread threadProduce = new Thread(produce);
      //  Thread threadConsume = new Thread(consume);
        threadProduce.start();
        //threadConsume.start();
    }


}
