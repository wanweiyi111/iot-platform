package com.hzyw.iot.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hzyw.iot.config.MqttConnectionConfig;
import com.hzyw.iot.mqtt.callback.MqttCallbackImpl;
import com.hzyw.iot.mqtt.sub.GatewayMqttSub;
import com.hzyw.iot.service.GateWayService;

/**
 * 盒子网关服务
 *
 */
@Service
public class GateWayServiceImpl implements GateWayService {
	
	@Autowired
	private MqttConnectionConfig mqttConnectionConfig;//mqtt配置
	
	@Autowired
	private MqttCallbackImpl mqttCallbackImpl;
	
	/* 
	 * 数据上报
	 */
	@Override
	public void dataAccess() {
	//从MQTT服务获取上报的消息
	new Thread(new GatewayMqttSub(mqttCallbackImpl,mqttConnectionConfig),"数据上报::订阅MQTT").start();
		
	}

	
	

}
