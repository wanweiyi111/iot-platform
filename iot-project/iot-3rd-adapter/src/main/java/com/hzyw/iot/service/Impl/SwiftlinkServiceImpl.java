package com.hzyw.iot.service.Impl;


import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.service.SwiftlinkService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.hzyw.iot.utils.IotInfoConstantSwiftlink;
import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.MetricInfoResponseDataVO;
import cn.hutool.core.date.DateUtil;
 
/**
 * 网关服务
 *
 */
@Service
public class SwiftlinkServiceImpl implements SwiftlinkService {
	
	private static final Logger logger = LoggerFactory.getLogger(SwiftlinkServiceImpl.class);
	
	@Autowired
	private KafkaCommon kafkaCommon; //KAFKA工具类
	
	@Autowired
	private ApplicationConfig applicationConfig;//全局配置

	@Autowired
	private RedisService redisService;
	
	@Override
	public void parseDeviceInfo() {
		for(String key :IotInfoConstantSwiftlink.slk_face_iotInfo_.keySet()){
			if(key.contains("_attribute")){
				String face_dev_sn = (String)IotInfoConstantSwiftlink.slk_face_iotInfo_.get(key).get("sn");
				_parseDeviceInfo(face_dev_sn);//上报此SN设备的信息
			}
		}
	}
	 
	public void _parseDeviceInfo(String face_dev_sn) {
		//启动完毕调用这里一次，有数据上报（多了部分数据从设备上来），也可以上报一次
		//String face_dev_sn = (String)temp.get("device_sn"); //设备sn 
		if(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute") == null){
			logger.info(">>>["+face_dev_sn+"] not init device data to mongodb !!!");
			return;
		}
		 
		List<Map> attributers = new ArrayList<Map>();//基本属性
		attributers.add(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute"));
		
		List<Map> definedAttributers = new ArrayList<Map>();
		//IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_defAttribute").get(IotInfoConstantSwiftlink.slk_614_face_dev_addr);
		//IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_defAttribute").get(IotInfoConstantSwiftlink.slk_614_face_dev_no);
		definedAttributers.add(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_defAttribute"));
		
		List<Map> signals = new ArrayList<Map>();//信号
		Map<String, Object> signls = IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_signl"); 
		signals.add(signls);
		
		List<String> methods = new ArrayList<String>();//方法上报
		
		Map<String, Object> tags = new HashMap<String, Object>();// tags
		tags.put(IotInfoConstantSwiftlink.dev_dataaccess_key, IotInfoConstantSwiftlink.dev_dataaccess_value);
		
		DevInfoDataVO devInfoDataVo = new DevInfoDataVO();
		devInfoDataVo.setId(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute").get(IotInfoConstantSwiftlink.uuid).toString());//网关ID
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
		messageVo.setGwId(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute").get(IotInfoConstantSwiftlink.uuid).toString());
		logger.info("--push KAFKA DeviceInfo--" + JSON.toJSONString(messageVo));
		//SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo)); 
		kafkaCommon.send("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo));
		
	}
	 

	/* 
	 * 接收到的数据格式： 参考Charger(充电桩)设备接入分析设计.excel==>接口协议 ===>结算通知推送
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.TomdaChargerService#parseTmdChargerMetricInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public String parseMetricInfo(JSONObject json ) throws ParseException {
		String msgid = UUID.randomUUID().toString();
		logger.info(">>>msgid="+msgid+"/ putjson=" + json.toJSONString());
		String face_dev_sn = (String)json.get("device_sn"); //设备sn
		if(face_dev_sn == null ){
			logger.info(">>>msgid="+msgid+"/"+face_dev_sn+" not init device data to mongodb !!!");
			return "device_sn must be not null !";
		}
		if(IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute") == null ){
			logger.info(">>>msgid="+msgid+"/"+"not init device data to mongodb !!!");
			return ""+face_dev_sn+", must be init this dev data to mongodb!";
		}
		 
		String deviceId =  (String)IotInfoConstantSwiftlink.slk_face_iotInfo_.get(face_dev_sn+"_attribute").get(IotInfoConstantSwiftlink.uuid);//进入系统后定义的ID
		String match_person_id="",match_person_name="",match_image="",match_format="";
		if(json.containsKey("match") 
				&& json.getJSONObject("match").containsKey("person_id")){
			match_person_id = (String)json.getJSONObject("match").get("person_id");
			match_person_name = (String)json.getJSONObject("match").get("person_name");
			match_image = (String)json.getJSONObject("match").get("image");
			match_format = (String)json.getJSONObject("match").get("format");
		}
		
		String match_type = "face";//(String)json.get("match_type");目前就只有一个人脸的
		int match_face_quality = 0;
		java.math.BigDecimal match_temperatur = null;
		if(json.containsKey("person") 
				&& json.getJSONObject("person").containsKey("temperatur")){
			match_face_quality = (int)json.getJSONObject("person").get("face_quality");
			match_temperatur = (java.math.BigDecimal)json.getJSONObject("person").get("temperatur");
		}
		 
		int match_match_result = (int)json.get("match_result");
		String match_cap_time = (String)json.get("cap_time");
		String match_sequence_no = (String)json.get("sequence_no").toString();
		
		//增加两个图片
		String rta_image = "",rta_format="";
		if(json.containsKey("closeup_pic") 
				&& json.getJSONObject("closeup_pic").containsKey("data")){
			rta_image = (String)json.getJSONObject("closeup_pic").get("data");
			rta_format = (String)json.getJSONObject("closeup_pic").get("format");
		}
		
		//拦截一部分数据：没有实时捉拍到图片、没有注册的图片？、温度=0或0.0、无match字段
		if(!json.containsKey("person") || !json.getJSONObject("person").containsKey("temperatur")
				|| match_temperatur.intValue() == 0){
			//忽略没有温度或=0的数据
			logger.info(">>>msgid="+msgid+"/"+face_dev_sn+"， temperatur=0,ignore, return ! "  );
			return "person or person.temperatur field is not existens, ignore ,return fail .";
		}
		
		if(!json.containsKey("closeup_pic") || !json.getJSONObject("closeup_pic").containsKey("data")){
			//忽略没有温度或=0的数据
			logger.info(">>>msgid="+msgid+"/"+face_dev_sn+", rta image is null,ignore, return ! "  );
			return "closeup_pic or closeup_pic.data field is not existens, ignore ,return fail .";
		}
		 
		List<Map> metricinfo = new ArrayList<Map>();
		//addMetric(IotInfoConstant.dev_plc_node_temperature, value,oldvalue ,IotInfoConstant.getUnit(IotInfoConstant.dev_plc_node_temperature) ,metricinfo);
		addMetric("match_person_id", match_person_id,null ,null ,metricinfo);
		addMetric("match_person_name", match_person_name,null ,null ,metricinfo);
		addMetric("match_type", match_type,null ,null ,metricinfo);
		addMetric("match_face_quality", match_face_quality,null ,null ,metricinfo);
		addMetric("match_temperatur", match_temperatur,null ,"℃" ,metricinfo);
		addMetric("match_match_result", match_match_result,null ,null ,metricinfo);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
        Date date = sdf.parse(match_cap_time);
		addMetric("match_cap_time", date.getTime()/1000,null ,null ,metricinfo);
		addMetric("match_sequence_no", match_sequence_no,null ,null ,metricinfo);
		addMetric("match_image", match_image==null?"":match_image,null ,null ,metricinfo);
		addMetric("match_format", match_format==null?"":match_format,null ,null ,metricinfo);
        date = new Date();
 		addMetric("match_accessTime", date.getTime()/1000,null ,null ,metricinfo);
 		String match_company =  (String)IotInfoConstantSwiftlink.slk_face_iotInfo_
				.get(face_dev_sn+"_defAttribute").get(IotInfoConstantSwiftlink.slk_614_match_company); //从初始化数据里获取
 		addMetric("match_company", match_company , null ,null ,metricinfo);
 		
 		addMetric("rta_image", rta_image==null?"":rta_image,null ,null ,metricinfo);
		addMetric("rta_format", rta_format==null?"":rta_format,null ,null ,metricinfo);
 		
		Double xx =  ((Double)IotInfoConstantSwiftlink.slk_face_iotInfo_
				.get(face_dev_sn+"_defAttribute").get(IotInfoConstantSwiftlink.slk_614_temperatur_threshold)) ; 
		BigDecimal temperatur_threshold = new BigDecimal(Double.toString(xx.doubleValue()));
		
 		int match_alarm = (match_temperatur.compareTo(temperatur_threshold) > -1)?-1:0; //-1发烧 0正常
 		addMetric("match_alarm", match_alarm , null ,null ,metricinfo);
 		
 		//addMetric("temperatur_threshold", temperatur_threshold , null ,null ,metricinfo); //阈值 一会注销掉
 		 
		//构造一条状态数据上报
		MetricInfoResponseDataVO  metricInfoResponseDataVO = new MetricInfoResponseDataVO();
		metricInfoResponseDataVO.setId(deviceId);
		metricInfoResponseDataVO.setDefinedAttributers(metricinfo);
		Map<String,String> tags = new HashMap<String,String>();
		tags.put(IotInfoConstantSwiftlink.dev_dataaccess_key, IotInfoConstantSwiftlink.dev_dataaccess_value); //指定接入类型 
		metricInfoResponseDataVO.setTags(tags);
		
		// 消息结构
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("metricInfoResponse");
		messageVo.setTimestamp(date.getTime()/1000);  //当前系统时间 只上传10位的因为提供的数据目标给他们是10位的
		
		messageVo.setMsgId(msgid);
		messageVo.setData(metricInfoResponseDataVO);
		messageVo.setGwId(deviceId);
		//send kafka
		kafkaCommon.send("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		logger.info("--push KAFKA METRIC seccess, msgid="+msgid+"/"+face_dev_sn);
 		return "seccess!";
	}
	
	public  static  void main(String[] s){
		String t =  "2020/03/10 16:46:19.413";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
		try {
			System.out.println("- -- " + t);
			Date a = sdf.parse(t);
			long l = a.getTime();
			System.out.println("-1-- " + l);
			
			Date x = new Date(l);
			System.out.println("-x- " + x.getTime());
			System.out.println("-2-- " + sdf.format(x));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if(oldvalue != null){
			metric.put("sourceValue", oldvalue);//源报文上报的值
		}
		metric.put("company", unit);//单位
		metricinfo.add(metric);
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

