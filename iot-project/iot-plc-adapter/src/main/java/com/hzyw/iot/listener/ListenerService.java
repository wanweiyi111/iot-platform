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

import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.config.NettyConfig;
import com.hzyw.iot.netty.RTUPortListener;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.utils.TpsCountUtils;

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
	private RedisService redisService; //redis工具类
    
    @Autowired
	private GateWayService gateWayService; 
    
    @Autowired
    ApplicationConfig  applicationConfig;

    @Override
    public void run(String... args) throws Exception {
    	startNettyServer();
        startScheduled();
        startSendDownConsumer();
        startTpsCountTask();
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
        //端口监听
        singleThreadExecutor.submit(
        	new Runnable() {
				@Override
				public void run() {
					//PLC
					new RTUPortListener(12345, new NioEventLoopGroup(), new NioEventLoopGroup(),redisService);
				}
			}
        );
    }
    
    /**
     * 启动消费线程获取下发的指令
     */
    public void startSendDownConsumer() {
  		gateWayService.dataSendDown(redisService);
    }
    
    
    /**
     * 启动定时任务，调度接口查询节点详情并作为状态类型的数据上报
     */
    public void startScheduled() {
		Runnable runnable = new Runnable() {
			public void run() {
				try{
					//启动定时任务，调度接口查询节点详情并作为状态类型的数据上报
			        //gateWayService.getPLCMetricInfo();
				}catch(Exception e){
					LOGGER.error("============Scheduled::查询节点详情=,异常==============",e);
					e.printStackTrace();
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		//第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		service.scheduleAtFixedRate(runnable, 10, 10*3, TimeUnit.SECONDS);  
	} 
    /**
     * 统计上一分钟的下发指令数量
     */
    public void startTpsCountTask() {
		Runnable runnable = new Runnable() {
			public void run() {
				try{
					Calendar beforeTime = Calendar.getInstance();
					beforeTime.add(Calendar.MINUTE, -1);// 前1分钟
					Date beforeD = beforeTime.getTime();
					String before1 = new SimpleDateFormat("yyyyMMddHHmm").format(beforeD);
					String key = TpsCountUtils.plcSendDown + before1;
 			        long tps = TpsCountUtils.getTpsCountPlcSendDown(redisService, key);
 			       LOGGER.info("==========countPlcSenddown::统计上一分钟的下发指令数量===  time/tps= ," + before1+"/"+tps);
				}catch(Exception e){
					System.out.println("");
					LOGGER.error("==========countPlcSenddown::统计上一分钟的下发指令数量=,异常===",e);
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		//每分钟统计一次，统计上一分钟的下发指令数量
		service.scheduleAtFixedRate(runnable, 10, 60, TimeUnit.SECONDS);  
	} 
    
}