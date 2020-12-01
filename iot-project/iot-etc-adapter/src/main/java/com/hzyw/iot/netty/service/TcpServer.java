package com.hzyw.iot.netty.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public class TcpServer {
	public static final Logger logger =  LoggerFactory.getLogger(TcpServer.class);
	public static final String IP = "127.0.0.1";
	public static final int PORT = 12345;
	public static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;	//Ĭ��
	public static final int BIZTHREADSIZE = 4;
	public static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
	public static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
	public static void run() throws Exception {
		logger.info("TCPrun..................................1111111111111....................");
		System.out.print("TCPrun..................................1111111111111....................");
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				/*pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));*/
				pipeline.addLast(new TcpServerHandler());
			}
		});

		b.bind(IP, PORT).sync();
		System.out.print("TCPrun.................................3333333....................");
	}

	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		System.out.print("main");
		TcpServer.run();
//		TcpServer.shutdown();
	}
}
