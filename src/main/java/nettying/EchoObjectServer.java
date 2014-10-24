package nettying;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenkai on 2014/10/23.
 */
public class EchoObjectServer {

    public static void main(String[] args) throws Exception {
        new EchoObjectServer(9342).start();
    }

    private final int port;

    public EchoObjectServer(int port) {
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,work)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEchoServerHandler());
                        }
                    });
            bootstrap.bind(port).sync().channel().closeFuture().sync();

        }finally {
            boss.shutdownGracefully().sync();
            work.shutdownGracefully().sync();
        }
    }

    class ObjectEchoServerHandler extends ChannelHandlerAdapter{
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            System.out.println("channel id="+ctx.channel().id()+" is coming!");
//            ClientPool.clients.put(ctx.channel().id(),ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            Message m = (Message)msg;
            if(m.getMsgType()==1){//客户端连接登记
                System.out.println("client "+m.getMsgSource() +" regist");
                ClientPool.clients.put(m.getMsgSource(),ctx.channel());
                ctx.writeAndFlush(new Message(2, "hello client.", "SERVER", m.getMsgSource()));
            }else{
                Channel destChannel = (Channel)ClientPool.clients.get(m.getMsgDest());
                if(destChannel!=null) {
                    if(destChannel.isActive())
                        destChannel.writeAndFlush(msg);
                    else{
                        ctx.writeAndFlush(new Message(2, m.getMsgDest()+" is offline!", "SERVER", m.getMsgSource()));
                        destChannel.closeFuture();
//                        ClientPool.clients.remove(m.getMsgDest());
                    }
                }else{
                    ctx.writeAndFlush(new Message(2, m.getMsgDest()+" is offline!", "SERVER", m.getMsgSource()));
                }
            }

        }

//        @Override
//        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
////            super.channelReadComplete(ctx);
//            ctx.flush();
//        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            super.exceptionCaught(ctx, cause);
            ctx.close();
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
    }
}
