package com.hzyw.iot.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils {
	private static JedisPool pool = null;

	static {
		// 加载配置文件
		InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("redis.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 获得池子对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(pro.get("redis.maxIdle").toString()));// 最大闲置个数
		poolConfig.setMinIdle(Integer.parseInt(pro.get("redis.minIdle").toString()));// 最小闲置个数
		poolConfig.setMaxTotal(Integer.parseInt(pro.get("redis.maxTotal").toString()));// 最大连接数
		poolConfig.setTestOnBorrow(true);// 在borrow一个jedis实例的时候，是否需要验正作，如果是赋值true
		poolConfig.setTestOnReturn(true);// 在return。。。。
		poolConfig.setBlockWhenExhausted(true);// 连接耗尽时是否阻塞
		pool = new JedisPool(poolConfig, pro.getProperty("redis.url"),
				Integer.parseInt(pro.get("redis.port").toString()), 1000 * 2, pro.getProperty("redis.passWord"));
	}

	// 获得jedis资源的方法
	public static Jedis getJedis() {
		return pool.getResource();
	}

	// 存值：
	public static void setPool(String key, String value) {
		getJedis().set(key, value);
	}

	// 取值：
	public static String getPool(String key) {
		return getJedis().get(key);
	}
	/*public static void main(String[] args) {
        setPool("hello","123");
    	getJedis().del("hello");
        System.out.println(getPool("test"));
        
        pool.close();
    }*/
}
