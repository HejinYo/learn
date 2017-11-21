package cn.hejinyo.learn.websocket.entity;

import lombok.Data;

/**
 * 服务器向浏览器传送消息，用该类进行传送
 *
 * @author : heshuangshuang
 * @date : 2017/11/21 15:46
 */
@Data
public class WiselyResponse {
    private String responseMessage;

    public WiselyResponse(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}