package com.hzyw.iot.test;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.hzyw.iot.config.MongoDbConfig;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.Impl.ObjectServiceImpl;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDbTest {
		public static void main(String[] args) {
			/*MongoDbConfig config = new MongoDbConfig();
			//获取数据库连接对象
		    MongoDatabase mongoDatabase = config.getConnect();
		    //获取集合
		    MongoCollection<Document> collection = mongoDatabase.getCollection("user");
		    Map<String, Object> map = new HashMap<String, Object>();
		    map.put("name", "wanweiyi");
		    map.put("age", 27);
		    map.put("sex", "男");
		    //要插入的数据
		    Document document = new Document("plcJson",map)
		                            .append("plcNodeJson", "");
		    //插入一个文档
		    collection.insertOne(document);*/
			ObjectService objTest = new ObjectServiceImpl();
			//String a = objTest.saveObj("helloWorld");
			//System.out.println(a);
		}
		


}
