package nettying;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by chenkai on 2014/10/17.
 */
public class TimerServer {

    public static void main(String[] args) {

    }

    public void bind(int port) throws Exception{
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 5)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });
            ChannelFuture cf = bootstrap.bind(port).sync();

            cf.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    class TimeServerHandler extends ChannelHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf byteBuf = (ByteBuf)msg;
            byte[] comeDatas = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(comeDatas);
            String cmd = new String(comeDatas,"utf-8");
            System.out.println("Server get command: "+cmd);
            String time = cmd.equals("QUERY TIME") ? new java.util.Date(System.currentTimeMillis()).toString()
                    : "BAD COMMAND";

            ByteBuf response = Unpooled.copiedBuffer(time.getBytes());
            ctx.write(response);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//            super.channelReadComplete(ctx);
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
}
