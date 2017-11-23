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

    public String setValue() {
        String key = "testS";
        Long i = redisUtils.increment("testService", 1L, null);
        if (i == 50) {
           // i = redisUtils.increment("testService", -50L, null);
            System.out.println("===========delete===================");
        }
        logger.debug("当前线程:{}，取值：{},赋值:{}", Thread.currentThread().getName(), i - 1, i);
        return "";
    }

    public static void main(String[] arge) {
        Long i = 20L;
        System.out.println(i.equals(20L));
    }

}
