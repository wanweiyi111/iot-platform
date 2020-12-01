package com.hzyw.iot.service.Impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.config.MqttConnectionConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.kafka.consumer.DataSendDownConsumer;
import com.hzyw.iot.mqtt.callback.MqttCallbackImpl;
import com.hzyw.iot.mqtt.pub.CommPubHandler;
import com.hzyw.iot.mqtt.pub.ServicePubHandler;
import com.hzyw.iot.mqtt.sub.GatewayMqttSub;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.GatewayMqttUtil;
import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.ServiceDataVO;

/**
 * 盒子网关服务
 *
 */
@Service
public class GateWayServiceImpl implements GateWayService {
	@Autowired
	private RedisService redisService; //redis工具类
	
	@Autowired
	private KafkaCommon kafkaCommon; //KAFKA工具类
	
	@Autowired
	private ApplicationConfig applicationConfig;//全局配置
	
	@Autowired
	private MqttConnectionConfig mqttConnectionConfig;//mqtt配置
	
	@Autowired
	private ServicePubHandler servicePubHandler;
	
	@Autowired
	private CommPubHandler commPubHandler;
	
	@Autowired
	private MqttCallbackImpl mqttCallbackImpl;
	
	/* 
	 * 平台上线
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.GateWayService#serviceOnline()
	 */
	@Override
	public void serviceOnline() {
		Thread online = new Thread(new Runnable() {
			public void run() {
				Map<String, String> map = new HashMap<String,String>();
				map.put("key", "value");
				//下线消息
				String msgId = GatewayMqttUtil.getUUID();
				ServiceDataVO serviceOfflineVO = new ServiceDataVO();
				serviceOfflineVO.setId("1000-f82d132f9bb018ca-2001-ffff-d28a");
				serviceOfflineVO.setStatus("offline");
				serviceOfflineVO.setTags(map);
				MessageVO offlineMessageVO = new MessageVO(serviceOfflineVO);
				offlineMessageVO.setType(DataType.ServiceOffline.getMessageType());
				offlineMessageVO.setMsgId(msgId);
				offlineMessageVO.setTimestamp(System.currentTimeMillis());
				offlineMessageVO.setGwId("0001-f82d132f9bb018ca-2001-ffff-acbc");
				//上线消息
				ServiceDataVO serviceOnlineVO = new ServiceDataVO();
				serviceOnlineVO.setId("1000-f82d132f9bb018ca-2001-ffff-d28a");//1000-f82d132f9bb018ca-2001-ffff-d28a
				serviceOnlineVO.setStatus("online");
				serviceOnlineVO.setTags(map);
				MessageVO onlineMessageVO = new MessageVO(serviceOnlineVO);
				onlineMessageVO.setType(DataType.ServiceOnline.getMessageType());
				onlineMessageVO.setMsgId(msgId);
				onlineMessageVO.setTimestamp(System.currentTimeMillis());
				onlineMessageVO.setGwId("0001-f82d132f9bb018ca-2001-ffff-acbc");//0001-f82d132f9bb018ca-2001-ffff-acbc
				//服务上线完毕后才能做下发和上报之类的交互，所以这里不用起新的线程处理
				servicePubHandler.Publish(JSON.toJSONString(onlineMessageVO),JSON.toJSONString(offlineMessageVO));
			}
		});
		online.start();
	}

	/* 
	 * 数据上报
	 */
	@Override
	public void dataAccess(GateWayService gateWayService) {
		//判断服务是否在线
		//if(serviceOnLine(applicationConfig.getServiceId())){ 
			//从MQTT服务获取上报的消息
			new Thread(new GatewayMqttSub(mqttCallbackImpl,mqttConnectionConfig,gateWayService),"数据上报::订阅MQTT").start();
		//}
		
	}
	
	

	/* 
	 * 数据下发-消费KAFKA获取下发数据
	 */
	@Override
	public void dataSendDown() {
		//判断服务是否在线,从KAFKA获取下发消息 DataSendDownConsumer
		if(serviceOnLine(applicationConfig.getServiceId())){  
			new Thread(new DataSendDownConsumer(kafkaCommon,applicationConfig,commPubHandler,this),"数据下发::消费KAFKA").start();
		}
	}
	
	/**
	 * 数据下发
	 * @param messageVO
	 */
	@Override
	public <T> void dataSendDown(MessageVO<T> messageVO) {
		//如果不通过KAFKA,dataSendDown()方法的实现可以在这里重构，直接提供接口API给下发调用
	}

	/* 
	 * 服务是否在线
	 */
	@Override
	public boolean serviceOnLine(String serviceId) {
		return redisService.hasHmkey(GatewayMqttUtil.rediskey_iot_cache_dataAccess, serviceId);
	}
	
	/*  
	 * 设备是否在线
	 */
	@Override
	public boolean deviceOnLine(String deviceId) {
		return redisService.hasHmkey(GatewayMqttUtil.rediskey_iot_cache_dataAccess, deviceId);
	}
	

}
