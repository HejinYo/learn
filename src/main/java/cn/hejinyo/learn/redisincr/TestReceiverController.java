package cn.hejinyo.learn.redisincr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/3 18:35
 */
@RestController
@RequestMapping("redis")
public class TestReceiverController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("/re")
    public String testRe() {
        redisTemplate.convertAndSend("redis:websocket:topic", "hello world");
        return "success";
    }

}
