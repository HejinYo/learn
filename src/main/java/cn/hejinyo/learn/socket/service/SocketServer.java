package cn.hejinyo.learn.socket.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/21 20:40
 * @apiNote :
 */
//@ServerEndpoint(value = "/socket/{userId}")
//@Component
public class SocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private Session session;
    private static HashMap<String, Session> sessionPool = new HashMap<>();
    private static HashMap<String, String> sessionIds = new HashMap<>();

    @OnOpen
    public void open(Session session, @PathParam(value = "userId") String userId) {
        logger.debug("open");
        this.session = session;
        if (sessionPool.containsKey(userId)) {
            try {
                sessionPool.get(userId).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessionPool.put(userId, session);
        sessionIds.put(session.getId(), userId);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.debug("当前发送人sessionId为：{},发送内容为：{}", session.getId(), message);
        try {
            JSONObject jsonObject = JSONObject.parseObject(message);
            String userId = jsonObject.getString("userId");
            String msg = jsonObject.getString("msg");
            sendMessage(msg, userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @OnClose
    public void onClose() {
        logger.debug("onClose");
        sessionPool.remove(sessionIds.get(session.getId()));
        sessionIds.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.debug("onError");
        error.printStackTrace();
    }

    public static void sendMessage(String message, String userId) {
        Session s = sessionPool.get(userId);
        System.out.println("=======" + s);
        if (s != null) {
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOnlineNum() {
        return sessionPool.size();
    }

    public static String getOnlineUsers() {
        StringBuffer users = new StringBuffer();
        sessionIds.forEach((key, value) -> users.append(value));
        return users.toString();
    }

    public static void sendAll(String msg) {
        sessionIds.forEach((key, value) -> sendMessage(msg, value));
    }
}
