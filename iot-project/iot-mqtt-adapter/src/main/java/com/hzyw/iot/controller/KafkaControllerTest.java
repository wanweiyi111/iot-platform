package com.hzyw.iot.controller;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.service.RedisService;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
public class KafkaControllerTest {

	@Autowired
	private KafkaCommon kafkaCommon;
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private ApplicationConfig applicationConfig;//全局配置
	
	//模拟kafka生产数据，下发到mqtt，MQTT订阅
	@RequestMapping(value = "/testproducer", method = RequestMethod.POST)
	public void testConsumer1(JSONObject jsonobjct) {
         try{
        	 //test 
        	 String topic = applicationConfig.getDatasendTopic();//下发数据队列   ,只有一个
             Producer<String, String> producer = kafkaCommon.getKafkaProducer();
             producer.send(new ProducerRecord<>(topic, JSON.toJSONString(jsonobjct)));
             producer.close();
         }catch(Exception e){
        	 e.printStackTrace();
         }
    }
	
	//模拟消费KAFKA数据，从MQTT发布上报数据
	@RequestMapping(value = "/testconsumer", method = RequestMethod.GET)
	public void testConsumer1(String[] args) {
        try{
       	 //制定消費隊列和消費組
       	 String topic = "iot_topic_dataAcess";
            String groupId = "iot_topic_dataAcess_group";
            //System.out.println("----kafkaCommon-----" +kafkaCommon);
            KafkaConsumer<String, String> consumer = kafkaCommon.getKafkaConsumer( groupId);
            consumer.subscribe(Arrays.asList("topic_wan"));
            
            //redisService.set("status", "online");
            int p = 0;
            for(;;) {
            	 System.out.println("===========into==========");
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records){
                	p++;
             		System.out.printf("==========="+p+"=============>offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value()); 
                }
                try {
    				Thread.currentThread().sleep(1000*5);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
        }catch(Exception e){
       	 e.printStackTrace();
        }
      
   }
}