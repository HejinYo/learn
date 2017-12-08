package cn.hejinyo.learn.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/7 23:09
 */
public class WebSocketServer {
    private final ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(workerGroup).channel(NioServerSocketChannel.class).childHandler(createInitializer(group));

        ChannelFuture f = boot.bind(address).syncUninterruptibly();
        channel = f.channel();
        return f;
    }

    protected ChannelHandler createInitializer(ChannelGroup group2) {
        return new ChatServerInitializer(group2);
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        group.close();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        final WebSocketServer server = new WebSocketServer();
        ChannelFuture f = server.start(new InetSocketAddress(2048));
        System.out.println("server start................");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.destroy();
            }
        });
        f.channel().closeFuture().syncUninterruptibly();
    }

    private static WebSocketServer instance;

    private WebSocketServer() {
    }

    public static synchronized WebSocketServer getInstance() {// 懒汉，线程安全
        if (instance == null) {
            instance = new WebSocketServer();
        }
        return instance;
    }

    public void running() {
        if (instance != null) {

            String port = null;
            port = "9090";//获取端口号
            if (null == port || port.length() < 0 || !StringUtils.isNumeric(port)) {
                port = "18080";
            }
            instance.start(new InetSocketAddress(Integer.valueOf(port)));
            //ChannelFuture f =
            System.out.println("----------------------------------------WEBSOCKET SERVER START----------------------------------------");
            /*Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    instance.destroy();
                }
            });
            f.channel().closeFuture().syncUninterruptibly();*/
        }
    }
}
