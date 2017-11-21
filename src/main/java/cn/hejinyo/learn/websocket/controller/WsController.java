package cn.hejinyo.learn.websocket.controller;

import cn.hejinyo.learn.websocket.entity.WiselyMessage;
import cn.hejinyo.learn.websocket.entity.WiselyResponse;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author : heshuangshuang
 * @date : 2017/11/21 15:47
 */
@Controller
public class WsController {
    private static final Logger logger = LoggerFactory.getLogger(WsController.class);
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    /**
     * 表示服务端可以接收客户端通过主题“/app/hello”发送过来的消息，客户端需要在主题"/topic/hello"上监听并接收服务端发回的消息
     */
    //@MessageMapping和@RequestMapping功能类似，用于设置URL映射地址，浏览器向服务器发起请求，需要通过该地址
   /* @MessageMapping("/welcome")
    //如果服务器接受到了消息，就会对订阅了@SendTo括号中的地址传送消息
    @SendTo("/topic/getResponse")
    public WiselyResponse say(WiselyMessage message) throws Exception {
        logger.debug("消息{},就是这个", message);
        System.out.println("接收到消息：" + JSON.toJSONString(message));
        return new WiselyResponse("Welcome, " + message.getName() + "!");
    }*/

    /**
     * 表示服务端可以接收客户端通过主题“/app/hello”发送过来的消息，客户端需要在主题"/topic/hello"上监听并接收服务端发回的消息
     */
    @MessageMapping("/welcome") //"/endpointWisely"为WebSocketConfig类中registerStompEndpoints()方法配置的
    @SendTo("/topic/getResponse")
    public WiselyResponse greeting(WiselyMessage message) {
        System.out.println("connected successfully....");
        System.out.println(message);
        return new WiselyResponse("Welcome, " + message.getName() + "!");
    }

    /**
     * 这里用的是@SendToUser，这就是发送给单一客户端的标志。本例中，
     * 客户端接收一对一消息的主题应该是“/user/” + 用户Id + “/message” ,这里的用户id可以是一个普通的字符串，只要每个用户端都使用自己的id并且服务端知道每个用户的id就行。
     *
     * @return
     */
    @MessageMapping("/message")
    @SendToUser("/message")
    public WiselyResponse handleSubscribe() {
        System.out.println("this is the @SubscribeMapping('/marco')");
        return new WiselyResponse("I am a msg from SubscribeMapping('/macro').");
    }

    /**
     * 测试对指定用户发送消息方法
     *
     * @return
     */
    @RequestMapping(path = "/send", method = RequestMethod.GET)
    public WiselyResponse send() {
        simpMessageSendingOperations.convertAndSendToUser("1", "/message", new WiselyResponse("I am a msg from SubscribeMapping('/macro')."));
        return new WiselyResponse("I am a msg from SubscribeMapping('/macro').");
    }
}
