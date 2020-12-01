package com.hzyw.iot.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hzyw.iot.service.RedisService;

/**
 * KAFKA消费-吞吐量统计
 * @author Administrator
 *
 */
public class TpsCountUtils {
	static Logger logger = LoggerFactory.getLogger(TpsCountUtils.class);
	
	public static final String plcSendDown = "plcSendDown_";
	 
	/**
	 * 计数器
	 * 
	 * 统计每分钟下发指令
	 * @param redisService
	 * @param key   如果是统计每分钟 key=yyyyMMddHHmm  当前分钟,也可以统计小时,天
	 * @param cacheSeconds   超时时间 /秒
	 */
	public static void plcSendDownSetIncr(RedisService redisService,String key, int cacheSeconds){
		long result = 0;
		String newkey = "";
 		try {
 			Date date = new Date();
 			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
 			newkey = key + dateFormat.format(date);
  			result = redisService.increment(newkey, cacheSeconds, 1);//当前分钟
		} catch (Exception e) {
			logger.error("====TpsCountUtils::plcSendDownSetIncr===exception!=== set "+ newkey + " = " + result,e);
		}  
		//return result；
	}
	
	//获取上一分钟下发的指令数
	public static long getTpsCountPlcSendDown(RedisService redisService, String key) {
		long result = 0;
		try {
			result = redisService.getIncrement(key);
		} catch (Exception e) {
			logger.error("====TpsCountUtils::getTpsCountPlcSendDown===exception!===  " + key + " = " + result,e);
		}
		return result;
	}
}
