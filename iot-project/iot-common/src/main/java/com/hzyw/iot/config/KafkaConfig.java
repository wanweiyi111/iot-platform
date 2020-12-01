package com.hzyw.iot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.util.JedisPoolUtils;

@Configuration   
public class KafkaConfig {
	private static Logger logger = Logger.getLogger(KafkaConfig.class);  
	   
    //@Value("${spring.redis.port}")  
    static Integer port;
    static String host;
    static String password;
     
    public static String group_id = "group.id";
    
    static Properties props = new Properties();
    static {
		// 手动加载加载配置文件
		InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("kafka.consumer.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		props.put("bootstrap.servers", pro.get("spring.kafka.bootstrap.servers"));    
        ///props.put("group.id", "group_zhu");   
        props.put("enable.auto.commit", "true"); 
        props.put("auto.commit.interval.ms", "1000"); 
        props.put("session.timeout.ms", "30000");  
        props.put("auto.offset.reset", "earliest");  
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
	}
    

    static Properties props_producer = new Properties();;
    static {
		// 手动加载加载配置文件
		InputStream _in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("kafka.producer.properties");
		Properties _pro = new Properties();
		try {
			_pro.load(_in);
		} catch (IOException e) {
			e.printStackTrace();
		}
  
		props_producer.put("bootstrap.servers", _pro.get("spring.kafka.bootstrap.servers"));    
		props_producer.put("acks", "all"); 
		props_producer.put("retries", 0); 
		props_producer.put("batch.size", 16384);  
		props_producer.put("linger.ms", 1);  
		props_producer.put("buffer.memory", 33554432);
		props_producer.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props_producer.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	}
    

    @Bean  
    public Properties getConsumerProperties() {   
        return props;  //扩展并手动从指定文件加载
    } 
    
    @Bean  
    public Properties getProducerProperties() {   
        return props_producer;  //扩展并手动从指定文件加载
    } 
      
    //@ConfigurationProperties(prefix = "spring.kafka")  未來可以考慮直接走springboot默認配置
    @Bean
    public KafkaCommon getKafkaCommon() {  
    	KafkaCommon factory = new KafkaCommon();  
        factory.setProducerConfig(getProducerProperties());  
        factory.setConsumerConfig(getConsumerProperties());
        logger.info("KafkaCommon bean init success.");    
        return factory;
    }  
    
    
    public static Properties copeProperty(Properties config){
		Properties props = new Properties();
        Set<Object> keys = config.keySet();//返回属性key的集合
        for (Object key : keys) {
            props.put(key, config.get(key));   
        }
        return props;
	}
}