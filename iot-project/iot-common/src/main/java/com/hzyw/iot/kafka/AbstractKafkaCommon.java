package com.hzyw.iot.kafka;

import java.util.Properties;

public abstract class AbstractKafkaCommon {

	private Properties producerConfig = new Properties();
	
	private Properties consumerConfig = new Properties();

	public Properties getProducerConfig() {
		return producerConfig;
	}

	public void setProducerConfig(Properties producerConfig) {
		this.producerConfig = producerConfig;
	}

	public Properties getConsumerConfig() {
		return consumerConfig;
	}

	public void setConsumerConfig(Properties consumerConfig) {
		this.consumerConfig = consumerConfig;
	}

	 
}
