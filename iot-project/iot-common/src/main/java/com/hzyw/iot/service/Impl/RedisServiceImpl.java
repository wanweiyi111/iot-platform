package com.hzyw.iot.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.dao.DataAccessException;  
import org.springframework.data.redis.connection.RedisConnection;  
import org.springframework.data.redis.core.RedisCallback;  
import org.springframework.data.redis.core.RedisTemplate;  
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.JSONUtil;

import javax.annotation.Resource;  
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;  
  
/** 
 */  
@Service  
public class RedisServiceImpl implements RedisService {  
  
    @Resource  
    private RedisTemplate<String, ?> redisTemplate;  
  
    @Override  
    public boolean set(final String key, final String value) {  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                connection.set(serializer.serialize(key), serializer.serialize(value));  
                return true;  
            }  
        });  
        return result;  
    }  
  
    @Override  
    public String get(final String key){  
        String result = redisTemplate.execute(new RedisCallback<String>() {  
            @Override  
            public String doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                byte[] value =  connection.get(serializer.serialize(key));  
                return serializer.deserialize(value);  
            }  
        });  
        return result;  
    } 
    
    public long getIncrement(final String key){  
    	RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return entityIdCounter.get();  
    } 
    
  
    @Override  
    public boolean expire(final String key, long expire) {  
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);  
    } 
    
    /* (non-Javadoc)
     * @see com.hzyw.iot.service.RedisService#increment(java.lang.String, long, long)
     */
    @Override  
    public long increment(String key, long liveTime, long delta) {  
    	RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
    	Long increment = entityIdCounter.addAndGet(delta);
    	if (liveTime > 0) {//初始设置过期时间
    		entityIdCounter.expire(liveTime, TimeUnit.SECONDS);//秒
    	}
        return increment;  //返回自增后的结果
    }
  
    @Override  
    public <T> boolean setList(String key, List<T> list) {  
        String value = JSONUtil.toJson(list);  
        return set(key,value);  
    }  
  
    @Override  
    public <T> List<T> getList(String key, Class<T> clz) {  
        String json = get(key);  
        if(json!=null){  
            List<T> list = JSONUtil.toList(json, clz);  
            return list;  
        }  
        return null;  
    }  
  
    @Override  
    public long lpush(final String key, Object obj) {  
        final String value = JSONUtil.toJson(obj);  
        long result = redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));  
                return count;  
            }  
        });  
        return result;  
    }  
  
    @Override  
    public long rpush(final String key, Object obj) {  
        final String value = JSONUtil.toJson(obj);  
        long result = redisTemplate.execute(new RedisCallback<Long>() {  
            @Override  
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));  
                return count;  
            }  
        });  
        return result;  
    }  
  
    @Override  
    public String lpop(final String key) {  
        String result = redisTemplate.execute(new RedisCallback<String>() {  
            @Override  
            public String doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                byte[] res =  connection.lPop(serializer.serialize(key));  
                return serializer.deserialize(res);  
            }  
        });  
        return result;  
    }  
    
    @Override  
    public void hmSet(String key,String field,String value) {  
        this.redisTemplate.opsForHash().put(key,field,value);
    } 
    
    @Override 
    public String hmGet(String key,String field) {  
       
		return  this.redisTemplate.opsForHash().get(key, field).toString();
    }
    
    @Override 
    public Map hmGetAll(String key,String field) {  
        return this.redisTemplate.opsForHash().entries(key);
    }
    
    @Override 
    public boolean hasHmkey(String key,String field) {  
        return this.redisTemplate.opsForHash().hasKey(key,field);
    }
    
    
}  
