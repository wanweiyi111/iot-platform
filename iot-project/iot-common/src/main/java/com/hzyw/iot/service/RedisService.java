package com.hzyw.iot.service;

import java.util.List;
import java.util.Map;
 

public interface RedisService {
	 
	    public boolean set(final String key, final String value);
	  
	    public String get(final String key);
	  
 	    public boolean expire(final String key, long expire); 
 	    
 	   public long getIncrement(final String key);
 	   /**
 	     * @param key
 	     * @param liveTime 超时时间/ 毫秒
 	     * @param delta  自增量
 	     * @return
 	     */
 	    public long increment(String key, long liveTime, long delta);
	  
 	    public <T> boolean setList(String key, List<T> list);
	  
 	    public <T> List<T> getList(String key, Class<T> clz);
	  
 	    public long lpush(final String key, Object obj);
	  
 	    public long rpush(final String key, Object obj); 
	  
 	    public String lpop(final String key);
 	    
 	   
 	    public void hmSet(String key,String field,String value);
 	    
  	    public String hmGet(String key,String field);
 	    
  	    public Map hmGetAll(String key,String field);
 	    
  	    public boolean hasHmkey(String key,String field);
}
