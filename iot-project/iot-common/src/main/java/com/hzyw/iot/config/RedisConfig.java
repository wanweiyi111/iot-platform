/*package com.hzyw.iot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.hzyw.iot.util.JedisPoolUtils;
import redis.clients.jedis.JedisPoolConfig;

@Configuration 
@PropertySource("classpath:redis.properties")
@ConfigurationProperties(prefix="spring.redis")
@EnableAutoConfiguration 
public class RedisConfig {
	private static Logger logger = Logger.getLogger(RedisConfig.class);  
	  
    //获取springboot配置文件的值 (get的时候获取)  
    @Value("${spring.redis.hostName}")  
    private String host;  
  
    @Value("${spring.redis.password}")  
    private String password;  
    
    //@Value("${spring.redis.port}")  
    static Integer port;
    static String host;
    static String password;
    
    @Value("${spring.redis.database}")  
    private String database;
    
    @Value("${spring.redis.timeout}")  
    private Integer timeout;
    
    @Value("${spring.redis.pool.max-active}")  
    private Integer maxActive;
    
    @Value("${spring.redis.pool.max-wait}")  
    private Integer maxWait;
    
    @Value("${spring.redis.pool.max-idle}")  
    private Integer maxIdle;
    
    @Value("${spring.redis.pool.min-idle}")  
    private Integer minIdle;
    
    @Value("${spring.redis.pool.max-total}")  
    private Integer maxTotal;
    
    static JedisPoolConfig poolConfig = null;
    static {
		// 手动加载加载配置文件
		InputStream in = JedisPoolUtils.class.getClassLoader().getResourceAsStream("redis.properties");
		Properties pro = new Properties();
		try {
			pro.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 获得池子对象
		poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(Integer.parseInt(pro.get("redis.maxIdle").toString()));// 最大闲置个数
		poolConfig.setMinIdle(Integer.parseInt(pro.get("redis.minIdle").toString()));// 最小闲置个数
		poolConfig.setMaxTotal(Integer.parseInt(pro.get("redis.maxTotal").toString()));// 最大连接数
		poolConfig.setTestOnBorrow(true);// 在borrow一个jedis实例的时候，是否需要验正作，如果是赋值true
		poolConfig.setTestOnReturn(true);// 在return。。。。
		poolConfig.setBlockWhenExhausted(true);// 连接耗尽时是否阻塞
		port = new Integer(pro.get("redis.port").toString());
		host = pro.get("redis.url").toString();
		password = pro.get("redis.passWord").toString();
		logger.info(">>>>host = " +host);
		logger.info(">>>>password = " +password);
		logger.info(">>>>port = " +port);
	}
     
     自动加载配置
    @Bean  
    @ConfigurationProperties(prefix = "spring.redis.pool")  
    public JedisPoolConfig getRedisConfig() {  
        JedisPoolConfig config = new JedisPoolConfig();  
        config.setMaxIdle(maxIdle);//最大闲置个数
        config.setMinIdle(minIdle);//最小闲置个数
        //config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxTotal(maxTotal);//最大连接数
        //config.setTestOnBorrow(true);// 在borrow一个jedis实例的时候，是否需要验正作，如果是赋值true
        //config.setTestOnReturn(true);// 在return。。。。
        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞
        return config;  
    }  
    
    @Bean  
    public JedisPoolConfig getRedisConfig() {  
        return poolConfig;  //扩展并手动从指定文件加载
    } 
  
    //@Bean  
    //@ConfigurationProperties()  
    public JedisConnectionFactory getConnectionFactory() {  
        JedisConnectionFactory factory = new JedisConnectionFactory();  
        factory.setUsePool(true);  
        JedisPoolConfig config = getRedisConfig();  
        factory.setPoolConfig(config);
        logger.info("JedisConnectionFactory bean init success.");  
        return factory;  
    }  
  
  
    @Bean  
    public RedisTemplate<?, ?> getRedisTemplate() {  
        JedisConnectionFactory factory = getConnectionFactory();  
        logger.info(this.host+","+factory.getHostName()+","+factory.getDatabase());  
        logger.info(this.password+",xxx"+factory.getPassword());  
        logger.info(factory.getPoolConfig().getMaxIdle());  
        factory.setHostName(this.host);  
        factory.setPassword(this.password);
        factory.setPort(port);
        //factory.set
        RedisTemplate<?, ?> template = new StringRedisTemplate(factory);  
        return template;  
    }  
}*/