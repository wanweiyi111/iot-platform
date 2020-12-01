package com.hzyw.iot.mqtt;

import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hzyw.iot.config.MqttConnectionConfig;

/**
 * MQTT配置信息，初始化客户端句柄对象
 */
@SuppressWarnings("unused")
public class MqttHandler {
	//@Autowired
	private MqttConnectionConfig mqttConnConfig;

	private MqttClient mqttClient;// 创建客户端
	private MqttConnectOptions options;// 创建链接参数
	private String topic;      // 主题
	private int qos;           // 通道
	private String url;        // 地址
	private String userName;   // 用户名
	private String password;   // 密码
	private String clientId;   // 发布ID
	private String contents;   //发送内容

  
	public void setConfig(Map<String,String> config,MqttConnectionConfig mqttConnectionConfig) throws MqttException {
		//contents = content;
		topic = config.get("topic");
		qos = Integer.parseInt(config.get("qos")); 
		url = config.get("url"); 
		userName = config.get("userName"); 
		password = config.get("password"); 
		clientId = config.get("clientId");
		
		options = new MqttConnectOptions();
		// 在重新启动和重新连接时记住状态
		options.setCleanSession(false);
		// 设置连接的用户名
		options.setUserName(userName);
		options.setPassword(password.toCharArray());
		
		//mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
		this.mqttConnConfig = mqttConnectionConfig;
	}
	 
	public MqttClient getMqttClient() {
		return mqttClient;
	}


	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}
 
	public MqttConnectionConfig getMqttConnConfig() {
		return mqttConnConfig;
	}

	public void setMqttConnConfig(MqttConnectionConfig mqttConnConfig) {
		this.mqttConnConfig = mqttConnConfig;
	}

	public MqttConnectOptions getOptions() {
		return options;
	}


	public void setOptions(MqttConnectOptions options) {
		this.options = options;
	}


	public String getTopic() {
		return topic;
	}


	public void setTopic(String topic) {
		this.topic = topic;
	}


	public int getQos() {
		return qos;
	}


	public void setQos(int qos) {
		this.qos = qos;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getContents() {
		return contents;
	}


	public void setContents(String contents) {
		this.contents = contents;
	}
	
	

}
