package nettying;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by chenkai on 2014/10/17.
 */
public class TimeClient {

    public void connect(int port,String host) throws Exception{

        EventLoopGroup work  = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host,port).sync();
            future.channel().closeFuture().sync();
        }finally {
            work.shutdownGracefully();
        }
    }

    class TimeClientHandler extends ChannelHandlerAdapter{
        private final ByteBuf firstMsg;

        TimeClientHandler() {
            byte[] cmd = "QUERY TIME".getBytes();
            firstMsg = Unpooled.buffer(cmd.length);
            firstMsg.writeBytes(cmd);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            ctx.writeAndFlush(firstMsg);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
            ByteBuf response = (ByteBuf)msg;
            byte[] rep = new byte[response.readableBytes()];
            response.readBytes(rep);
            String time = new String(rep,"utf-8");
            System.out.println("NOW IS : "+time);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
}
