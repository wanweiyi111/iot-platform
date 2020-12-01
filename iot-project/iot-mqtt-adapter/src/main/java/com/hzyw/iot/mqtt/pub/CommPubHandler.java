package com.hzyw.iot.mqtt.pub;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hzyw.iot.config.MqttConnectionConfig;

/**
 * 请求下发，发布完毕则关闭连接
 */
@Service
public class CommPubHandler extends AbstractPubHandler {
	
	private static Logger logger = LoggerFactory.getLogger(CommPubHandler.class);
	
	@Autowired
	private MqttConnectionConfig mqttConnectionConfig;
	
	public CommPubHandler() {
		//super.setConfig(getConfig());
	}
	
	@PostConstruct
    public void init() {
		try {
			setConfig(getConfig(),mqttConnectionConfig);
		} catch (MqttException e) {
			logger.error(">>>CommPubHandler::init ",e);
		}
    }

	@Override
	public void Publish(String ... para) {
		try {
			//NEW一个客户端连接对象
			this.setMqttClient(new MqttClient(this.getUrl(), this.getClientId(), new MemoryPersistence()));
			// 建立连接
			this.getMqttClient().connect(this.getOptions());
			// 创建消息
			MqttMessage message = new MqttMessage(para[0].getBytes());
			// 设置消息的服务质量
			message.setQos(this.getQos());
			// 发布消息
			this.getMqttClient().publish(this.getTopic(), message);
			// 断开连接
			this.getMqttClient().disconnect();
			// 关闭客户端
			this.getMqttClient().close();
		} catch (MqttException me) {
			logger.error(">>>CommPubHandler::Publish ",me);
			try {
				this.getMqttClient().disconnect();
			} catch (MqttException e) {
				logger.error(">>>CommPubHandler::Publish ;getMqttClient().disconnect() exception! ",e);
			}
			// 关闭客户端
			try {
				this.getMqttClient().close();
			} catch (MqttException e) {
				logger.error(">>>CommPubHandler::Publish ;getMqttClient().close() exception! ",e);
			}
		}

	}

	@Override
	public Map<String, String> getConfig() {
		Map<String,String> config = new HashMap<String,String>();
		config.put("topic", mqttConnectionConfig.getRequest().get("topic"));
		config.put("qos", mqttConnectionConfig.getRequest().get("qos"));
		config.put("url", mqttConnectionConfig.getComm().get("url"));
		config.put("userName", mqttConnectionConfig.getComm().get("userName"));
		config.put("password", mqttConnectionConfig.getComm().get("password"));
		config.put("clientId", mqttConnectionConfig.getRequest().get("clientId"));
		return config;
	}
 
}
