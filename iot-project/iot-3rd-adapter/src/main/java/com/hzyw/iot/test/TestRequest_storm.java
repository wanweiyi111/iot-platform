package com.hzyw.iot.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import redis.clients.jedis.Jedis;

/**
 * 测试
 * @author Administrator
 *
 */
public class TestRequest_storm {
		public static void main(String[] args) {
			Map<String, Object> paramMap = new HashMap<>();
			Map<String, String> head = new HashMap<>();
			paramMap.put("time", "4112");
			paramMap.put("type", "12289");
			paramMap.put("content", "000000100");
			head.put("token", "test123");
			JSONObject jsonObject = JSONUtil.parseObj(paramMap);
			//String test = HttpUtil.post("http://127.0.0.1:8008/3rd/sundray/wifi", paramMap);
			String test =HttpRequest.post("http://127.0.0.1:8008/3rd/sundray/wifi").body(jsonObject).addHeaders(head).execute().body();//json方式
	       
			/*//from表单方式
			String test = HttpRequest.post("http://127.0.0.1:8008/3rd/sundray/wifi")
			    .addHeaders(head)//头信息，多个头信息多次调用此方法即可
			    .form(paramMap)//表单内容
			    .timeout(20000)//超时，毫秒
			    .execute().body();*/
			//Console.log(test);
			
			System.out.println("返回值:"+test);
			
			
			/*Date date = DateUtil.parse(DateUtil.today().toString());
			String format = DateUtil.format(date, "yyyyMMdd");//日期格式
			String token = format+"getApssangfor";
			Digester md5 = new Digester(DigestAlgorithm.MD5);
			String digestHex = md5.digestHex(token);
			System.out.println(digestHex);//MD5*/		
			}
		
		
}
