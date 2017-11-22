package cn.hejinyo.learn.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/22 21:33
 * @apiNote :
 */
@RestController
@RequestMapping("/redis")
public class TestRedis {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private DoService doService;

    @RequestMapping("/{key}/{value}")
    public String test1(@PathVariable(value = "key") String key, @PathVariable(value = "value") String value) {
        //Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
       /* Long lockTime = redisLock.lock("lock:" + key, Thread.currentThread().getName());
        if (lockTime != null) {
            redisTemplate.opsForValue().set(key, value);
            //任务执行完毕 关闭锁
            //redisLock.unlock("lock:" + key, lockTime, Thread.currentThread().getName());
        }*/
        for (int i = 0; i < 50; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doService.setValue();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doService.setValue();
                }
            }).start();
        }

        return "result";
    }
}
