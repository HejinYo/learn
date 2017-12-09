package cn.hejinyo.learn.netty.server.common;

import java.net.InetSocketAddress;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

@Component
public class ChatServer {
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    /**
     * NioEventLoopGroup 和NioEventLoop 都可以.但是前者使用的是线程池. 其实bossgroup如果服务端开启的是一个端口(大部分都是一个),单线程即可.
     * worker大部分情况需要多线程处理了 .因为 一个eventloop绑定了一个selector,事件都是通过selector轮询处理的. 一万个情况让一个select处理和让100个selector处理
     * 肯定是多线程效率要高一些(因为有io).
     * boss用来监控tcp链接,执行 server.accept()操作
     * worker用来处理io事件,处理事件的读写到业务逻辑处理等后续操作.
     * 默认线程数是 cpu核心数的2倍. 但是也可以通过
     * -Dio.netty.eventLoopThreads  参数在服务端启动的时候指定 .
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                //通道类型NIO websocket
                .channel(NioServerSocketChannel.class)
                //协议具体实现
                .childHandler(new ChatServerInitializer(channelGroup))
                //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
                .option(ChannelOption.SO_BACKLOG, 128)
                //长连接，心跳检测
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }

        channelGroup.close();
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9090);
        ChannelFuture future = server.start(address);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.destroy();
            }
        });

        future.channel().closeFuture().syncUninterruptibly();
    }
}
