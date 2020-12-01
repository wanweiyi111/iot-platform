package com.hzyw.iot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;

import com.hzyw.iot.util.JedisPoolUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDbConfig {
	static Properties props = new Properties();
	 static {
			// 手动加载加载配置文件
			InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("mongoDb.properties");
			Properties pro = new Properties();
			try {
				pro.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			props.put("mongoIp", pro.get("spring.mongodb.servers.ip"));  
			props.put("mongoport", pro.get("spring.mongodb.servers.port"));  
			
		}
	 @Bean  
	 public Properties getConsumerProperties() {   
	        return null;  //扩展并手动从指定文件加载
	    } 
	//不通过认证获取连接数据库对象
    public static MongoDatabase getConnect(){
        //连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient(props.getProperty("mongoIp").toString(), Integer.parseInt(props.getProperty("mongoport")));
        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        //返回连接数据库对象
        return mongoDatabase;
    }
}
