package cn.hejinyo.learn.redisincr;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息监听者
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/3 18:28
 */
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch;

    @Autowired
    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) {
        LOGGER.info("接收到消息： <" + message + ">");
        latch.countDown();
    }
}
