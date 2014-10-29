import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * サーバ側メインクラス
 */
public class EchoServer {
    public static void create_server(int port) {
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

                pipeline.addLast("handler", new EchoServerHandler()); // server

                return pipeline;
            }
        });

        bootstrap.bind(new InetSocketAddress(port)); // 9999番ポートでlisten
    }
}
