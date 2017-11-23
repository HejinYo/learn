package cn.hejinyo.learn.redis;

import cn.hejinyo.learn.common.ThreadPoolUtil;
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
    private DoService doService;

    @RequestMapping("/{count}")
    public String test1(@PathVariable(value = "count") int count) {
        for (int i = 0; i < count; i++) {
            ThreadPoolUtil.getInstance().execute(() -> {
                doService.setValue();
                doService.setValue();
                doService.setValue();
            });
            ThreadPoolUtil.getInstance().execute(() -> {
                doService.setValue();
                doService.setValue();
                doService.setValue();
            });
        }

        return "result";
    }
}
