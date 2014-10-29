import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.util.CharsetUtil;

import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * サーバ側アプリケーションロジック
 */
public class EchoServerHandler extends SimpleChannelHandler {
    /**
     * クライアントから電文を受信した際に呼び出されるメソッド
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {

        HttpRequest request = (HttpRequest)event.getMessage();

        ChannelBuffer messageObject = ((HttpRequest)event.getMessage()).getContent();


        if (messageObject instanceof BigEndianHeapChannelBuffer) {

            try {

                BigEndianHeapChannelBuffer bigEndianHeapChannelBuffer = (BigEndianHeapChannelBuffer) messageObject;

                byte[] byteArray  = new byte[bigEndianHeapChannelBuffer.readableBytes()];

                bigEndianHeapChannelBuffer.readBytes(byteArray);

                System.out.print(" Message = "+new String(byteArray));

                bigEndianHeapChannelBuffer.clear();


            } catch (Exception e) {

                System.out.println("Exception in MessageReceived...");

                e.printStackTrace();

            }
        }


        ChannelBuffer buf = request.getContent();
        System.out.println(buf.toString());

        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false),request);
            try {
                for(InterfaceHttpData data : decoder.getBodyHttpDatas()){
                    System.out.println(data.toString());
                    System.out.println("  -------------------- ");
                }
                System.out.println("  -------=============-------- ");

            } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException e) {
                e.printStackTrace();
            }
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            e.printStackTrace();
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException e) {
            e.printStackTrace();
        }

        String msg = event.getMessage().toString();
        System.out.println(msg);
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setContent(ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        event.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
}

