package com.hzyw.iot.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.service.AudioService;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.ReportService;
import com.hzyw.iot.util.IotInfoConstant;
import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;
import com.hzyw.iot.vo.dataaccess.DevOnOffline;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.MetricInfoResponseDataVO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
@Service
public class ReportServiceImpl implements ReportService{
private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	@Autowired
	private KafkaCommon kafkaCommon; //KAFKA工具类
	@Autowired
    private AudioService  audioService;
	@Autowired
	private ObjectService objectService;
	/**
	 * 上报设备属性数据(初始化)
	 */
	@Override
	public void AudioDeviceInfo() {
		if(!AudioServiceImpl.overallMap.containsKey("JSessionID")) {
		audioService.Login();
		System.out.println(AudioServiceImpl.overallMap.get("JSessionID"));
		}
		List TermIds= audioService.getTermIds();
		List Terms=audioService.getTermState(TermIds);//获取设备状态
		//System.out.println("Terms:"+Terms);
		
		/*List<JSONObject> list=new ArrayList<JSONObject>();
		JSONObject json = new JSONObject();
		json.put("vendor_code", 12293);
		json.put("serialNum", "27.242.21.8");
		json.put("device_type_name", "音柱");
		json.put("dev_base_vendor_name", "Audio");
		json.put("device_type_code", 8194);
		json.put("vendor_name", "Audio");
		json.put("model", "test");
		json.put("type", "Audio");
		json.put("deviceId", "2002-dbb948662c6019c3-3005-ffff-0163");
		json.put("sitePosition", "xxxx");
		list.add(json);
		objectService.saveUnits(list);*/
		
		
		for(int i = 0;i < Terms.size(); i ++){
		JSONObject json = new JSONObject(Terms.get(i));
		//System.out.println("json:"+json);
		String serialNum = json.get("IP").toString();
		
		if(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute") == null){
			//设备没有注册到本系统，请查看初始化数据里	面是否包含此设备
			logger.warn("设备["+serialNum+"]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
			return;
		}
		
		List<Map> attributers = new ArrayList<Map>();// 基本属性
		Map<String, Object> attribute = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute");//基本属性
		attributers.add(attribute);
		
		Map<String, Object> defAttribute = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute");//自定义属性
		List<Map> definedAttributers = new ArrayList<Map>();
		defAttribute.put("Name", 3);
		for (Map.Entry<String, Object> entry :defAttribute.entrySet()) {
			//System.out.println("key:"+entry.getKey()+"   value:"+entry.getValue());
			Map<String,Object> typeValue =new HashMap<String,Object>();
			typeValue.put("type", entry.getKey());
			typeValue.put("value", entry.getValue());
			if(IotInfoConstant.getUnit(entry.getKey())==null) {
				typeValue.put("company", "");
			}else {
				typeValue.put("company", IotInfoConstant.getUnit(entry.getKey()));
			}
			definedAttributers.add(typeValue);
		}
		
		
		List<Map> signals = new ArrayList<Map>();// 信号
		/*Map<String, Object> signls = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_signl");//信号
		signals.add(signls);*/
		
		List<String> methods = new ArrayList<String>();//方法上报
		methods.add("FileSessionSetStat");
		methods.add("TextPlay");
		methods.add("TermVolSet");
		
		Map<String, Object> tags = new HashMap<String, Object>();// tags
		tags.put(IotInfoConstant.dev_dataaccess_key, IotInfoConstant.dev_dataaccess_value);
		
		DevInfoDataVO devInfoDataVo = new DevInfoDataVO();
		devInfoDataVo.setId(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute").get(IotInfoConstant.dev_base_uuid).toString());//网关ID
		devInfoDataVo.setAttributers(attributers);//基本属性
		devInfoDataVo.setMethods(methods);//方法上报
		devInfoDataVo.setDefinedAttributers(definedAttributers);//自定义属性
		devInfoDataVo.setSignals(signals);//信号
		devInfoDataVo.setTags(tags);//扩展
		// 消息结构1  
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("devInfoResponse");
		messageVo.setTimestamp(DateUtil.currentSeconds());
		messageVo.setMsgId(UUID.randomUUID().toString());
		messageVo.setData(devInfoDataVo);
		messageVo.setGwId(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute").get(IotInfoConstant.dev_base_uuid).toString());
		logger.info("--初始化上报METRIC--" + JSON.toJSONString(messageVo));
		
		//设备上线
		AudioDevSignalResponse(attribute.get("uuid").toString(),"online");
		
		//SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo)); 
		//kafkaCommon.send("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo));
		}
	}
	/**
	 * 上报状态数据（解析推送过来的结算通知消息）
	 */
	@Override
	public void AudioMetricInfo() {
		if(!AudioServiceImpl.overallMap.containsKey("JSessionID")) {
			audioService.Login();
			System.out.println(AudioServiceImpl.overallMap.get("JSessionID"));
			}
			List TermIds= audioService.getTermIds();
			List Terms=audioService.getTermState(TermIds);//获取设备状态
			
			
			for(int i = 0;i < Terms.size(); i ++){
				JSONObject json = new JSONObject(Terms.get(i));
				//System.out.println("json:"+json);
				String serialNum = json.get("IP").toString();
				
				if(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute") == null){
					//设备没有注册到本系统，请查看初始化数据里	面是否包含此设备
					logger.warn("设备["+serialNum+"]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
					return;
				}
				//设备为1表示离线
				if((int)json.get("Status")==1) {
					AudioDevSignalResponse(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute").get(IotInfoConstant.dev_base_uuid).toString(),"offline");
				}
				List<Map> attributers = new ArrayList<Map>();// 基本属性
				
				List<Map> definedAttributers = new ArrayList<Map>();
				Map<String, Object> defAttribute = new HashMap<String, Object>();//自定义属性
				defAttribute.put("Status", json.get("Status"));
				defAttribute.put("Vol", json.get("Vol"));
				defAttribute.put("WorkStatus", json.get("WorkStatus"));
				defAttribute.put("Name", json.get("Name"));
				for (Map.Entry<String, Object> entry :defAttribute.entrySet()) {
					//System.out.println("key:"+entry.getKey()+"   value:"+entry.getValue());
					Map<String,Object> typeValue =new HashMap<String,Object>();
					typeValue.put("type", entry.getKey());
					typeValue.put("value", entry.getValue());
					if(IotInfoConstant.getUnit(entry.getKey())==null) {
						typeValue.put("company", "");
					}else {
						typeValue.put("company", IotInfoConstant.getUnit(entry.getKey()));
					}
					definedAttributers.add(typeValue);
				}
				
				
				Map<String, Object> tags = new HashMap<String, Object>();// tags
				
				MetricInfoResponseDataVO devInfoDataVo = new MetricInfoResponseDataVO();
				devInfoDataVo.setId(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute").get(IotInfoConstant.dev_base_uuid).toString());//网关ID
				devInfoDataVo.setAttributers(attributers);//基本属性
				devInfoDataVo.setDefinedAttributers(definedAttributers);//自定义属性
				devInfoDataVo.setTags(tags);//扩展
				// 消息结构1  
				MessageVO messageVo = new MessageVO<>();
				messageVo.setType("metricInfoResponse");
				messageVo.setTimestamp(DateUtil.currentSeconds());
				messageVo.setMsgId(UUID.randomUUID().toString());
				messageVo.setData(devInfoDataVo);
				messageVo.setGwId(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute").get(IotInfoConstant.dev_base_uuid).toString());
				logger.info("--状态上报METRIC--" + JSON.toJSONString(messageVo));
				//SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo)); 
				//kafkaCommon.send("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo));
				}
	}
	
	
	/**
	 * 设备上下线
	 */
	@Override
	public void AudioDevSignalResponse(String uuid,String OnOff) {
		// 设备上下VO
		DevOnOffline devOnline = new DevOnOffline();
		// 初始化设备属性
		IotInfoConstant iotInfoConstant = new IotInfoConstant();
		Map<String, Object> tags = new HashMap<String, Object>();// tags
		tags.put("agreement", "Audio");

		devOnline.setId(uuid);
		devOnline.setStatus(OnOff);
		devOnline.setTags(tags);
		// 消息结构
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("devOnline");
		messageVo.setTimestamp(DateUtil.currentSeconds());
		messageVo.setMsgId(UUID.randomUUID().toString());
		messageVo.setData(devOnline);
		messageVo.setGwId(uuid);

		// 集中器下的灯节点应该也要上报上线
		// todo

		System.out.println("设备上下线："+JSON.toJSONString(messageVo));
		//SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));

	}
	/**
	 * 上报信号（解析推送过来的事件消息）
	 */
	@Override
	public void AudioAlarm() {
		// TODO Auto-generated method stub
		
	}
	

}
