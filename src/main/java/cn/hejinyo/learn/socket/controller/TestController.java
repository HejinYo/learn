package cn.hejinyo.learn.socket.controller;

import cn.hejinyo.learn.socket.service.SocketServer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/21 21:02
 * @apiNote :
 */
@RestController
@RequestMapping
public class TestController {

    @PostMapping(value = "/sendAll")
    public String sendAll(@RequestBody String message) {
        String msg = JSONObject.parseObject(message).getString("message");
        SocketServer.sendAll(StringUtils.isEmpty(msg) ? "新年快乐[鼓掌]" : msg);
        return "发送成功";
    }

    @PostMapping(value = "/send/{userId}")
    public String sendMessage(@RequestBody String message, @PathVariable String userId) {
        String msg = JSONObject.parseObject(message).getString("message");
        SocketServer.sendMessage(StringUtils.isEmpty(msg) ? "新年快乐[鼓掌]" : msg, userId);
        return "发送成功";
    }

}
