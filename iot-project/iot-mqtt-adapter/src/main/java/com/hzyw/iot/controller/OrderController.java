package com.hzyw.iot.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hzyw.iot.service.RedisService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
public class OrderController {

	// 缓存测试
	@Autowired
	private RedisService redisService;

	/**
	 * 
	 * @throws MqttException
	 */
	@RequestMapping(value = "/sos", method = RequestMethod.POST)
	public String sos(@RequestBody JSONObject json) throws MqttException {
		JSONObject jsonObject = JSONUtil.parseObj(json);
		
		/*PublishSample publish = new PublishSample();
		publish.Publish(jsonObject.get("topic").toString(), jsonObject.toString());*/

		return "hello";
	}

	/**
	 * 
	 * @throws MqttException
	 */
	@RequestMapping(value = "/testredis", method = RequestMethod.POST)
	public String testredis(@RequestBody JSONObject json) throws MqttException {
		JSONObject jsonObject = JSONUtil.parseObj(json);

		redisService.set("a", "aaaaaaaaaaatest");
		System.out.println("----" + redisService.get("a"));
		return redisService.get("a");
	}

	@RequestMapping(value = "/testredis1", method = RequestMethod.GET)
	public String testredis1() throws MqttException {
		redisService.set("a", "aaaaaaaaaaatest1");
		System.out.println("----" + redisService.get("a"));
		return redisService.get("a");
	}

}
