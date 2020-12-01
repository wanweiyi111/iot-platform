package com.hzyw.iot.utils;


public class TestMongoDb {
	/*public static void main(String[] args) {
		 //获取数据库连接对象
	    MongoDatabase mongoDatabase = getConnect();
	    //获取集合
	    MongoCollection<Document> collection = mongoDatabase.getCollection("user");
	    //要插入的数据
	    Document document = new Document("name","万万")
	                            .append("sex", "男")
	                            .append("age", 21);
	    //插入一个文档
	    collection.insertOne(document);
		
	}
	
		//mongodb 连接数据库工具类
			    //不通过认证获取连接数据库对象
			    public static MongoDatabase getConnect(){
			        //连接到 mongodb 服务
			        MongoClient mongoClient = new MongoClient("192.168.3.189", 27017);
			 
			        //连接到数据库
			        MongoDatabase mongoDatabase = mongoClient.getDatabase("hello");
			 
			        //返回连接数据库对象
			        return mongoDatabase;
			    }*/
			 
			    /*//需要密码认证方式连接
			    public static MongoDatabase getConnect2(){
			        List<ServerAddress> adds = new ArrayList<>();
			        //ServerAddress()两个参数分别为 服务器地址 和 端口
			        ServerAddress serverAddress = new ServerAddress("192.168.3.189", 27017);
			        adds.add(serverAddress);
			        
			        List<MongoCredential> credentials = new ArrayList<>();
			        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
			        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());
			        credentials.add(mongoCredential);
			        
			        //通过连接认证获取MongoDB连接
			        MongoClient mongoClient = new MongoClient(adds, credentials);
			 
			        //连接到数据库
			        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
			 
			        //返回连接数据库对象
			        return mongoDatabase;
			    }*/

}
