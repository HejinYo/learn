package cn.hejinyo.learn.netty.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;

import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/7 23:12
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //private LoginTimeService loginTimeService = SpringContextHolder.getBean("loginTimeServiceImpl");
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        super();
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);
            // group.writeAndFlush("");
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        String userId = (String) incoming.attr(AttributeKey.valueOf(incoming.id().asShortText())).get();
        // UserIdToWebSocketChannelShare.userIdToWebSocketChannelMap.remove(userId);// 删除缓存的通道
        //loginTimeService.outLine(userId, new Date());// 下线通过
    }

}
