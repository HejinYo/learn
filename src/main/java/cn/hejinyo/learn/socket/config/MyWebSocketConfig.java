package cn.hejinyo.learn.socket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/21 20:37
 * @apiNote :
 */
@Configuration
public class MyWebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
