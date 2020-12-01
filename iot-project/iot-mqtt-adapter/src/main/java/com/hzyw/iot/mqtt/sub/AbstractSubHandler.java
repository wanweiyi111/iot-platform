package com.hzyw.iot.mqtt.sub;

import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hzyw.iot.config.MqttConnectionConfig;
import com.hzyw.iot.mqtt.MqttHandler;

/**
 * 抽象实现,抽象出发布的动作并作为一个fork的新线程
 */
@SuppressWarnings("unused")
public abstract class AbstractSubHandler extends MqttHandler implements Runnable {
	 
	/**
	 * 发布消息的逻辑可以重写
	 * @param content
	 */
	public abstract void subscribe();
	 
	/**
	 * 连接主题及相关配置,不同连接是不一样的
	 * @return
	 */
	public abstract Map<String,String> getConfig();

	@Override
	public void run() {
		this.subscribe();
	}
	 
}
