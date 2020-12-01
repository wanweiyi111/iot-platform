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
import io.netty.channel.ChannelHandlerContext;

/**
 * 充电桩
 * @author Administrator
 *
 */
@RestController
public class TomdaChargerController {
	
	private static final Logger logger = LoggerFactory.getLogger(TomdaChargerController.class);
	
    @Autowired
	private RedisService redisService; 
    
    @Autowired
	private MqttMainTest MqttMainTest;
    
    @Autowired
   	private  ObjectService objectService;
    
    /**
     * 充电桩服务
     */
    @Autowired
	private TomdaChargerService tomdaChargerService;  
    
	/**
	 * 数据计入服务API
	 * 
	 */
	 @ResponseBody
	 @RequestMapping(value = "/3rd/tomda/pushData", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	    public String generatorId(@RequestParam String time,String type, String content,HttpServletRequest request) {
		 System.out.println("接口进入===================="+"time==="+time+"type==="+type+"content====="+content);
		 JSONObject resultJsonParam = new JSONObject();	
		 try{
				//校验输入数据格式
				if(time == null 
						|| type == null
						|| content == null){
					logger.info("===>无效的数据格式数据!!! " );
					resultJsonParam.put("msg", "fail");
				    resultJsonParam.put("data", "无效的数据格式数据,必须包含type,time,content !");
					return resultJsonParam.toJSONString();
				}
				//按消息类型处理 
				JSONObject contentJson = JSON.parseObject(content);
				if("DEVICE_INFO".equals(type)) { //设备信息推送,一分钟推送一次
					tomdaChargerService.parseTmdChargerDeviceInfo(contentJson,time,type);
				}else if("ORDER_DATA".equals(type)){ //结算通知推送,1分钟提送一次 (充电记录)
					tomdaChargerService.parseTmdChargerMetricInfo(contentJson,time,type);
				}else if("EVENT_INFO".equals(type)){ //事件推送 ,触发事件即推送
					tomdaChargerService.parseTmdChargerAlarm(contentJson,time,type);
				}else if("WORK_DATA".equals(type)){ //工作数据
					tomdaChargerService.parseTmdChargerWorkData(contentJson,time,type);
				}
	 		    resultJsonParam.put("errorCode", "200");
			}catch(Exception e){
				resultJsonParam.put("errorCode", "201");
				logger.error("错误方法定位:3rd/tomda/pushData,time="+time+"type类型="+type+",充电桩推送数据异常,data=" + content ,e); 
				resultJsonParam.put("errorDesc", "fail");
			    resultJsonParam.put("data", content);
			}
		    return resultJsonParam.toJSONString();
	    }

	/**
	 * wifi
	 */
	@ResponseBody	
	@RequestMapping(value = "/3rd/sundray/wifi",  method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String wifi(@RequestParam String time, String type, String content, HttpServletRequest request) {
	        System.out.println(request.getHeader("token"));
		return "helloworld~";
	}

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
						String test =HttpRequest.post("https://192.168.3.88/index.php/sys_runstat").body(jsonObject).addHeaders(head).execute().body();//json方式
						System.out.println("无线状态:"+test);
						
						//在线数据请求
						head.remove("token");
						head.put("token", tokenListusr);
						Map<String, Object> listusrMap = new HashMap<>();
						Map<String, Object> listusrData = new HashMap<>();
						Map<String, Object> filter = new HashMap<>();
						filter.put("filterType", "all");
						filter.put("id", 100);
						filter.put("gid", -1);
						filter.put("type", 888);
						listusrData.put("start", 0);
						listusrData.put("limit", 25);
						listusrData.put("filter", filter);
						listusrMap.put("opr", "listusr");
						listusrMap.put("search", "");
						listusrMap.put("sort", "onlineTime");
						listusrMap.put("direction", "DESC");
						listusrMap.put("searchValue", "");
						listusrMap.put("is_deposit", false);
						listusrMap.put("is_central_area", false);
						listusrMap.put("data", listusrData);
						
						cn.hutool.json.JSONObject listusrJson = JSONUtil.parseObj(listusrMap);
						String listusrTest =HttpRequest.post("https://192.168.3.88/index.php/sys_runstat").body(listusrJson).addHeaders(head).execute().body();//json方式
						System.out.println("在线用户:"+listusrTest);
						
						//请求客流分析数据
						head.remove("token");
						head.put("token", tokenGuestAns);
						Map<String, Object> guestAnsMap = new HashMap<>();
						guestAnsMap.put("opr", "guestAns");
						guestAnsMap.put("start", 0);
						guestAnsMap.put("limit", 200);
						guestAnsMap.put("time_type", "last7day");
						guestAnsMap.put("type", "AP组");
						guestAnsMap.put("name", "/全部");
						guestAnsMap.put("id", -1);
						
						cn.hutool.json.JSONObject guestAnsJson = JSONUtil.parseObj(guestAnsMap);
						String guestAnsTest =HttpRequest.post("https://192.168.3.88/index.php/sys_runstat").body(guestAnsJson).addHeaders(head).execute().body();//json方式
						System.out.println("客流分析:"+guestAnsTest);
						
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

	
	@ResponseBody
	@RequestMapping(value = "/test/hello", method = RequestMethod.POST)
	public void testConsumer1(@RequestBody JSONObject jsonobjct) {
			System.out.println("hello");
			
			JSONObject jsonMogoDb = new JSONObject(); 
	    	jsonMogoDb.put("type", "mqtt"); 
	    	List<JSONObject> listJson = objectService.findObjects(jsonMogoDb);
	    	
			//MqttClient mq = MqttMainTest.mqtt();
			for(int i = 0;i<100;i++) {
			String info = MqttMainTest.devInfoResponse(listJson.get(i).get("sn").toString(),listJson.get(i).get("uuid").toString());
			MqttMainTest.publishMqtt(info);
			}
			for(int i = 0;i<100;i++) {
			String onoff =MqttMainTest.devOnOff(listJson.get(i).get("uuid").toString());
			MqttMainTest.publishMqtt(onoff);
			}
			for(;;) {
		    	//String devInfoResponse = devInfoResponse();//初始化
		    	/*publishMqtt(devInfoResponse);
		    	System.out.println(devInfoResponse);*/
				for(int i = 0;i<100;i++) {
					String metricInfo = MqttMainTest.metricInfo(listJson.get(i).get("uuid").toString());
					MqttMainTest.publishMqtt(metricInfo);
				}
				try {
					System.out.println("睡眠30秒");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	}
	    }
}
 