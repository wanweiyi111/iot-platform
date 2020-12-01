package com.hzyw.iot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.hzyw.iot.util.JedisPoolUtils;

public class SendKafkaUtils {
	
	static Properties props = new Properties();
	static String servers = null;
    static {
		// 手动加载加载配置文件
		InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("kafka.consumer_plc.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		servers = (String)pro.get("spring.kafka.bootstrap.servers");
		props.put("bootstrap.servers", pro.get("spring.kafka.bootstrap.servers"));
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

	}
	
	/**
	 * setKafka发送
	 */
	public static void sendKafka(String topic,String messageVo) {
		Producer<String, String> producer = new KafkaProducer<>(props);
		try {
			// Producer<String, String> producer = kafkaCommon.getKafkaProducer();
			producer.send(new ProducerRecord<>(topic, messageVo));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			producer.close();
		}
	}
	
	public static KafkaConsumer<String, String> getKafka(String consumerGroup) throws Exception {
		Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("group.id", consumerGroup); //group123 consumerGroup
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("auto.offset.reset", "earliest");
        props.put("auto.offset.reset", "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
	}
}
