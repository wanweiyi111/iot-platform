package com.hzyw.iot.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.service.SwiftlinkService;
import com.hzyw.iot.service.TomdaChargerService;
import com.hzyw.iot.test.MqttMainTest;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.md5.DeviceIdGenerator;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

/**
 * 人脸识别-Swiftlink厂商设备
 * @author Administrator
 *
 */
@RestController
public class SwiftlinkController {
	
	private static final Logger logger = LoggerFactory.getLogger(SwiftlinkController.class);
  
    @Autowired
	private SwiftlinkService swiftlinkService;  
    
    /**
     * @param json
     */
    @RequestMapping("/api/3rd/sklpushData")
    public JSONObject receiveNotify(@RequestBody String json) {
		 
		 JSONObject resultJsonParam = new JSONObject();	
		 try{
			 logger.info("======test --------------------!!! "  );
				//按消息类型处理 
				JSONObject contentJson = JSON.parseObject(json);
				//System.out.println("==>>>消息接收/" +contentJson.toJSONString());
				String result = swiftlinkService.parseMetricInfo(contentJson ); 
	 		    resultJsonParam.put("messageCode", "200");
	 		    resultJsonParam.put("message", result);
			}catch(Exception e){
				//resultJsonParam.put("errorCode", "201");
				logger.info("======excepiton:/api/3rd/sklpushData " ,e);
				logger.error("======excepiton:/api/3rd/sklpushData " ,e); 
				resultJsonParam.put("messageCode", "201");
			    resultJsonParam.put("message", "fail : "+e.getMessage());
			}
		 return resultJsonParam;
	}
 

    //模擬推送 10秒一個人經過，並推送一條數據
	public static void main(String[] args) {
		final long timeInterval = 3000;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					System.out.println(Thread.currentThread().getName() + " 定时间隔3秒");
					try {
						Date date = DateUtil.parse(DateUtil.today().toString());
						String format = DateUtil.format(date, "yyyyMMdd");//日期格式
						String getAps = format+"getAps"+"sangfor";//年月日，接口名，sangfor   无线状态查询接口
						String listusr = format+"listusr"+"sangfor";//请求在线
						String guestAns = format+"guestAns"+"sangfor";//请求客流分析数据
						Digester md5 = new Digester(DigestAlgorithm.MD5);
						String tokenGetAps = md5.digestHex(getAps);
						String tokenListusr = md5.digestHex(listusr);
						String tokenGuestAns = md5.digestHex(guestAns);
						
						Map<String, String> head = new HashMap<>();//头部
						Map<String, Object> getApsMap = new HashMap<>();//无线状态
						Map<String, Object> getApsData = new HashMap<>();
						
						head.put("POST /index.php/sys_runstat HTTP/1.1", "");
						head.put("Accept", " */*");
						head.put("Accept-Encoding", "gzip, deflate, br");
						head.put("Accept-Language", "zh-CN,zh;q=0.8,fi;q=0.6,en;q=0.4");
						head.put("Content-Length", "113");
						head.put("Host", "192.168.3.88");
						head.put("Origin", "https://192.168.3.88");
						head.put("Referer", "https://192.168.3.88/WLAN/index.php");
						head.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
						head.put("X-Requested-With", "XMLHttpRequest");
						head.put("Content-Type", "application/json");
						head.put("token", tokenGetAps);
						
						
						getApsMap.put("start", 0);
						getApsMap.put("limit", 25);
						getApsMap.put("search", "");
						getApsData.put("opr", "getAps");
						getApsData.put("data", getApsMap);
						
						//查询无线状态
						cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(getApsData);
						String test =HttpRequest.post("https://localhost:8001/api/3rd/sklpushData").body(jsonObject).addHeaders(head).execute().body();//json方式
						System.out.println("无线状态:"+test);
						
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	
	 
}
 