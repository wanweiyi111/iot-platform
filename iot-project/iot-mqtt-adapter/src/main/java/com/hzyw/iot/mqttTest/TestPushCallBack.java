package com.hzyw.iot.mqttTest;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class TestPushCallBack {
	private static final Logger LOG = LogManager.getLogger(TestPushCallBack.class);
	/*
	//生成配置对象，用户名，密码等
	public MqttConnectOptions getOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setUserName("wan");
		options.setPassword("123".toCharArray());
		options.setConnectionTimeout(10);
		options.setKeepAliveInterval(20);
		return options;
	}
	public void connect() throws MqttException {
		 String HOST =  "tcp://47.106.189.255:1883";
		 String clientid = "subClient";
		 MqttClient client = new MqttClient(HOST, clientid, new MemoryPersistence());
		//防止重复创建MQTTClient实例
		if (client==null) {
			client = new MqttClient(host + ":" + port, clientId, new MemoryPersistence());
			client.setCallback(new PushCallback(MiddlewareMqttClient.this));
		}
		MqttConnectOptions options = getOptions();
		//判断拦截状态，这里注意一下，如果没有这个判断，是非常坑的
		if (!client.isConnected()) {
			client.connect(options);
			System.out.println("连接成功");
		}else {//这里的逻辑是如果连接成功就重新连接
			client.disconnect();
			client.connect(getOptions(options));
			System.out.println("连接成功");
		}
	}
	//监听设备发来的消息
	//PostConstruct确保该函数在类被初始化时调用
	@PostConstruct
	public void init() {
		connect();
		//getMessage是我自己封装的一个订阅主题的函数，对于聪明的你们，应该很简单吧
		//getMessage(topic, 2);
		
	}*/

}
