package com.hzyw.iot.mqtt.sub;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hzyw.iot.config.MqttConnectionConfig;
import com.hzyw.iot.mqtt.callback.MqttCallbackImpl;
import com.hzyw.iot.mqtt.pub.ServicePubHandler;
import com.hzyw.iot.service.GateWayService;


/**
 * 统一订阅盒子设备消息
 * 1，获取   /公司编号/版本号/namespace1/devicepub 设备端发布的消息
 * 2,把消息字符串转化为MessageVO；
 * 3，根据type类型 转化为特定类型的VO ，如DevInfoDataVO
 * 4，把MessageVO 再此转化为JSON串 发送到KAFKA ，根据消息类型来发送到不同的KAFKA队列
 * 
 * kafka队列如下：
 *  iot_topic_dataAcess_request
	iot_topic_dataAcess_response
	
	iot_topic_dataAcess_devInfoResponse
	iot_topic_dataAcess_metricInfoResponse
	iot_topic_dataAcess_devSignlResponse
 */

/**
 * 订阅端
 */
public class GatewayMqttSub extends AbstractSubHandler  {
	private static Logger logger = LoggerFactory.getLogger(GatewayMqttSub.class);
	  
	private MqttCallbackImpl callbackImpl;
	
	private MqttConnectionConfig connectionConfig;
	
	private GateWayService gateWayService;
	
	public GatewayMqttSub(){}
	
	public GatewayMqttSub(MqttCallbackImpl mqttCallbackImpl,MqttConnectionConfig mqttConnectionConfig,GateWayService gateWayService){
		this.callbackImpl = mqttCallbackImpl;
		this.connectionConfig = mqttConnectionConfig;
		this.gateWayService = gateWayService;
		try {
			setConfig(getConfig(),connectionConfig);
		} catch (MqttException e) {
			logger.error(">>>GatewayMqttSub::init fail!",e);
		}
	}
	 
	@Override
	public void subscribe() {
		try {
			logger.info("==============GatewayMqttSub订阅,连接中==============================");
			//NEW一个客户端连接对象
			this.setMqttClient(new MqttClient(this.getUrl(), this.getClientId(), new MemoryPersistence())); 
			
			// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
			this.getOptions().setCleanSession(true);
			// 设置超时时间 单位为秒
			this.getOptions().setConnectionTimeout(10);
			// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
			this.getOptions().setKeepAliveInterval(20);
			// 遗愿消息
			//this.getOptions().setWill(this.getMqttConnConfig().getResponseFailover().get("topic"), "公共订阅线程掉线!".getBytes(), this.getQos(), true);
			// 设置断开后重新连接
			this.getOptions().setAutomaticReconnect(true);
			// 设置回调函数
			//this.getMqttClient().setCallback(new MqttCallbackImpl());
			this.callbackImpl.setT(this);
			this.getMqttClient().setCallback(this.callbackImpl);
			// 连接
			this.getMqttClient().connect(this.getOptions());
			// 订阅消息
			this.getMqttClient().subscribe(this.getTopic(), this.getQos());
			logger.info("==============GatewayMqttSub订阅,待接收消息==============================");
			//订阅通道准备完毕后通知设备服务上线
			gateWayService.serviceOnline();
			//return client;
		} catch (Exception e) {
			//设备接入-公共订阅主题-连接失败
			logger.error(">>>GatewayMqttSub::subscribe exception!",e);
			try {
				this.getMqttClient().disconnect();
			} catch (MqttException ex) {
				logger.error(">>>GatewayMqttSub::subscribe::MqttException ; getMqttClient().disconnect()  ",ex);
			}
			// 关闭客户端
			try {
				this.getMqttClient().close();
			} catch (MqttException ex) {
				logger.error(">>>GatewayMqttSub::subscribe::MqttException ; getMqttClient().close()  ",ex);
			}
		}
		
	}

	@Override
	public Map<String, String> getConfig() {
		//获取需要上报主题信息
		Map<String,String> config = new HashMap<String,String>();
		config.put("topic", connectionConfig.getResponse().get("topic"));
		config.put("qos", connectionConfig.getResponse().get("qos"));
		config.put("url", connectionConfig.getComm().get("url"));
		config.put("userName", connectionConfig.getComm().get("userName"));
		config.put("password", connectionConfig.getComm().get("password"));
		config.put("clientId", connectionConfig.getResponse().get("clientId"));
		return config;
		 
	}

	public void reConnection(){
		while (true) {
			try {
				if (!this.getMqttClient().isConnected()) {
					logger.info("--GatewayMqttSub---断线重连。。");
					this.getMqttClient().connect(this.getOptions());
					this.getMqttClient().subscribe(this.getTopic(), this.getQos());
					logger.info("--GatewayMqttSub---重新订阅成功!-------");
					break;
				} else {
					this.getMqttClient().disconnect(); // 反复尝试去断开现有的连接
					logger.info("--GatewayMqttSub---断线重连-先断开连接 ---------");
				}
			} catch (Exception e) {
				continue;
			}
		}
		logger.info("--GatewayMqttSub---重新订阅---------");
		 
	}
	
	

}
