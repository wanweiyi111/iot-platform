package com.hzyw.iot.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.netty.channelhandler.CommandHandler;
import com.hzyw.iot.netty.processor.*;
import com.hzyw.iot.netty.processor.Impl.IDataProcessor;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.constant.ProtocalAdapter;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.utils.TpsCountUtils;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.ItemInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;
import com.hzyw.iot.vo.dc.RTUInfo;
import com.hzyw.iot.vo.dc.enums.EMqExchange;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

 
/**
 * 处理分发
 * @author Administrator
 */
@Component
public class DataSendDownHandler  {
    private static final Logger logger = LoggerFactory.getLogger(DataSendDownHandler.class);
    
    /**
     * IO密集型任务  = 一般为2*CPU核心数（常出现于线程中：数据库数据交互、文件上传下载、网络数据传输等等）
     * CPU密集型任务 = 一般为CPU核心数+1（常出现于线程中：复杂算法）
     * 混合型任务  = 视机器配置和复杂度自测而定
     */
    private static int corePoolSize = 10;//Runtime.getRuntime().availableProcessors();
   
    /**
     * public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue)
     * corePoolSize用于指定核心线程数量
     * maximumPoolSize指定最大线程数
     * keepAliveTime和TimeUnit指定线程空闲后的最大存活时间
     * workQueue则是线程池的缓冲队列,还未执行的线程会在队列中等待
     * 监控队列长度，确保队列有界
     * 不当的线程池大小会使得处理速度变慢，稳定性下降，并且导致内存泄露。如果配置的线程过少，则队列会持续变大，消耗过多内存。
     * 而过多的线程又会 由于频繁的上下文切换导致整个系统的速度变缓——殊途而同归。队列的长度至关重要，它必须得是有界的，这样如果线程池不堪重负了它可以暂时拒绝掉新的请求。
     * ExecutorService 默认的实现是一个无界的 LinkedBlockingQueue。
     */
    public static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, 50, 30*1000, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
    
    public DataSendDownHandler() {
    }
    
    public static void process(final String message,final RedisService redisService,ApplicationConfig applicationConfig) {
    	Runnable requestTask = new Runnable() {
			@SuppressWarnings({ "unused", "static-access" })
			public void run() {
				ProtocalAdapter protocalAdapter = new ProtocalAdapter();
				String messageId=null,plcCommand,plc_id;
				try {
					JSONObject sendMode = JSON.parseObject(message);
					messageId = sendMode.getString("msgId");
					plc_id = sendMode.getString("gwId");
					String serverPort_plcSn = PlcProtocolsBusiness.getPortByPlcID(plc_id)+PlcProtocolsBusiness.getPlcSnByPlcID(plc_id);
					if(serverPort_plcSn == null){
						logger.warn("====dataSendDownConsumer_request_"+messageId+"==, Not found serverPort_plcSn from Gloable cache, 下发失败! ,,messageVo/{}", message);
						return;
					}
					if (!GlobalInfo.SN_CHANNEL_INFO_MAP.containsKey(serverPort_plcSn)){
						logger.warn("====dataSendDownConsumer_request_"+messageId+"==, Not found this Plc_sn's channel from Gloable cache,pls check had login, 下发失败! ,,messageVo/{}", message);
						return;
					}
					// --这里已经改成并行的消费处理 ，如果后面确认并行下发设备过频繁而设备会出现问题的华,可以在CommandHandler.writeCommand里面做处理
					logger.info("=======dataSendDownConsumer_request_"+messageId+"==下发指令/消费 start =====/messageVo="+message);
					long FIRST_TIME = System.currentTimeMillis();
					//plcCommand = protocalAdapter.messageRequest(sendMode);
					//logger.info("==========dataSendDownConsumer_request_消息ID:"+messageId+"==protocalAdapter适配后, 下发的报文:"+plcCommand);
					//logger.info("=======dataSendDownConsumer_request_"+messageId+"==protocalAdapter complete. ===/ts="+(System.currentTimeMillis()-FIRST_TIME));
					//if(!"".equals(plcCommand)){
					//	logger.info("===******=======下发的报文:"+plcCommand+", PLC执行中。。。");
					//	CommandHandler.writeCommand(serverPort_plcSn, plcCommand, messageId);
					//	logger.info("====******======下发的报文:"+plcCommand+", PLC执行完成。。。");
					//}
					
					CommandHandler.writeCommandByRequestMessageVO(message, applicationConfig); //提供了另一种下发方式
					logger.info("=======dataSendDownConsumer_request_"+messageId+"==下发指令/消费 end ===/ts="+(System.currentTimeMillis()-FIRST_TIME));
					 
					
					TpsCountUtils.plcSendDownSetIncr(redisService, TpsCountUtils.plcSendDown, 60*3);//统计1分钟请求量    
				} catch (Exception e) {
					logger.error("====dataSendDownConsumer_request_"+messageId+"==下发指令消费/并进入protocalAdapter处理模块 处理异常！  " ,e);
				}
			}
		};
		executor.submit(requestTask);
    } 
}
