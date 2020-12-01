package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	
	//消费PLC指令
	@Value(value = "${iot.plc.adapter.topic.producer.plcOrder}")
	private String  plcOrder;
	
	@Value(value = "${iot.plc.adapter.topic.producer.plcOrder.consumergroup}")
	private  String  kafkaPlcConsumerGroup;
	
	@Value(value = "${iot.plc.adapter.request42.queuesync.thread.sleep}")
	private Integer request42SyncSleepTime;
	 
	public String getKafkaPlcConsumerGroup() {
		return kafkaPlcConsumerGroup;
	}

	public void setKafkaPlcConsumerGroup(String kafkaPlcConsumerGroup) {
		this.kafkaPlcConsumerGroup = kafkaPlcConsumerGroup;
	}

	public String plcOrder() {
		return plcOrder;
	}

	public void setDevInfoResponseTopic(String plcOrder) {
		this.plcOrder = plcOrder;
	}

	public Integer getRequest42SyncSleepTime() {
		return request42SyncSleepTime;
	}

	public void setRequest42SyncSleepTime(Integer request42SyncSleepTime) {
		this.request42SyncSleepTime = request42SyncSleepTime;
	}

}
