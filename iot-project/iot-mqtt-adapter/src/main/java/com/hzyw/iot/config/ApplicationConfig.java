package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	//iot.mqtt.adapter.serviceid=service_00001
	/*@Value(value = "${iot.mqtt.adapter.kafkatopic}")
	private String  kafkaTopic;
	*/
	@Value(value = "${iot.mqtt.adapter.serviceid}")
	private String  serviceId;
	//下发请求上报(生产消息)
	@Value(value = "${iot.mqtt.adapter.topic.producer.dataAcess}")
	private String  dataAcessTopic;
	//设备属性上报(生产消息)
	@Value(value = "${iot.mqtt.adapter.topic.producer.devInfoResponse}")
	private String  devInfoResponseTopic;
	//下发(消费消息)
	@Value(value = "${iot.mqtt.adapter.topic.consumer.datasend}")
	private String  datasendTopic;
	//下发列队
	@Value(value = "${iot.mqtt.adapter.topic.consumer.datasend.groupid}")
	private String  datasendTopicGroup;
	
	//下发指令到PLC
	@Value(value = "${iot.plc.adapter.topic.producer.plcOrder}")
	private String  plcOrder;
	 
	public String getDataAcessTopic() {
		return dataAcessTopic;
	}

	public void setDataAcessTopic(String dataAcessTopic) {
		this.dataAcessTopic = dataAcessTopic;
	}

	public String getDevInfoResponseTopic() {
		return devInfoResponseTopic;
	}

	public void setDevInfoResponseTopic(String devInfoResponseTopic) {
		this.devInfoResponseTopic = devInfoResponseTopic;
	}

	public String getDatasendTopic() {
		return datasendTopic;
	}

	public void setDatasendTopic(String datasendTopic) {
		this.datasendTopic = datasendTopic;
	}

	public String getDatasendTopicGroup() {
		return datasendTopicGroup;
	}

	public void setDatasendTopicGroup(String datasendTopicGroup) {
		this.datasendTopicGroup = datasendTopicGroup;
	}

	/*public String getKafkaTopic() {
		return kafkaTopic;
	}

	public void setKafkaTopic(String kafkaTopic) {
		this.kafkaTopic = kafkaTopic;
	}

	*/
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlcOrder() {
		return plcOrder;
	}

	public void setPlcOrder(String plcOrder) {
		this.plcOrder = plcOrder;
	}
	 
	

}
