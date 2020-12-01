package com.hzyw.iot.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.mqtt.pub.CommPubHandler;
import com.hzyw.iot.netty.GlobalInfo;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.service.RedisService;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

@RestController
public class OrderController {
	 private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
	// 缓存测试
	@Autowired
	private RedisService redisService;
	
	private CommPubHandler commPubHandler;
	
	public OrderController(CommPubHandler commPubHandler) {
		this.commPubHandler = commPubHandler;
	}

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

	
	/**
	 * HTTP下发盒子指令
	 */
	@RequestMapping(value = "/request", method = RequestMethod.POST)
	public void request(@RequestBody JSONObject json) throws MqttException {
		System.out.println("下发指令到盒子:"+json);
		if(json!=null&&json.size()!=0) {
		commPubHandler.Publish(JSONUtil.parseObj(json).toString()); 
		}else {
			System.out.println("数据有误");
		}
	}
	
	
	
	
	/**
	 * netty下发测试
	 */
	@RequestMapping(value = "/nettyRequest", method = RequestMethod.POST)
	public void nettyRequest(@RequestBody JSONObject json) {
		System.out.println("netty下发");
        try {
        	Channel ctxTest = GlobalInfo.CHANNEL_INFO_MAP.get("nettyTest");//获取上下文
        	if(ctxTest==null) {
        		System.out.println("下发失败(没有拿到上下文)");
        		return;
        	}
        	byte[] byteTest ="helloWorld".getBytes();
        	ByteBuf byteBuf= ctxTest.alloc().buffer();
    		byteBuf.writeBytes(byteTest);
    		ctxTest.writeAndFlush(byteBuf)
    		.addListener((ChannelFutureListener) future -> { //监听下发的请求执行是否成功！
                if (future.isSuccess()) {
                	System.out.println("下发成功"); 
                } else {
                	System.out.println("下发失败"); 
                }
            });
		}catch (Exception e) {
			LOGGER.error("下发异常",e);
		}
	}
}
