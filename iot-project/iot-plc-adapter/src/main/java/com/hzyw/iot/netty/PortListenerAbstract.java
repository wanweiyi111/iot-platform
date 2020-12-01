package com.hzyw.iot.netty;

import com.hzyw.iot.netty.channelhandler.ChannelManagerHandler;
import com.hzyw.iot.netty.channelhandler.ExceptionHandler;
import com.hzyw.iot.netty.channelhandler.HeartBeatHandler;
import com.hzyw.iot.service.RedisService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 端口监听
 *
 */
abstract class PortListenerAbstract {
	private static final Logger LOGGER = LoggerFactory.getLogger(PortListenerAbstract.class);
	/**
	 * 端口
	 */
	private int port;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private RedisService redisService;

	PortListenerAbstract(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup,RedisService redisService) {
		this.port = port;
		this.bossGroup = bossGroup;
		this.workerGroup = workerGroup;
		this.redisService = redisService;
	}

	/**
	 * channelhandler 设置 抽象方法
	 *
	 * @return
	 */
	abstract ChannelHandler settingChannelInitializerHandler();

	/**
	 * 接口绑定
	 */
	void bind() {
		if (port == 0 || this.workerGroup == null || this.bossGroup == null) {
			throw new RuntimeException("'port','bossGroup' and 'workerGroup' had to be initialized before bind.");
		}

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)// zhu
					.childHandler(this.settingChannelInitializerHandler());
			ChannelFuture channelFuture = bootstrap.bind(this.port).sync();
			LOGGER.info("port:{} bind successful.", port);
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * 基础
	 */
	abstract class BaseHandler extends ChannelInitializer<SocketChannel> {
		/**
		 * 拓展
		 *
		 * @param pipeline
		 * @return
		 */
		abstract ChannelPipeline extHandler(ChannelPipeline pipeline);

		@Override
		protected void initChannel(SocketChannel socketChannel) {
			ChannelPipeline pipeline = socketChannel.pipeline();
			int x = port;
			pipeline
					// log
					.addLast("logging", new LoggingHandler(LogLevel.INFO))
					// 心跳检测  第一个参数是指定读操作空闲秒数，第二个参数是指定写操作的空闲秒数，第三个参数是指定读写空闲秒数，当有操作操作超出指定空闲秒数时，便会触发HeartBeatHandler::UserEventTriggered事件
					 .addLast(new IdleStateHandler(11*2, 0, 0, TimeUnit.SECONDS))//PLC设备上报心跳的频率
					 .addLast(new HeartBeatHandler())
					// 链路管理
					.addLast(new ChannelManagerHandler());
			// 拓展
			extHandler(socketChannel.pipeline());
			// 异常管理
			pipeline.addLast(new ExceptionHandler());
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}
	
	
	
}
