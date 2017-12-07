package cn.hejinyo.learn.netty.server.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import cn.hejinyo.learn.netty.server.common.ChatConstants;
import cn.hejinyo.learn.netty.server.module.UserInfo;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/chat")
public class ChatController {

    // 跳转到交谈聊天页面
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView talk(String token, Model model) {
        ModelAndView modelAndView = new ModelAndView("/chat");
        modelAndView.addObject("token", token);
        System.out.println(token);
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "users", method = RequestMethod.GET, produces = {"application/json; charset=UTF-8", "text/plain"})
    public String users(String token) {
        Map<String, UserInfo> onlines = ChatConstants.onlines;
        UserInfo cur = onlines.get(token);

        Map<String, Object> map = new HashMap<>(2);
        map.put("curName", cur != null ? cur.getCode() : "");
        map.put("users", onlines);
        return JSON.toJSONString(map);
    }
}
