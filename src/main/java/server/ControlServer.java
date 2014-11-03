package server;


import io.netty.bootstrap.ChannelFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * サーバ側メインクラス
 */
public class ControlServer {
    public static void createServer(final int PORT, final LinkedBlockingQueue queue) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 100)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline p = ch.pipeline();
                                    //p.addLast(new LoggingHandler(LogLevel.INFO));
                                    //p.addLast(new StringEncoder());
                                    //p.addLast(new HttpResponseEncoder());
                                    //p.addLast(new HttpRequestDecoder());
                                    //p.addLast(new StringDecoder());
                                    p.addLast(new HttpServerCodec());
                                    p.addLast(new HttpObjectAggregator(1048576));
                                    p.addLast(new ContServerHandler(queue));
                                    //p.addLast(new HttpUploadServerHandler());
                                }
                            });

                    // Start the server.
                    ChannelFuture f = b.bind(PORT).sync();

                    // Wait until the server socket is closed.
                    f.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Shut down all event loops to terminate all threads.
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }

            }
        });

        t.start();
       /*

        System.out.println("Control Server INIT");
        ChannelFactory factory =
                new NioServerSocketChannelFactory( // server
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()
                );

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();

                // Downstream(送信)
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("frameEncoder", new HttpResponseEncoder());


                // Upstream(受信)
                pipeline.addLast("frameDecoder", new HttpRequestDecoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                // Application Logic Handler

                pipeline.addLast("handler", new ControlServerHandler()); // server

                return pipeline;
            }
        });

        bootstrap.bind(new InetSocketAddress(port)); // 9999番ポートでlisten
        System.out.println("Control Server Started");
        */
    }
}
