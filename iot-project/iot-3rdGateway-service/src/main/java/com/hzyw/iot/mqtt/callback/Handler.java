package com.hzyw.iot.mqtt.callback;


import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.util.GatewayMqttUtil;
import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;
import com.hzyw.iot.vo.dataaccess.DevOnOffline;
import com.hzyw.iot.vo.dataaccess.DevSignlResponseDataVO;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.MetricInfoResponseDataVO;
import com.hzyw.iot.vo.dataaccess.ResponseDataVO;
import com.hzyw.iot.vo.dataaccess.ResultMessageVO;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@Component
public class Handler {
	private static final Logger log = LoggerFactory.getLogger(Handler.class);
	/**
	 * 处理逻辑消息
	 */
	public void handlerMessages(String topic, String message) {
		JSONObject jsonObject =null;
		try{
			 jsonObject = JSONUtil.parseObj(message);//messageVO
		
		JSONObject data = JSONUtil.parseObj(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data));
		// 判断type类型
		String type = (String)jsonObject.get(GatewayMqttUtil.dataModel_messageVO_type);
		String deviceId = (String)jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId);
		DataType enumType = DataType.getByValue(type);
		//消息结构初始化
		MessageVO messageVo =null;
		
		//在这里统一做消息认证，认证通过继续执行 
		//---代码待增量开发加入即可..
		
		//下发的时候才需要判断设备是否在线，因上报的时候默认设备就是在线的
		
		//应该根据设备激活状态（建议这个状态丢到TAG里面去）+是否上线来决定以下的执行
		
		switch (enumType) {
		case DevInfoResponse://属性上报
			//设备基本消息
			DevInfoDataVO devInfoDataVo = new DevInfoDataVO();
			devInfoDataVo.setId(data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			//devInfoDataVo.setStatus(data.get(GatewayMqttUtil.dataModel_messageVO_data_status).toString());
			devInfoDataVo.setAttributers((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_attributers));
			devInfoDataVo.setMethods((List<String>)data.get(GatewayMqttUtil.dataModel_messageVO_data_methods));
			devInfoDataVo.setDefinedAttributers((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_definedAttributers) );
			devInfoDataVo.setDefinedMethods((List<String>)data.get(GatewayMqttUtil.dataModel_messageVO_data_definedMethods) );
			devInfoDataVo.setSignals((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_signals));
			devInfoDataVo.setTags((Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags));
			/*//消息结构
			messageVo.setType(type);
			messageVo.setSeq(Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_seq)));
			messageVo.setTimestamp(Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)));//消息上报时间
			messageVo.setMsgId((String)jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId));
			messageVo.setData(devInfoDataVo);*/
			
			messageVo= getMessageVO(devInfoDataVo,type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString(),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString());
			/*//HTTP 
			HttpRequest("http://localhost:8004/devInfoResponse",JSON.toJSONString(messageVo));*/
			mqttSend(type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			break;
			
		case Response://请求返回
			ResponseDataVO responseDataVo = new ResponseDataVO();
			responseDataVo.setId(data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			responseDataVo.setMethods((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_methods));
			responseDataVo.setTags((Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags));
			//消息结构
			messageVo= getMessageVO(responseDataVo,type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString(),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString(),(int)jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_messageCode));
			/*//HTTP 
			HttpRequest("http://localhost:8004/response",JSON.toJSONString(messageVo));*/
			mqttSend(type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			break;
		case MetricInfoResponse://设备状态数据上报
			MetricInfoResponseDataVO  metricInfoResponseDataVO = new MetricInfoResponseDataVO();
			metricInfoResponseDataVO.setId(data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			metricInfoResponseDataVO.setAttributers((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_attributers));
			metricInfoResponseDataVO.setDefinedAttributers((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_definedAttributers));
			metricInfoResponseDataVO.setTags((Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags));
			
			
			//消息结构
			messageVo= getMessageVO(metricInfoResponseDataVO,type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString(),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString());
			/*//HTTP 
			HttpRequest("http://localhost:8004/metricInfoResponse",JSON.toJSONString(messageVo));*/
			mqttSend(type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			break;
		case DevSignalResponse://设备信号上报
			DevSignlResponseDataVO devSignlResponseDataVO = new DevSignlResponseDataVO();
			devSignlResponseDataVO.setId(data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			devSignlResponseDataVO.setSignals((List<Map>)data.get(GatewayMqttUtil.dataModel_messageVO_data_signals));
			devSignlResponseDataVO.setTags((Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags));
			//消息结构
			messageVo= getMessageVO(devSignlResponseDataVO,type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString(),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString());
			/*//HTTP 
			HttpRequest("http://localhost:8004/devSignalResponse",JSON.toJSONString(messageVo));*/
			mqttSend(type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			break;
		case DevOnline://DEV在线离线
			DevOnOffline devOnline = new DevOnOffline();
			devOnline.setId(data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			devOnline.setStatus(data.get(GatewayMqttUtil.dataModel_messageVO_data_status).toString());
			devOnline.setTags((Map)data.get(GatewayMqttUtil.dataModel_messageVO_data_tags));
			//消息结构
			messageVo= getMessageVO(devOnline,type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_msgId).toString(),jsonObject.get(GatewayMqttUtil.dataModel_messageVO_data_gatewayId).toString());
			
			/*//HTTP 
			HttpRequest("http://localhost:8004/devOnline",JSON.toJSONString(messageVo));*/
			mqttSend(type,Convert.toLong(jsonObject.get(GatewayMqttUtil.dataModel_messageVO_timestamp)),data.get(GatewayMqttUtil.dataModel_messageVO_data_deviceId).toString());
			break;
		
        default:
            System.out.println("未知消息类型");
            break;
		}
	    //yes, it is
			}catch(Exception e){
				log.error("报错",e);
			}
			

	}
	
	/**
	 * 消息结构处理
	 */
	@SuppressWarnings("unchecked")
	public <T> MessageVO<T>  getMessageVO(T data,String type,Object timestamp,String msgId,String gwId) {
		//消息结构
		MessageVO<T> messageVo = new MessageVO<T>();
		//消息结构
		messageVo.setType(type);
		messageVo.setTimestamp(timestamp);//消息上报时间
		messageVo.setMsgId(msgId);
		messageVo.setData(data);
		messageVo.setGwId(gwId);
		return messageVo;
	}
	
	/**
	 * 消息结构处理(返回code)
	 */
	@SuppressWarnings("unchecked")
	public <T> MessageVO<T>  getMessageVO(T data,String type,Long timestamp,String msgId,String gwId,int messageCode) {
		//消息结构
		ResultMessageVO<T> messageVo = new ResultMessageVO<T>();
		//消息结构
		messageVo.setType(type);
		messageVo.setTimestamp(timestamp);//消息上报时间
		messageVo.setMsgId(msgId);
		messageVo.setData(data);
		messageVo.setGwId(gwId);
		messageVo.setMessageCode(messageCode);
		return messageVo;
	}
	
	/**
	 * HTTP推送数据
	 *//*
	public void HttpRequest(String url, String jsonObj) {
		String returnData =HttpRequest.post(url).body(jsonObj).execute().body();//json方式
		JSONObject returnJson=new JSONObject(returnData);
		if(!returnJson.get("code").equals("0")) {
			System.out.println("数据有误:"+returnData);
		}
		
	}*/
	
	/**
	 * MQTT推送数据
	 */
	public void mqttSend(String type,long timestamp,String id) {
		JSONObject json=new JSONObject();
		json.put("type", type);
		json.put("timestamp", timestamp);
		json.put("id", id);
		String topic = "v1/devices/me/telemetry";
        int qos = 1;
        String broker = "tcp://127.0.0.1:1883";
        String userName = "ACVFESAw8yWitU6kc5Ja";
        String password = "";
        String clientId = "pubClient";
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
            MqttMessage message = new MqttMessage(json.toString().getBytes());
            // 设置消息的服务质量
            message.setQos(qos);
            // 发布消息
            sampleClient.publish(topic, message);
            /*// 断开连接
            sampleClient.disconnect();
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
}
