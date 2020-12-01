package com.hzyw.iot.netty.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
 
public class HelloClient {
 
    public void connect(String host, int port) throws Exception {
 
        EventLoopGroup workerGroup = new NioEventLoopGroup();
 
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            //new IntegerToByteEncoder(),
                            //new ByteToIntegerDecoder(),
                    		//new MyServerInitializer(),//读写心跳设置
                            new HelloClientIntHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
 
    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient();
        //client.connect("127.0.0.1", 9151);
        client.connect("127.0.0.1", 12345);
    }
}