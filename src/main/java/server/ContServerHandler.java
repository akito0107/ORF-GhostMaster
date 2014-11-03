package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.*;
import demo1.SIFTThread;

import java.io.FileOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by aqram on 11/3/14.
 */
public class ContServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    LinkedBlockingQueue mQueue;

    public ContServerHandler(LinkedBlockingQueue queue){
        super();
        mQueue = queue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {

        final HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);


        while (decoder.hasNext()) {
            final InterfaceHttpData data = decoder.next();
            if (data != null) {
                try {
                    //Attribute attribute = (Attribute) data;
                    MemoryFileUpload file = (MemoryFileUpload) data;
                    Thread t = new Thread(new SIFTThread(file.get(), mQueue));
                    t.start();
                    writeToTestFile(file.get());
                    //System.out.println(attribute.getValue());
                } finally {
                    data.release();
                }
            }
        }

    }

    private void writeToTestFile(byte[] data){

        FileOutputStream fos = null;

        try{
            // create new file output stream
            fos=new FileOutputStream("test_outs/test1.jpeg");

            // writes bytes to the output stream
            fos.write(data);

            // flushes the content to the underlying stream
            fos.flush();

            fos.close();
            // create new file input stream
        }catch(Exception ex) {
            // if an error occurs
            ex.printStackTrace();
        }
    }
}
