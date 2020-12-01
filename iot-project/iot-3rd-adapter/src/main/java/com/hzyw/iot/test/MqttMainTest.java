package com.hzyw.iot.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.utils.md5.DeviceIdGenerator;
import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;
import com.hzyw.iot.vo.dataaccess.DevOnOffline;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.MetricInfoResponseDataVO;

import cn.hutool.core.date.DateUtil;
@Service
public class MqttMainTest {
	@Autowired
   	private  ObjectService objectService;
	
	 static String topic = "hzyw/001/ns1/0001-f82d132f9bb018ca-2001-ffff-acbc/iotpub";
	 static String content = "123Test";
	 static int qos = 1;
     static String broker = "tcp://47.106.189.255:1883";
     static String userName = "test";
     static String password = "test";
     static String clientId = "wanweiyi";

	
	    public static void main(String[] args) throws InterruptedException {
	    	/*for(;;) {
	    	String devInfoResponse = devInfoResponse("1000-890fd4146a6ff6c8-2001-ffff-9935", "1030-2034b82c04d657a1-200d-ffff-0dcf");//初始化
	    	publishMqtt(devInfoResponse);
	    	//System.out.println(devInfoResponse);
	    		//System.out.println("test");
	    		Thread.sleep(5000);
	    	}*/
	    
	    		for(int i = 0;i<100;i++) {
				String info = MqttMainTest.devInfoResponse("1000-890fd4146a6ff6c8-2001-ffff-9935", "1030-2034b82c04d657a1-200d-ffff-0dcf");
				MqttMainTest.publishMqtt(info);
				}
				for(int i = 0;i<100;i++) {
				String onoff =MqttMainTest.devOnOff("1000-890fd4146a6ff6c8-2001-ffff-9935");
				MqttMainTest.publishMqtt(onoff);
				}
				for(;;) {
			    	//String devInfoResponse = devInfoResponse();//初始化
			    	/*publishMqtt(devInfoResponse);
			    	System.out.println(devInfoResponse);*/
					for(int i = 0;i<100;i++) {
						String metricInfo = MqttMainTest.metricInfo("1000-890fd4146a6ff6c8-2001-ffff-9935");
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
	  
	    public static MqttClient mqtt() {
	    	// 内存存储
	        MemoryPersistence persistence = new MemoryPersistence();
	    	 // 创建客户端
          
			try {
				  MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
				// 创建链接参数
		            MqttConnectOptions connOpts = new MqttConnectOptions();
		            // 在重新启动和重新连接时记住状态
		            connOpts.setCleanSession(false);
		            // 设置连接的用户名
		            connOpts.setUserName(userName);
		            connOpts.setPassword(password.toCharArray());
		            // 遗愿消息
		           // connOpts.setWill("test/demo/willflag", "helloWorld~".getBytes(), qos, true);
		            // 建立连接
		            sampleClient.connect(connOpts);
		            return sampleClient;
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
            
	    }
	    
	    /**
		 *MQTT
	     * @param mq 
		 */
	    public static void publishMqtt(String msg) {
	    	// 内存存储
	        MemoryPersistence persistence = new MemoryPersistence();

	        try {
	            // 创建客户端
	            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
	            // 创建链接参数
	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            // 在重新启动和重新连接时记住状态
	            connOpts.setCleanSession(false);
	            // 设置连接的用户名
	            connOpts.setUserName(userName);
	            connOpts.setPassword(password.toCharArray());
	            // 遗愿消息
	           // connOpts.setWill("test/demo/willflag", "helloWorld~".getBytes(), qos, true);
	            // 建立连接
	            sampleClient.connect(connOpts);
	            // 创建消息
	            MqttMessage message = new MqttMessage(msg.getBytes());
	            // 设置消息的服务质量
	            message.setQos(qos);
	            // 发布消息
	            sampleClient.publish(topic, message);
	            // 断开连接
	            /*sampleClient.disconnect();
	            // 关闭客户端
	            sampleClient.close();*/
	        } catch (MqttException me) {
	            System.out.println("reason " + me.getReasonCode());
	            System.out.println("msg " + me.getMessage());
	            System.out.println("loc " + me.getLocalizedMessage());
	            System.out.println("cause " + me.getCause());
	            System.out.println("excep " + me);
	            me.printStackTrace();
	        }
	    }
	    /**
		 *属性上报
		 */
	    public static  String devInfoResponse(String sn,String uuid) {
	    	/*JSONObject jsonMogoDb = new JSONObject(); 
	    	jsonMogoDb.put("type", "mqtt"); 
	    	List<JSONObject> listJson = objectService.findObjects(jsonMogoDb);*/
	    	
	    	DevInfoDataVO devInfoDataVo = new DevInfoDataVO();
	    	List<Map> attributers = new ArrayList<Map>();
	    	List<String> methodsInfo = new ArrayList<String>();
	    	List<Map> definedAttributersInfo = new ArrayList<Map>();
	    	List<Map> signalsInfo = new ArrayList<Map>();
	    	Map<String, Object> tagsInfo = new HashMap<String, Object>();
	    	Map<String, Object> signals = new HashMap<String, Object>();
	    	
	    	
	    	//String sn = UUID.randomUUID().toString();//SN
	    	//String uuid = DeviceIdGenerator.generatorId(sn,4112,8208);//UUID
	    	Map<String, Object> attribute = new HashMap<String,Object>();
			attribute.put("vendor_code", 8208);
			attribute.put("device_type_name", "Light controller");
			attribute.put("version_software", "");
			attribute.put("vendor_name", "InnoSmart");
			attribute.put("ipaddr_v4", "");
			attribute.put("ipaddr_v6", "");
			attribute.put("uuid", uuid);
			attribute.put("version_hardware", "");
			attribute.put("malfunction", 0);
			attribute.put("date_of_production", "2020-01-03");
			attribute.put("mac_addr", "");
			attribute.put("device_type_code", 4112);
			attribute.put("up_time", 0);
			attribute.put("online", 1);
			attribute.put("model", "ODC_500_HP1");
			attribute.put("sn", sn);
			attributers.add(attribute);
	    	
			methodsInfo.add("set_time");
			methodsInfo.add("set_brightness");
			methodsInfo.add("set_onoff");
			methodsInfo.add("set_time_plan_task");
			methodsInfo.add("clean_time_plan_task");
			methodsInfo.add("set_ctrl_mode");
	    	
				/*Map<String,Object> typeValue =new HashMap<String,Object>();//自定义属性
				typeValue.put("type", "");
				typeValue.put("value", "");
				typeValue.put("company", "");
				definedAttributersInfo.add(typeValue);*/
			
			signals.put("lamp_offline", 1052673);
			signalsInfo.add(signals);
			
			tagsInfo.put("TestType", "MqttInfo");
			
			devInfoDataVo.setId(uuid);
	    	devInfoDataVo.setAttributers(attributers);//基本属性
			devInfoDataVo.setMethods(methodsInfo);//方法上报
			devInfoDataVo.setDefinedAttributers(definedAttributersInfo);//自定义属性
			devInfoDataVo.setSignals(signalsInfo);//信号
			devInfoDataVo.setTags(tagsInfo);//扩展
			
			// 消息结构1  
			MessageVO messageInfo = new MessageVO<>();
			messageInfo.setType("devInfoResponse");
			messageInfo.setTimestamp(DateUtil.currentSeconds());
			messageInfo.setMsgId(UUID.randomUUID().toString());
			messageInfo.setData(devInfoDataVo);
			messageInfo.setGwId("1000-f82d132f9bb018ca-2001-ffff-d28a");
			
			/*JSONObject json = new JSONObject(); 
			List<JSONObject> listMogodb = new ArrayList<JSONObject>();
			json.put("type", "mqtt"); 
	    	json.put("sn", sn); 
	    	json.put("uuid", uuid);
	    	listMogodb.add(json);
	    	objectService.saveObjects(listMogodb);*/
			System.out.println("属性上报:"+JSON.toJSONString(messageInfo));
			return JSON.toJSONString(messageInfo);
	    }
	    
	    /**
		 *设备上下线
		 */
	    public static String devOnOff(String uuid) {
	    	JSONObject json = new JSONObject();  
	    	json.put("type", "mqtt"); 
	    	//List<JSONObject> listJson = objectService.findObjects(json);
	    	//String uuid = listJson.get(i).get("uuid").toString();//SN
	    	DevOnOffline devOnline = new DevOnOffline();
	    	List<Map> attributers = new ArrayList<Map>();
	    	Map<String,Object> deviceInfo = new HashMap<String, Object>();
	    	Map<String,Object> tags = new HashMap<String, Object>();
	    	devOnline.setId(uuid);
			devOnline.setStatus("online");
			devOnline.setTags(tags);

		// 消息结构1
			MessageVO message = new MessageVO<>();
			message.setType("metricInfoResponse");
			message.setTimestamp(DateUtil.currentSeconds());
			message.setMsgId(UUID.randomUUID().toString());
			message.setData(devOnline);
			message.setGwId("1000-f82d132f9bb018ca-2001-ffff-d28a");
			System.out.println("设备上线:"+JSON.toJSONString(message));
			return JSON.toJSONString(message);
	    }
	
	    /**
		 *设备状态上报
		 */
	    public static String metricInfo(String uuid) {
	    	/*JSONObject json = new JSONObject(); 
			json.put("type", "mqtt"); 
	    	List<JSONObject> listJson = objectService.findObjects(json);*/

	    	List<Map> definedAttributers = new ArrayList<Map>();
	    	Map<String, Object> tags = new HashMap<String, Object>();
	    	Map<String, Object> attributersMap = new HashMap<String, Object>();
	    	MetricInfoResponseDataVO  metricInfoResponseDataVO = new MetricInfoResponseDataVO();
	    	
	    	
	    	attributersMap.put("type", "on_off");
	    	attributersMap.put("value", 0);
	    	attributersMap.put("company", "");
	    	definedAttributers.add(attributersMap);
	    	
	    	attributersMap.put("type", "brightness");
	    	attributersMap.put("value", 0);
	    	attributersMap.put("company", "");
	    	definedAttributers.add(attributersMap);
	    	
	    	attributersMap.put("type", "voltage");
	    	attributersMap.put("value", 228000);
	    	attributersMap.put("company", "mV");
	    	definedAttributers.add(attributersMap);
	    	
	    	
			metricInfoResponseDataVO.setId(uuid);
			metricInfoResponseDataVO.setDefinedAttributers(definedAttributers);//自定义属性
			metricInfoResponseDataVO.setTags(tags);//扩展
			// 消息结构1  
			MessageVO message = new MessageVO<>();
			message.setType("metricInfoResponse");
			message.setTimestamp(DateUtil.currentSeconds());
			message.setMsgId(UUID.randomUUID().toString());
			message.setData(metricInfoResponseDataVO);
			message.setGwId("1000-f82d132f9bb018ca-2001-ffff-d28a");
			System.out.println("设备上报:"+JSON.toJSONString(message));
			//publishMqtt(JSON.toJSONString(message));
			return JSON.toJSONString(message);
			
	    }
}
