package com.hzyw.iot.test;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;

public class KafkaConsumerExample {
	public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.3.183:9901,192.168.3.183:9902,192.168.3.183:9903");
        props.put("group.id", "iot_topic_dataAcess_request_group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("auto.offset.reset", "earliest");
        props.put("auto.offset.reset", "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("mytest"));
       
        //for(int i = 0; i < 10; i++) {
        for( ;  ;  ) {
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records){
            	 System.out.println("      处理--------------+++++++++---------"+record.value());
            	/*try {
	               	 System.out.println("------消费，并休眠3秒---------");
	   				Thread.currentThread().sleep(1000*3);
	   				System.out.println("      休眠完毕 ，处理---------");
	   			} catch (InterruptedException e1) {
	   				// TODO Auto-generated catch block
	   				e1.printStackTrace();
	   			}
	               // System.out.printf("      offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
	               System.out.println("      处理--------------+++++++++---------"+record.value());*/
            }
        }
    }
}
