package com.hzyw.iot.service.Impl;


import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.RedisService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.service.TomdaChargerService;
import com.hzyw.iot.util.constant.ConverUtil;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.md5.DeviceIdGenerator;
import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;
import com.hzyw.iot.vo.dataaccess.DevOnOffline;
import com.hzyw.iot.vo.dataaccess.DevSignlResponseDataVO;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.MetricInfoResponseDataVO;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
 
/**
 * 网关服务
 *
 */
@Service
public class TomdaChargerServiceImpl implements TomdaChargerService {
	
	private static final Logger logger = LoggerFactory.getLogger(TomdaChargerServiceImpl.class);
	
	@Autowired
	private KafkaCommon kafkaCommon; //KAFKA工具类
	
	@Autowired
	private ApplicationConfig applicationConfig;//全局配置

	@Autowired
	private RedisService redisService;
	
	@Autowired
	private ObjectService objectService;

	/* 完善充电桩主要功能:设备信息推送(初始化动作)，结算通知(设备状态数据上报)，事件数据推送(设备信号上报)
	 * 接收到的数据格式：参考Charger(充电桩)设备接入分析设计.excel==>接口协议 ===>设备信息推送
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerDeviceInfo(com.alibaba.fastjson.JSONObject)
	 */
	/* (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerDeviceInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public void parseTmdChargerDeviceInfo(JSONObject content,String time,String type) {
		System.out.println("设备信息推送==========================================");
		//Map<String,Object> content = (Map<String,Object>)chargerDataMode.get("content");//电桩数据
		
		String deviceType = (String) content.get("deviceType");//设备类型
		Map<String,Object> deviceInfo = (Map<String,Object>)content.get("deviceInfo");//设备信息
		
		String serialNum = deviceInfo.get("serialNum").toString();//电桩序列号
		int lost = (int) deviceInfo.get("lost");//0: 在线，1: 不在线
		int connectorCount = (int) deviceInfo.get("connectorCount");//充电口数量
		//String chargerStatus = deviceInfo.get("chargerStatus").toString();//充电桩状态，见附录 2
		String deviceName = deviceInfo.get("deviceName").toString();//设备名
		String chargerDeviceType =deviceInfo.get("chargerDeviceType").toString();//设备类型（可能增加）
		String chargerDeviceName =deviceInfo.get("chargerDeviceName").toString();//设备类型名称
		//String commuType =deviceInfo.get("commuType").toString();//通讯方式，包括：ETHERNET、WIFI、GPRS、SUB1G、ZIGBEE、BLUETOOTH、CAN、RS485
		//String deviceModel =deviceInfo.get("deviceModel").toString();//设备型号
		String hardwareVersion =deviceInfo.get("hardwareVersion").toString();//硬件版本
		String firmwareVersion =deviceInfo.get("firmwareVersion").toString();//固件版本
		List<Map<String, Object>> connectorInfo = (List<Map<String, Object>>) deviceInfo.get("connectorInfo");//充电口信息
		
		if(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute") == null){
			//设备没有注册到本系统，请查看初始化数据里	面是否包含此设备
			logger.warn("设备["+serialNum+"]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
			return;
		}
		
		//String time = (String)chargerDataMode.get("time");//"推送时间";   
		long pushKafka = System.currentTimeMillis();   //这里应该 =time ，因为现在不确定接口传递的时间格式，暂时取了系统时间，联调的时候记得改过来？？？？
		 
		List<Map> attributers = new ArrayList<Map>();// 基本属性
		Map<String, Object> attribute = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_attribute");//基本属性
		attribute.put(IotInfoConstant.dev_base_online, lost);//0:在线  1:离线
		attribute.put("version_software", hardwareVersion);//硬件版本
		attribute.put("version_hardware", firmwareVersion);//软件版本
		attribute.put("device_type_name", deviceName);//设备名
		attribute.put("model", chargerDeviceType);//型号
		
		attributers.add(attribute);
		
		Map<String, Object> defAttribute = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute");//自定义属性
		defAttribute.put("connectorCount", connectorCount);//充电口数量
		//defAttribute.put("deviceName", deviceName);//设备名
		defAttribute.put("chargerDeviceType", chargerDeviceType);//设备类型（可能增加）
		defAttribute.put("chargerDeviceName", chargerDeviceName);//设备类型名称
		//defAttribute.put("commuType", commuType);//通讯方式
		//defAttribute.put("deviceModel", deviceModel);//设备型号
		int j = 0;
		for(int i = 0 ; i < connectorInfo.size() ; i ++){
			++j;
			defAttribute.put("connectorId_"+j,connectorInfo.get(i).get("connectorId").toString());//充电口号
			defAttribute.put("connectorStatus_"+j,connectorInfo.get(i).get("connectorStatus").toString());//充电状态
		}
		List<Map> definedAttributers = new ArrayList<Map>();
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
		Map<String, Object> signls = IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_signl");//信号
		signals.add(signls);
		
		List<String> methods = new ArrayList<String>();//方法上报
		
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
		logger.info("--上报METRIC--" + JSON.toJSONString(messageVo));
		//SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo)); 
		kafkaCommon.send("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo));
		
	}

	/* 
	 * 接收到的数据格式： 参考Charger(充电桩)设备接入分析设计.excel==>接口协议 ===>结算通知推送
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerMetricInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public void parseTmdChargerMetricInfo(JSONObject chargerDataMode,String time,String type) {
		System.out.println("结算通知推送==========================================");
		//Map<String,Object> content = (Map<String,Object>)chargerDataMode.get("content");
		//List<Map<String,Object>> eventInfo = (List<Map<String,Object>>)content.get("eventInfo");
		
		String serialNum = (String)chargerDataMode.get("serialNum"); //设备序列号
		if(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute") == null){
			//设备没有注册到本系统，请查看初始化数据里面是否包含此设备
			logger.warn("设备["+serialNum+"]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
			return;
		}
		String deviceId =  (String)IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute").get(IotInfoConstant.dev_tomda_charger_id);//进入系统后定义的ID
		//String time = (String)chargerDataMode.get("time");//"推送时间";   		 
		int connectorId = (int)chargerDataMode.get("connectorId");
		//String clientOrderId = (String)chargerDataMode.get("clientOrderId");
		String chargeStartTime = (String)chargerDataMode.get("chargeStartTime");
		String chargeEndTime = (String)chargerDataMode.get("chargeEndTime");
		int chargeEnergy = (int)chargerDataMode.get("chargeEnergy");
		int chargeTime = (int)chargerDataMode.get("chargeTime");
		int chargeCost = (int)chargerDataMode.get("chargeCost");
		//String orderStatus = (String)chargerDataMode.get("orderStatus");
		String closeType = (String)chargerDataMode.get("closeType");
		List<Map> metricinfo = new ArrayList<Map>();
		//addMetric(IotInfoConstant.dev_plc_node_temperature, value,oldvalue ,IotInfoConstant.getUnit(IotInfoConstant.dev_plc_node_temperature) ,metricinfo);
		addMetric("connectorId", connectorId,connectorId ,null ,metricinfo);
		addMetric("closeType", closeType,closeType ,null ,metricinfo);
		addMetric("chargeStartTime", chargeStartTime,chargeStartTime ,null ,metricinfo);
		addMetric("chargeEndTime", chargeEndTime,chargeEndTime ,null ,metricinfo);
		addMetric("chargeEnergy", chargeEnergy,chargeEnergy ,null ,metricinfo);
		addMetric("chargeTime", chargeTime,chargeTime ,null ,metricinfo);
		addMetric("chargeCost", chargeCost,chargeCost ,null ,metricinfo);
		//addMetric("orderStatus", orderStatus,orderStatus ,null ,metricinfo); //BOOKED 预约,CHARGE 充电, PARK 停车,FINISH 充电完成状态,CLOSED已关闭,WAIT_PAY 等待付款
		
		//构造一条状态数据上报
		MetricInfoResponseDataVO  metricInfoResponseDataVO = new MetricInfoResponseDataVO();
		metricInfoResponseDataVO.setId(deviceId);
		//metricInfoResponseDataVO.setAttributers(null);
		metricInfoResponseDataVO.setDefinedAttributers(metricinfo);
		Map<String,String> tags = new HashMap<String,String>();
		tags.put(IotInfoConstant.dev_dataaccess_key, IotInfoConstant.dev_dataaccess_value); //指定接入类型是充电器接入类型
		tags.put("dataAccessTime", UUID.randomUUID().toString()); //表示
		tags.put("messageType", "ORDER_DATA"); 
		metricInfoResponseDataVO.setTags(tags);
		
		// 消息结构
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("metricInfoResponse");
		messageVo.setTimestamp(DateUtil.currentSeconds());  //时间应该等于time  待修改
		messageVo.setMsgId(UUID.randomUUID().toString());
		messageVo.setData(metricInfoResponseDataVO);
		messageVo.setGwId(deviceId);
		//send kafka
		logger.info("--上报METRIC--" + JSON.toJSONString(messageVo));
 		//SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
	}
	/* 
	 * 接收到的数据格式： 参考Charger(充电桩)设备接入分析设计.excel==>接口协议 ===>工作数据推送
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerMetricInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public void parseTmdChargerWorkData(JSONObject chargerDataMode, String time, String type) {
		System.out.println("工作数据推送==========================================");
		// Map<String,Object> content =
		// (Map<String,Object>)chargerDataMode.get("content");
		// List<Map<String,Object>> eventInfo =
		// (List<Map<String,Object>>)content.get("eventInfo");

		String serialNum = (String) chargerDataMode.get("serialNum"); // 设备序列号
		if (IotInfoConstant.tomda_charger_iotInfo_.get(serialNum + "_defAttribute") == null) {
			// 设备没有注册到本系统，请查看初始化数据里面是否包含此设备
			logger.warn("设备[" + serialNum + "]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
			return;
		}
		String deviceId = (String) IotInfoConstant.tomda_charger_iotInfo_.get(serialNum + "_defAttribute")
				.get(IotInfoConstant.dev_tomda_charger_id);// 进入系统后定义的ID
		List<Map<String, Object>> workDataInfo = (List<Map<String, Object>>) chargerDataMode.get("workDataInfo");
		for (int i = 0; i < workDataInfo.size(); i++) {
			int connectorId = (int) workDataInfo.get(i).get("connectorId");//充电口号，单枪桩传 0 或者不传
			// String clientOrderId = (String)chargerDataMode.get("clientOrderId");
			String chargerRunStatus = (String) workDataInfo.get(i).get("chargerRunStatus");//BOOKED 预约,CHARGE 充电, PARK 停车,FINISH 充电完成状态,CLOSED已关闭,WAIT_PAY 等待付款
			String deviceTime = (String) workDataInfo.get(i).get("deviceTime");//设备上报时间
			int chargeVoltage = (int) workDataInfo.get(i).get("chargeVoltage");//充电电压，单位：0.1V(实时输出)
			int chargeTime = (int) workDataInfo.get(i).get("chargeTime");//充电时长，单位：分钟
			int chargeCurrent = (int) workDataInfo.get(i).get("chargeCurrent");//充电电流，单位：0.1A(实时输出)
			int chargePower = (int) workDataInfo.get(i).get("chargePower");//充电功率，单位：W(实时输出)
			int chargeEnergy = (int) workDataInfo.get(i).get("chargeEnergy");//本次总电量，单位：0.001kWh(累加电量)
			int chargeCost = (int) workDataInfo.get(i).get("chargeCost");//本次消费金额，单位：0.01 元
			int soc = (int) workDataInfo.get(i).get("soc");
			List<Map> metricinfo = new ArrayList<Map>();
			// addMetric(IotInfoConstant.dev_plc_node_temperature, value,oldvalue
			// ,IotInfoConstant.getUnit(IotInfoConstant.dev_plc_node_temperature)
			// ,metricinfo);
			addMetric("connectorId", connectorId, connectorId, null, metricinfo);
			addMetric("chargerRunStatus", chargerRunStatus, chargerRunStatus, null, metricinfo);
			addMetric("deviceTime", deviceTime, deviceTime, null, metricinfo);
			addMetric("chargeVoltage", chargeVoltage, chargeVoltage, null, metricinfo);
			addMetric("chargeTime", chargeTime, chargeTime, null, metricinfo);
			addMetric("chargeCurrent", chargeCurrent, chargeCurrent, null, metricinfo);
			addMetric("chargePower", chargePower, chargePower, null, metricinfo);
			addMetric("chargeEnergy", chargeEnergy, chargeEnergy, null, metricinfo);
			addMetric("chargeCost", chargeCost, chargeCost, null, metricinfo);
			addMetric("soc", soc, soc, null, metricinfo);

			// 构造一条状态数据上报
			MetricInfoResponseDataVO metricInfoResponseDataVO = new MetricInfoResponseDataVO();
			metricInfoResponseDataVO.setId(deviceId);
			// metricInfoResponseDataVO.setAttributers(null);
			metricInfoResponseDataVO.setDefinedAttributers(metricinfo);
			Map<String, String> tags = new HashMap<String, String>();
			tags.put(IotInfoConstant.dev_dataaccess_key, IotInfoConstant.dev_dataaccess_value); // 指定接入类型是充电器接入类型
			tags.put("dataAccessTime", UUID.randomUUID().toString()); // 表示
			tags.put("messageType", "WORK_DATA");
			metricInfoResponseDataVO.setTags(tags);

			// 消息结构
			MessageVO messageVo = new MessageVO<>();
			messageVo.setType("metricInfoResponse");
			messageVo.setTimestamp(DateUtil.currentSeconds()); // 时间应该等于time 待修改
			messageVo.setMsgId(UUID.randomUUID().toString());
			messageVo.setData(metricInfoResponseDataVO);
			messageVo.setGwId(deviceId);
			// send kafka
			logger.info("--上报METRIC--" + JSON.toJSONString(messageVo));
			kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		}
	}
	/**
	 * 添加某个指标对应的状态值
	 * 
	 * @param type 字段
	 * @param value 字段值
	 * @param oldvalue 源报文对应到此字段的值
	 * @param unit  单位 -没有单位给空值
	 * @param metricinfo 集合
	 */
	public static void addMetric(String type,Object value ,Object oldvalue ,String unit ,List<Map> metricinfo){
		Map<String,Object> metric = new HashMap<String,Object>();
		metric.put("type", type);//字段
		metric.put("value", value);//字段值
		metric.put("sourceValue", oldvalue);//源报文上报的值
		metric.put("company", unit);//单位
		metricinfo.add(metric);
	}

	/* 
	 * 接收到的数据格式： 参考Charger(充电桩)设备接入分析设计.excel==>接口协议 ===>事件数据推送
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerAlarm(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public void parseTmdChargerAlarm(JSONObject content,String time,String type) {
		System.out.println("事件数据推送==========================================");
		//Map<String,Object> content = (Map<String,Object>)chargerDataMode.get("content");
		List<Map<String,Object>> eventInfo = (List<Map<String,Object>>)content.get("eventInfo");
		String serialNum = (String)content.get("serialNum"); //设备序列号
		//String eventSource = (String)content.get("eventSource"); //事件来源
		//int connectorId = (int)content.get("connectorId"); //充电口号
		//int eventCount = (int)content.get("eventCount"); //本次事件数量
		if(IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute") == null){
			//设备没有注册到本系统，请查看初始化数据里面是否包含此设备
			logger.warn("设备["+serialNum+"]没有注册到本系统,请查看初始化数据里面是否包含此设备!");
			return;
		}
		String deviceId =  (String)IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_defAttribute").get(IotInfoConstant.dev_tomda_charger_id);//进入系统后定义的ID
		//String time = (String)chargerDataMode.get("time");//"推送时间";   
		long pushKafka = System.currentTimeMillis();   //这里应该 =time ，因为现在不确定接口传递的时间格式，暂时取了系统时间，联调的时候记得改过来？？？？
		if(eventInfo != null){
			for(Map<String,Object> event : eventInfo){
				/*eventCode:<事件编码1>,
				eventName:<事件名称1>
				deviceTime: <事件发生时间1>*/
				String enCode = (String)event.get("eventCode");//事件编码  ，值等于设计文档=信号定义里的英文字段
				String eventName = (String)event.get("eventName");//事件名称
				String deviceTime = (String)event.get("deviceTime");//事件时间
				//String eventStatus = (String)event.get("eventStatus");//事件状态
				List<Map> signals = new ArrayList<Map>();
				Map signal = new HashMap();
				//signal =<信号编码key,信号编码>
				signal.put(enCode+"_CODE", IotInfoConstant.tomda_charger_iotInfo_.get(serialNum+"_signl_2").get(enCode));
				signals.add(signal);
				Map<String,String> tags = new HashMap<String,String>();//增加一些附加字段到tag
				tags.put(IotInfoConstant.dev_dataaccess_key, IotInfoConstant.dev_dataaccess_value);
				tags.put("eventName", eventName);
				tags.put("deviceTime", deviceTime); 
				pushDevSignlResponseDataVO(deviceId, type, signals, pushKafka, tags);
			}
		}else{
			logger.warn("设备["+serialNum+"]eventInfo = null!");
		}
		
	}
	
	/**
	 * 构造消息体并推送到KAFKA
	 * @param devId
	 * @param messageType
	 * @param signals
	 * @param pushKafka
	 * @param tags
	 */
	void pushDevSignlResponseDataVO(String devId,String messageType,List<Map> signals,long pushKafka,Map<String,String> tags){
		DevSignlResponseDataVO devSignlResponseDataVO = new DevSignlResponseDataVO();
		devSignlResponseDataVO.setId(devId); //设备ID
		devSignlResponseDataVO.setSignals(signals);
		//Map<String,String> tags = new HashMap<String,String>();
		tags.put(IotInfoConstant.dev_dataaccess_key, IotInfoConstant.dev_dataaccess_value); //指定接入类型是PLC接入类型
		devSignlResponseDataVO.setTags(tags);
		//消息结构
		MessageVO messageVo = getMessageVO(devSignlResponseDataVO,"devSignlResponse",pushKafka,devId+"_"+pushKafka+"_"+messageType,devId);
		logger.info("--上报signl--" + JSON.toJSONString(messageVo));
		//kafka处理
		//SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
	}
	
	public static <T> MessageVO<T>  getMessageVO(T data,String type,long timestamp,String msgId,String Plcid) {
		//消息结构
		MessageVO<T> messageVo = new MessageVO<T>();
		//消息结构
		messageVo.setType(type);
		messageVo.setTimestamp(timestamp);//取当前时间戳即可
		messageVo.setMsgId(msgId);
		messageVo.setData(data);
		messageVo.setGwId(Plcid);
		return messageVo;
	}
	
	
	@Override
	public void wifi() {
		//mogoDB查询
		JSONObject json = new JSONObject(); 
		json.put("vendor_name", "NAP");
		json.put("type", "wifi"); 
    	List<JSONObject> wifiInfo = objectService.findObjects(json);
		//VO属性上报
    	for (int i = 0; i < wifiInfo.size(); i++) {
		List<Map> attributers = new ArrayList<Map>();// 基本属性
		Map<String, Object> attribute = new HashMap<String,Object>();
		attribute.put("vendor_code", wifiInfo.get(i).get("vendor_code"));
		attribute.put("device_type_name", wifiInfo.get(i).get("device_type_name"));
		attribute.put("version_software", "");
		attribute.put("vendor_name", "信锐");
		attribute.put("ipaddr_v4", "");
		attribute.put("ipaddr_v6", "");
		attribute.put("uuid", wifiInfo.get(i).get("deviceId"));
		attribute.put("version_hardware", "");
		attribute.put("malfunction", "");
		attribute.put("date_of_production", "");
		attribute.put("mac_addr", "");
		attribute.put("device_type_code", wifiInfo.get(i).get("device_type_code"));
		attribute.put("up_time", "");
		attribute.put("online", 1);
		attribute.put("model", "MAC-6100");
		attribute.put("sn", wifiInfo.get(i).get("serialNum"));
		attributers.add(attribute);

		List<Map> definedAttributersInfo = new ArrayList<Map>();
	
		List<Map> signalsInfo = new ArrayList<Map>();// 信号
		
		List<String> methodsInfo = new ArrayList<String>();//方法上报
		
		Map<String, Object> tagsInfo = new HashMap<String, Object>();// tags
		tagsInfo.put("agreement", "3rd_wifi");
		
		DevInfoDataVO devInfoDataVo = new DevInfoDataVO();
		devInfoDataVo.setId("1040-4f97c6983a0a264b-3003-ffff-f813");//网关ID
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
		messageInfo.setGwId("1040-4f97c6983a0a264b-3003-ffff-f813");
		//logger.info("--上报METRIC--" + JSON.toJSONString(message));
		System.out.println("初始化上报:"+JSON.toJSONString(messageInfo));
		//kafkaCommon.send("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageInfo));
		sendKafka(JSON.toJSONString(messageInfo).toString(),"iot_topic_dataAcess_devInfoResponse");
    	}
		int number = 0;
		final long timeInterval = 10000;//定时10秒
				while (true) {
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
						//System.out.println("无线状态:"+test);
						
						//VO上下线
						cn.hutool.json.JSONObject data = JSONUtil.parseObj(test);
						List<Map> listMap =(List<Map>) data.get("data");
						String id =null;
						for(int i = 0 ; i < listMap.size() ; i ++){
							if(number==(int)listMap.get(i).get("status")) {
								System.out.println("已报了上下线");
								continue;
							}else {
								id = DeviceIdGenerator.generatorId(listMap.get(i).get("id").toString(),4160,12291);
								DevOnOffline devOnline = new DevOnOffline();
								Map<String, String> tags = new HashMap<>();
								tags.put("agreement", "3rd_wifi");
								MessageVO messageVo = new MessageVO();
								if((int)listMap.get(i).get("status")==1) {
								devOnline.setId(id);
								devOnline.setStatus("online");
								devOnline.setTags(tags);
								//消息结构
								messageVo.setType("devOnline");
								messageVo.setTimestamp(DateUtil.currentSeconds());
								messageVo.setMsgId(UUID.randomUUID().toString());
								messageVo.setData(devOnline);
								messageVo.setGwId(id);
								number=(int)listMap.get(i).get("status");
								sendKafka(JSON.toJSONString(messageVo).toString(),"iot_topic_dataAcess");
								//kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
								System.out.println("设备上下线:"+JSON.toJSONString(messageVo));
								//redisService.hmSet("state",id,"devOnline");
								}else {
									devOnline.setId(id);
									devOnline.setStatus("offline");
									devOnline.setTags(tags);
									//消息结构
									messageVo.setType("devOnline");
									messageVo.setTimestamp(DateUtil.currentSeconds());
									messageVo.setMsgId(UUID.randomUUID().toString());
									messageVo.setData(devOnline);
									messageVo.setGwId(id);
									number=(int)listMap.get(i).get("status");
									sendKafka(JSON.toJSONString(messageVo).toString(),"iot_topic_dataAcess");
									//kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
									System.out.println("设备上下线:"+JSON.toJSONString(messageVo));
									//redisService.hmSet("state",id,"offline");
								}
							}
							
						}
						
						
						
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
						//System.out.println("在线用户:"+listusrTest);
						
						//VO状态数据
						cn.hutool.json.JSONObject userWifi = JSONUtil.parseObj(listusrTest);
											
						List<Map> dataUser = (List<Map>) userWifi.get("data");
						
						List<Map> definedAttributers = new ArrayList<Map>();
						
						for (Map<String, Object> map : dataUser) {
						for (Map.Entry<String, Object> entry :map.entrySet()) {	
							//System.out.println("key:"+entry.getKey()+"   value:"+entry.getValue());
							Map<String,Object> typeValue =new HashMap<String,Object>();
							typeValue.put("type", entry.getKey());
							typeValue.put("value", entry.getValue());
							typeValue.put("company", "");
							definedAttributers.add(typeValue);
						}
						
						
						List<Map> signals = new ArrayList<Map>();// 信号
						
						List<String> methods = new ArrayList<String>();//方法上报
						
						Map<String, Object> tags = new HashMap<String, Object>();// tags
						tags.put("agreement", "3rd_wifi");
						
						MetricInfoResponseDataVO  metricInfoResponseDataVO = new MetricInfoResponseDataVO();
						metricInfoResponseDataVO.setId(id);//网关ID
						metricInfoResponseDataVO.setDefinedAttributers(definedAttributers);//自定义属性
						metricInfoResponseDataVO.setTags(tags);//扩展
						// 消息结构1  
						MessageVO message = new MessageVO<>();
						message.setType("metricInfoResponse");
						message.setTimestamp(DateUtil.currentSeconds());
						message.setMsgId(UUID.randomUUID().toString());
						message.setData(metricInfoResponseDataVO);
						message.setGwId(id);
						//logger.info("--上报METRIC--" + JSON.toJSONString(message));
						System.out.println("在线用户:"+JSON.toJSONString(message));
						sendKafka(JSON.toJSONString(message).toString(),"iot_topic_dataAcess");
						//kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(message));
						}
						
						/*//请求客流分析数据
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
						System.out.println("客流分析:"+guestAnsTest);*/
						
						System.out.println("定时间隔10秒");
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
		};
		
		
		/**
		 * setKafka处理
		 */
		public void sendKafka(String messageVo, String topic) {
			try {
				Producer<String, String> producer = kafkaCommon.getKafkaProducer();
				producer.send(new ProducerRecord<>(topic, messageVo));
				producer.close();
			} catch (Exception e) {
				logger.error(">>>Handler::handlerMessages::sendKafka ; producer.send异常 !",e);
			}
		}
		
	}

