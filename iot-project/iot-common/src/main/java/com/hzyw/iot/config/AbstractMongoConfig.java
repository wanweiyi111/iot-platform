package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

public abstract class AbstractMongoConfig {
	//mongodb配置属性 
	@Value("${primary.mongodb.host}")
	private String host;
	@Value("${primary.mongodb.database}")
	private String database;
	@Value("${primary.mongodb.port}")
	private int port;
	 
	//Setter methods go here.. 
	/* 
	* 创建MongoDBFactory的方法
	* 两个MongoDB连接共用 
	*/
	public MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(host, port), database);
	}
	/* 
	* Factory method to create the MongoTemplate 
	*/
	abstract public MongoTemplate getMongoTemplate() throws Exception;
	}
