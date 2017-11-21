package cn.hejinyo.learn.websocket.entity;

import lombok.Data;

/**
 * 浏览器向服务器传送消息，用该类进行接收
 *
 * @author : heshuangshuang
 * @date : 2017/11/21 15:45
 */
@Data
public class WiselyMessage {
    private String name;

    @Override
    public String toString() {
        return "WiselyMessage{" +
                "name='" + name + '\'' +
                '}';
    }
}
