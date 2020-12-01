package com.hzyw.iot.mqtt.callback;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 实现回调函数
 *
 *
 * //订阅包括以下消息类型 devOnline DEV在线 devOffline DEV离线 response 上报下发请求结果
 * devInfoResponse 属性上报 metricInfoResponse设备状态数据上报 devSignlResponse 设备信号上报
 *
 * 
 */
@Component
public class MqttCallbackImpl implements MqttCallback {
	private static Logger logger = LoggerFactory.getLogger(MqttCallbackImpl.class);
	
	@Autowired
	private Handler handler;

	public void connectionLost(Throwable cause) {
		// 连接正常断开？
		System.out.println("connectionLost");
	}

	public void messageArrived(String topic, MqttMessage message) {
		try {
			logger.info(">>>GatewayMqttSub订阅,接收，message content :"  + new String(message.getPayload()));
			if(message !=null && !StringUtils.isEmpty(message.toString())){
				handler.handlerMessages(topic, message.toString());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(">>>MqttCallbackImpl::messageArrived exception !", e);
		}
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("deliveryComplete---------" + token.isComplete());
	}

}
