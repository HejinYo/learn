package cn.hejinyo.learn.netty.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/7 23:11
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    //private LoginTimeService loginTimeService = SpringContextHolder.getBean("loginTimeServiceImpl");
    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        super();
        this.wsUri = wsUri;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        if (wsUri.equalsIgnoreCase(msg.getUri().substring(0, 3))) {
            String userId = findUserIdByUri(msg.getUri());
            if (userId != null && userId.trim() != null && userId.trim().length() > 0) {
                ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asShortText())).set(userId);// 写userid值
                //UserIdToWebSocketChannelShare.userIdToWebSocketChannelMap.put(userId, ctx.channel()); // 用户Id与Channel绑定
                //loginTimeService.onLine(userId, new Date());// 统计上线记录

            } else {
            }// 没有获取到用户Id
            ctx.fireChannelRead(msg.setUri(wsUri).retain());
        }
    }

    private String findUserIdByUri(String uri) {// 通过Uid获取用户Id--uri中包含userId
        String userId = "";
        try {
            userId = uri.substring(uri.indexOf("userId") + 7);
            if (userId != null && userId.trim() != null && userId.trim().length() > 0) {
                userId = userId.trim();
            }
        } catch (Exception e) {
        }
        return userId;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace(System.err);
    }
}
