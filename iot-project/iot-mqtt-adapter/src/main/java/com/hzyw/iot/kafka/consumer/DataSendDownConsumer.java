package com.hzyw.iot.kafka.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.mqtt.pub.CommPubHandler;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.util.GatewayMqttUtil;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 1，从KAFKA获取下发数据（实时获取） 2, 判断服务是否在线 3，判断设备是否已上线 4，发送到MQTT服务器，关闭mqtt连接
 * 5，如果存在异常，检查看KAFKA是否已经提交offset
 */
public class DataSendDownConsumer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(DataSendDownConsumer.class);
	
	private KafkaCommon kafkaCommon;
	
	private ApplicationConfig applicationConfig;
	
	private CommPubHandler commPubHandler;
	
	private GateWayService gateWayService;

	public DataSendDownConsumer() {
	}

	public DataSendDownConsumer(KafkaCommon kafkaCommon, ApplicationConfig applicationConfig,
			CommPubHandler commPubHandler, GateWayService gateWayService) {
		this.kafkaCommon = kafkaCommon;
		this.applicationConfig = applicationConfig;
		this.commPubHandler = commPubHandler;
		this.gateWayService = gateWayService;
	}

	/**
	 * 获取kafka的数据，并处理下发Mqtt(抽到外层的service业务方法里去)
	 */
	@SuppressWarnings("static-access")
	public void consumerProcess() {
		String topic = applicationConfig.getDatasendTopic(); //"iot_topic_dataAcess_request"
		String groupId = applicationConfig.getDatasendTopicGroup(); //"iot_topic_dataAcess_request_group"
		try {
			KafkaConsumer<String, String> consumer = kafkaCommon.getKafkaConsumer(groupId);//指定消费组
			consumer.subscribe(Arrays.asList(topic));//订阅主题
			
			for(;;){
				//logger.info(">>>DataSendDownConsumer::consumerProcess waiting... " );
				ConsumerRecords<String, String> records = consumer.poll(100);
				process(records);
				/*try {
					Thread.currentThread().sleep(1000 * 5);
				} catch (InterruptedException e) {
					logger.error(">>>DataSendDownConsumer::consumerProcess::process; currentThread().sleep exception!",e);
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(">>>DataSendDownConsumer::consumerProcess; kafkaCommon.getKafkaConsumer() exception!",e);
		}
	}
	
	private static int corePoolSize = 10;//Runtime.getRuntime().availableProcessors();
	private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, 50, 30*1000, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000));
	
	@SuppressWarnings("static-access")
	private void  processHandle(String value){
		if(executor.getQueue().size() > 800){
			logger.warn(">>>DataSendDownConsumer::consumerProcess ==  线程池> 800告警   ====executor.Queue="+executor.getQueue().size()+" /value="+value);
			try {
				Thread.currentThread().sleep(50);//延迟 防止撑爆线程池
			} catch (InterruptedException e) {
				logger.error("DataSendDownConsumer::processHandle, Thread.currentThread exception!",e);
			}
		}
		
		Runnable requestTask = new Runnable() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				String type,deviceId,gatewayId;
				boolean isOnline;
				JSONObject jsonObject = new JSONObject();
				try{ 
					//value = record.value();
					logger.info(">>>DataSendDownConsumer::consumerProcess ;value="+value);
					if(ObjectUtil.isNotNull(value)) {
						jsonObject = JSONUtil.parseObj(value);//格式=messageVO
						JSONObject data = JSONUtil.parseObj(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data));
						
						type = jsonObject.get(GatewayMqttUtil.dataModel_messageVO_type).toString();
						deviceId = data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString();//设备ID
						gatewayId = jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString();//网关ID
						Map tags = (Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags);//tags
						
						//data数据
						RequestDataVO requestDataVO = new RequestDataVO();
						requestDataVO.setMethods((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_methods));
						requestDataVO.setTags(tags);
						requestDataVO.setId(deviceId);
						logger.info(">>>DataSendDownConsumer::consumerProcess::--0001-;value="+value);
						//消息结构
						MessageVO messageVo = new MessageVO();
						//消息结构
						messageVo.setType(type);
						messageVo.setTimestamp((Object)jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp));//消息上报时间
						messageVo.setMsgId(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString());
						messageVo.setData(requestDataVO);
						messageVo.setGwId(gatewayId);
						logger.info(">>>DataSendDownConsumer::consumerProcess;---1="+ (tags!=null) +" /"+value);
						logger.info(">>>DataSendDownConsumer::consumerProcess;----2="+ (tags!=null && tags.get("agreement")!=null) +" /"+value);
						logger.info(">>>DataSendDownConsumer::consumerProcess;----3="+ (tags!=null && tags.get("agreement")!=null && tags.get("agreement").equals("plc")) +" /"+value);
						if(tags!=null){
							logger.info(">>>DataSendDownConsumer::consumerProcess;---4="+ tags.get("agreement") +"/"+value);
						}

						//判断是否PLC指令
						if(tags!=null&& tags.get("agreement")!=null&&tags.get("agreement").equals("plc")) {
							//下发到PLC
							logger.info(">>>DataSendDownConsumer::consumerProcess::分发到plc;msgId="+messageVo.getMsgId());
							Producer<String, String> producer = kafkaCommon.getKafkaProducer();
							producer.send(new ProducerRecord<>(applicationConfig.getPlcOrder(), JSON.toJSONString(messageVo)));
							producer.close();
							logger.info(">>>DataSendDownConsumer::consumerProcess::分发到plc,seccess!!!;msgId="+messageVo.getMsgId());
						}
						else if(tags!=null&& tags.get("agreement")!=null&&tags.get("agreement").equals("audio")) {
							Producer<String, String> producer = kafkaCommon.getKafkaProducer();
							producer.send(new ProducerRecord<>("iot_topic_dataAcess_audio", JSON.toJSONString(messageVo)));
							producer.close();
						}
						else {
							logger.info(">>>DataSendDownConsumer::consumerProcess::盒子指令;---5=" );
							isOnline = gateWayService.deviceOnLine(deviceId);//设备是否在线？
							logger.info(">>>DataSendDownConsumer::consumerProcess; type/gatewayId/deviceId/isOnline=" + type +"/"+ gatewayId +"/"+ deviceId +"/"+ isOnline);
							//推送到MQTT
							if(isOnline){
								logger.info(">>>DataSendDownConsumer::consumerProcess::Publish(下发);.... /topic/value =" + commPubHandler.getTopic()+ "/"+value );
								commPubHandler.Publish(JSONUtil.parseObj(messageVo).toString(),gatewayId); 
							}else{
								//反馈失败信息
								jsonObject.put(GatewayMqttUtil.dataModel_messageVO_messageCode, GatewayMqttUtil.return_devoffline_code);
								jsonObject.put(GatewayMqttUtil.dataModel_messageVO_message, GatewayMqttUtil.return_devoffline_message);//反馈不在线
								sendKafka(JSON.toJSONString(jsonObject),applicationConfig.getDataAcessTopic());
							}
						}
					}else{
						logger.info(">>>DataSendDownConsumer::consumerProcess;ObjectUtil.isNotNull(value)=false ;value="+value);
					}
				}catch(Exception e){
					//反馈异常失败信息
					logger.error(">>>DataSendDownConsumer::consumerProcess::process; 处理ConsumerRecord  exception! value="+value,e);
					jsonObject.put(GatewayMqttUtil.dataModel_messageVO_messageCode, GatewayMqttUtil.return_fail_code);
					jsonObject.put(GatewayMqttUtil.dataModel_messageVO_message, GatewayMqttUtil.return_fail_message+e.getMessage());//反馈不在线
					sendKafka(JSON.toJSONString(jsonObject),applicationConfig.getDataAcessTopic());
				}
			}
		};
		if(executor.getActiveCount() < 50 && executor.getQueue().size() < 1000){
			executor.submit(requestTask);
		}else{
			//理论上说，如果上面的休眠时间合理的话，是不会进入丢弃的，如果有丢弃，可以把上面的休眠时间加大，直到压测能稳定的消费且不丢弃数据才算OK
			logger.warn(">>>DataSendDownConsumer::consumerProcess ==  线程池已爆满,丢弃...  ActiveCount="+executor.getActiveCount()
			+"，queue="+executor.getQueue().size()+" /value="+value);
		}
	}
	public void process(ConsumerRecords<String, String> records){
			for (ConsumerRecord<String, String> record : records) {
				processHandle(record.value());
			}
	}

	@Override
	public void run() {
		this.consumerProcess();
	}
	
	public void sendKafka(String messageVo, String topic) {
		try {
			Producer<String, String> producer = kafkaCommon.getKafkaProducer();
			producer.send(new ProducerRecord<>(topic, messageVo.toString()));
			producer.close();
		} catch (Exception e) {
			logger.error(">>>DataSendDownConsumer::process::sendKafka ; producer.send异常 !",e);
		}
	}

}
