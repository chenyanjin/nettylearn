package nettying;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Created by chenkai on 2014/10/23.
 */
public class EchoObjectClient {


    public static void main(String[] args) throws Exception {

        // Parse options.
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        Scanner scanner = new Scanner(System.in);
        final String clientId = scanner.next();

        new EchoObjectClient(host, port,clientId).run();
    }

    private final String host;
    private final int port;
    final String clientId;
    public EchoObjectClient(String host, int port,String clientId) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEchoClientHandler());
                        }
                    });

            // Start the connection attempt.
            b.connect(host, port).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    class ObjectEchoClientHandler extends ChannelHandlerAdapter {

        /**
         * Creates a client-side handler.
         */
        public ObjectEchoClientHandler() {

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
            final ChannelHandlerContext cctx = ctx;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    scanner.findInLine("(\\w+)\\s(.+)");
                    MatchResult mr = scanner.match();

                    String dest = mr.group(1);
                    String content = mr.group(2);
                    cctx.writeAndFlush(new Message(2,content,clientId,dest));
                }
            }).start();

        }


//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//            ctx.flush();
//        }

        @Override
        public void exceptionCaught(
                ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }
}
