package communicate;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import nettying.Message;

/**
 * Created by chenkai on 2014/10/24.
 */
public class ServerConnectable implements Runnable {

    private final String host;
    private final int port;
    private final String clientId;
    private CommunicateClient cc;
    public ServerConnectable(String host, int port, String clientId,CommunicateClient cc) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.cc = cc;
    }


    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            final String cid = clientId;
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEchoClientHandler(cid));
                        }
                    });

            // Start the connection attempt.
            ChannelFuture cf = b.connect(host, port).sync();
            cc.getSessions().put(clientId,cf.channel());
            cc.unlock();
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }


    class ObjectEchoClientHandler extends ChannelHandlerAdapter {
        private String clientId;
        /**
         * Creates a client-side handler.
         */
        public ObjectEchoClientHandler(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // Send the first message if this handler is a client-side handler.
            ctx.writeAndFlush(new Message(1,"hello server",clientId));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.out.println("client channelInactive");
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            super.disconnect(ctx, promise);
            System.out.println("client disconnect");
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            super.close(ctx, promise);
            System.out.println("client close");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // Echo back the received object to the server.
//            ctx.write(msg);
            Message m = (Message)msg;

            System.out.println("[RECEIVE MSG]:" + m.getMsgSource() + " say to " + m.getMsgDest() + ": " + m.getMsgContent());
        }

        @Override
        public void exceptionCaught(
                ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
