package stream.agent;

import memsys.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stream.agent.ui.SingleVideoDisplayWindow;
import stream.handler.StreamFrameListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StreamClient {
    /**
     * @author kerr
     */


    private final static Dimension dimension = new Dimension(Constants.WIDTH, Constants.HEIGHT);
    private final static SingleVideoDisplayWindow displayWindow = new SingleVideoDisplayWindow("Stream example", dimension);
    protected final static Logger logger = LoggerFactory.getLogger(StreamClient.class);

    private final LinkedBlockingQueue<BufferedImage> inqueue;
    private final LinkedBlockingQueue<BufferedImage> outqueue;

    public StreamClient(LinkedBlockingQueue<BufferedImage> in, LinkedBlockingQueue<BufferedImage> out) {
        inqueue = in;
        outqueue = out;
    }

    public void run() {
        //setup the videoWindow
        displayWindow.setVisible(true);

        //setup the connection
        logger.info("setup dimension :{}", dimension);
        StreamClientAgent clientAgent = new StreamClientAgent(new StreamFrameListenerIMPL(), dimension);
        clientAgent.connect(new InetSocketAddress("localhost", 20000));
    }


    class StreamFrameListenerIMPL implements StreamFrameListener {
        private volatile long count = 0;

        private BufferedImage buf;

        @Override
        public void onFrameReceived(BufferedImage image) {
            BufferedImage out = null;

            if (count % 2 == 0) {
                try {
                    inqueue.put(image);
                    out = outqueue.poll(50, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // logger.info("frame received :{}",count++);
                if (out == null) {
                    displayWindow.updateImage(image);
                } else {
                    buf=out;
                    displayWindow.updateImage(out);
                }
            }
            count++;
        }

    }

}
