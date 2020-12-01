package com.hzyw.iot.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.netty.channelhandler.CommandHandler;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.constant.ProtocalAdapter;
import com.hzyw.iot.utils.SendKafkaUtils;
import com.hzyw.iot.utils.TpsCountUtils;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;
import com.hzyw.iot.vo.dataaccess.ResultMessageVO;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;

/**
 * 1，从KAFKA获取下发数据（实时获取） 2, 判断服务是否在线 3，判断设备是否已上线 4，发送到MQTT服务器，关闭mqtt连接
 * 5，如果存在异常，检查看KAFKA是否已经提交offset
 */
public class DataSendDownConsumer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(DataSendDownConsumer.class);
	
	private KafkaCommon kafkaCommon;
	
	private ApplicationConfig applicationConfig;
	
	
	private GateWayService gateWayService;
	
	private RedisService redisService;

	public DataSendDownConsumer() {
	}

	public DataSendDownConsumer(KafkaCommon kafkaCommon, ApplicationConfig applicationConfig,
			 GateWayService gateWayService,RedisService redisService) {
		this.kafkaCommon = kafkaCommon;
		this.applicationConfig = applicationConfig;
		this.gateWayService = gateWayService;
		this.redisService = redisService;
	}

	/**
	 * 获取kafka的数据
	 */
	@SuppressWarnings("static-access")
	public void consumerProcess() {
		//获取kafka主题和消费组
		String topic = applicationConfig.plcOrder(); //  "testbyzhu" ;
		String consumerGroup = applicationConfig.getKafkaPlcConsumerGroup();
		try {
			KafkaConsumer<String, String> consumer = SendKafkaUtils.getKafka(consumerGroup);
			consumer.subscribe(Arrays.asList(topic));//消费主题
			for(;;){
				ConsumerRecords<String, String> records = consumer.poll(1024);
				process(records);
			}
		} catch (Exception e) {
			logger.error(">>>DataSendDownConsumer::consumerProcess; kafkaCommon.getKafkaConsumer() exception!",e);
		}
	}
	//通过netty发送到plc设备
	public void process(ConsumerRecords<String, String> records){
		String value;
		DataSendDownHandler dataSendDownHandler = new DataSendDownHandler();
		for (ConsumerRecord<String, String> record : records) {
			//logger.info("====dataSendDownConsumer_request==clean data==>");
			//if(true)continue;
			value = record.value();
			if(ObjectUtil.isNotNull(value)) {
				if(dataSendDownHandler.executor.getQueue().size() > 200){
					logger.warn("====dataSendDownConsumer_request_==下发指令消费==  线程池> 200告警   ====");
					try {
						Thread.currentThread().sleep(100);//延迟 防止撑爆线程池
					} catch (InterruptedException e) {
						logger.error("DataSendDownHandler::process, Thread.currentThread exception!",e);
					}
				}
				dataSendDownHandler.process(value,redisService,applicationConfig);//异步处理
			}
		}
	}

	@Override
	public void run() {
		this.consumerProcess();
	}
}
