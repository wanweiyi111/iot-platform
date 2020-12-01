package com.hzyw.iot.netty.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
 
/**
 * 主要思路：
 * 实现了一个编码器IntegerToByteEncoder和一个解码器ByteToIntegerDecoder
 * 客户端直接发送一个数字
 * 服务器端接收后向客户端发送一个数字
 * 在这过程中可以看到解码器和编码器所起的作用
 */
public class HelloServer {
    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 注册handler
                            ch.pipeline().addLast(
                            		//new LengthFieldBasedFrameDecoder(1024, 4, 4, 2, 0),//解码方式:https://blog.csdn.net/u014801432/article/details/81909902
                                    new HelloServerInHandler());
                        }
                        
                    });
 
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
 
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
 
    public static void main(String[] args) throws Exception {
        HelloServer server = new HelloServer();
        server.start(9151);
    }
}
