package cn.hejinyo.learn.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/22 22:12
 * @apiNote :
 */
@Service
public class DoService {
    private static final Logger logger = LoggerFactory.getLogger(DoService.class);
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    public String setValue() {
        String key = "testS";
        Long lockTime = null;
        while (lockTime == null) {
          /*  try {
                Thread.sleep(1000);
                Thread.yield();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            lockTime = redisLock.lock("lock:" + key, Thread.currentThread().getName());
        }
        int i = redisUtils.get("testService", Integer.class);
        int n = i + 1;
        logger.debug("当前线程:{}，取值：{},赋值:{}", Thread.currentThread().getName(), i, n);
        redisUtils.set("testService", n);
        //任务执行完毕 关闭锁
        redisLock.unlock("lock:" + key, lockTime, Thread.currentThread().getName());
        return "";

    }
}
