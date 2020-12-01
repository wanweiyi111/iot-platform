package com.hzyw.iot.test.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import cn.hutool.json.JSONObject;

public class ProducerDemo {

    public static void main(String[] args) throws Exception {
    	String json = "{\"data\":{\"methods\":[{\"method\": \"set_onoff\",\"in\": [{\"ab\": \"03\",\"code\": \"03\",\"level\": 0,\"onoff\": 0}]}],\"id\": \"1010-341bfdc842f3af1f-2011-ffff-5bb0\",\"tags\": {\"agreement\": \"plc\"}},\"msgId\": \"1db179ce-c81e-4499-bff2-29e8a954af97\",     \"gwId\": \"2000-d8b38d288d431464-2011-ffff-08ed\",\"type\": \"request\",\"timestamp\": 1566205651 }";
    	JSONObject jsonObj=new JSONObject(json);
    	sendKafka("iot_topic_plcOrder",jsonObj.toString());
         }
    
    
    /**
	 * setKafka发送
	 */
	public static void sendKafka(String topic,String messageVo) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "172.18.222.89:9901,172.18.222.89:9902,172.18.222.89:9903");//192.168.3.183:9901
        props.put("acks", "all");
        props.put("retries", 0); 
        props.put("batch.size", 16384);  
        props.put("linger.ms", 1);  
        props.put("buffer.memory", 33554432);
         //设置key和value序列化方式
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
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
}
