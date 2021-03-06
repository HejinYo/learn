package cn.hejinyo.learn.netty.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2017/12/7 23:44
 */
public class AppWebSocketNettyServer {
/*
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AppWebSocketNettyServer() {
        super();
        // TODO Auto-generated constructor stub
    }


    public void startNettyServer() {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();

                try {

                    ServerBootstrap bootstrap = new ServerBootstrap();


                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024); //连接数
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);  //不延迟，消息立即发送
//                  bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);  //超时时间
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //长连接
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {

                            ChannelPipeline p = socketChannel.pipeline();

                            WebSocketIpTableHandler ipTableHandler = new WebSocketIpTableHandler();
                            p.addLast(ipTableHandler);

                            //请求处理类
                            WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler();

                            //消息处理类
                            WebSocketMessageManager webSocketMessageManager = new WebSocketMessageManager();
                            webSocketServerHandler.setWebSocketMessageManager(webSocketMessageManager);

                            p.addLast("http-codec", new HttpServerCodec());
                            p.addLast("aggregator", new HttpObjectAggregator(65536));
                            p.addLast("http-chunked", new ChunkedWriteHandler());
                            p.addLast("handler", webSocketServerHandler);

                        }
                    });
                    ChannelFuture f = bootstrap.bind(18888).sync();


                    if (f.isSuccess()) {
                        System.out.println("启动Netty服务成功 !! ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                  boss.shutdownGracefully();
//                  worker.shutdownGracefully();
                }
                System.out.println(" netty starting ... ");

            }
        });



    }*/


}
