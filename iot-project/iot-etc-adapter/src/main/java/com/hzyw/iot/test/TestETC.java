package com.hzyw.iot.test;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.util.encoders.Hex;

import com.hzyw.iot.listener.ListenerService;
import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.thread.ThreadUtil;

public class TestETC {
		public static void main(String[] args) throws UnsupportedEncodingException {
			String test  ="d4c142333844383600000000";
			byte[] a = Hex.decode(test);
			String gbk = new String(a,"GBK");  
			System.out.println(gbk);  
		}
		
		//1190123010804349----卡片网络编号
	/*public final static TimedCache<String, String> timedCacheTest = CacheUtil.newTimedCache(6000);
	
	public static void main(String[] args) throws Exception {
		HeadHandlerUtil util =new HeadHandlerUtil();
		
		System.out.println(new String(util.hexStrToByteArr("d4c145415034343800000000"),"gbk"));
		//创建缓存，默认5秒过期
		TimedCache<String, String> timedCache = CacheUtil.newTimedCache(5000);
		timedCache.put("key", "test");//5秒过期
		//等待3秒
		for(int i =0;i<10;i++) {
		ThreadUtil.sleep(2000);
		System.out.println("值:"+timedCache.get("key"));
		//timedCache.put("key", "test",1000);
		timedCache.get("key", false);
		ThreadUtil.sleep(2000);
		timedCacheTest.put("key", "test");
		while (true) {
		Iterator<CacheObj<String, String>> entries = timedCacheTest.cacheObjIterator();
		while(entries.hasNext()){
		    CacheObj<String, String> entry = entries.next();
		    String key = entry.getKey();
		    String value = entry.getValue();
		    System.out.println(key+":"+value);
		    String retainKey = timedCacheTest.get(key);
		    System.out.println("保留"+retainKey);
		}
		Thread.sleep(3000);
		}
		
	}*/
		
	/*public static void main(String[] args) {
		//System.out.println(System.currentTimeMillis());
		String dateStr = Long.toString(System.currentTimeMillis()/1000L);
		System.out.println(dateStr); 
		Long a = System.currentTimeMillis();
		System.out.println("当前时间1:"+a);
		ThreadUtil.sleep(5000);
		Long s = (System.currentTimeMillis() - a) / (1000 * 1);
		System.out.println(s);
	}*/

}
