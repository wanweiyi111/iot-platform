package com.hzyw.iot.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.hzyw.iot.config.NettyConfig;
import com.hzyw.iot.netty.RTUPortListener;
import com.hzyw.iot.service.TomdaChargerService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.service.SwiftlinkService;
import com.hzyw.iot.utils.IotInfoConstant;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 
 * 监听服务
 * 
 * @author Administrator
 *
 */
@Component
public class ListenerService implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerService.class);
	private static ExecutorService singleThreadExecutor = Executors.newFixedThreadPool(2);
	@Autowired
	private NettyConfig nettyConfig;

	@Autowired
	private RedisService redisService;

	@Override
	public void run(String... args) throws Exception {
		// gateWayService.wifi();//wifi
	}

	/**
	 * 启动消费线程获取下发的指令
	 */
	public void startNettyServer() {
		final RedisService redisService = this.redisService;
		LOGGER.info("Netty Start..");
		Integer port = nettyConfig.getPort();
		if (port == null) {
			LOGGER.warn("port is empty.");
			return;
		}
		// 端口监听
		singleThreadExecutor.submit(new Runnable() {
			@Override
			public void run() {
				// PLC
				new RTUPortListener(12345, new NioEventLoopGroup(), new NioEventLoopGroup(), redisService);
			}
		});
	}

}