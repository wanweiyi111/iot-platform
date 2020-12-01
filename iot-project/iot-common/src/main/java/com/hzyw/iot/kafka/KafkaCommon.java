package com.hzyw.iot.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import com.hzyw.iot.config.KafkaConfig;
import com.hzyw.iot.vo.dataaccess.MessageVO;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisOperations;

//@Service  
public class KafkaCommon extends AbstractKafkaCommon {

	public KafkaConsumer<String, String> getKafkaConsumer(String groupId) throws Exception {
		Properties config = KafkaConfig.copeProperty(this.getConsumerConfig()); //this.getConfig()返回的对象是KafkaCommon单例下的一个属性，需要拷贝新的对象
		config.put(KafkaConfig.group_id, groupId);   //动态指定消费组
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
		return consumer;
	}

	/**
	 * 获取发送Producer
	 * --调用端必须手动执行producer.close()，对于批量发送的场景可以调用此方法连续发送消息后再手动关闭连接
	 * @return
	 * @throws Exception
	 */
	public Producer<String, String> getKafkaProducer() throws Exception {
		Properties config = KafkaConfig.copeProperty(this.getProducerConfig()); 
		Producer<String, String> producer = new KafkaProducer<>(config);
		return producer;
	}
	
	/**
	 * 发送消息
	 * --发送完毕，自动关闭连接
	 * @param topic
	 * @param messageVo
	 */
	public void send(String topic,String messageVo) {
		Producer<String, String> producer = null;
		try{
			producer = getKafkaProducer();
			producer.send(new ProducerRecord<>(topic, messageVo));
			producer.close();
		}catch(Exception e){
			if(producer != null){
				producer.close();
			}
		}
	}
	
 
}
