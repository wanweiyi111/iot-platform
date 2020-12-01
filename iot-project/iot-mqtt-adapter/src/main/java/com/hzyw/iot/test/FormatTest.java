package com.hzyw.iot.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hzyw.iot.bean.DeviceBean;
import com.hzyw.iot.vo.TagVO;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class FormatTest {
	
	public static void main(String[] args){
		/*DevInfoDataVO dinfo = new DevInfoDataVO();
		//dinfo.put("aaaa", "vlaue");
		dinfo.setDeviceId("xxxx");
		 
		List<Map> attributer = new ArrayList<Map>();
		Map<String,String> je = new HashMap<String,String>();
		je.put("attributer1", "-");
		Map<String,String> je1 = new HashMap<String,String>();
		je1.put("attributer2", "-");
		je1.put("attributer2desc", "-");
		attributer.add(je);
		attributer.add(je1);
		//dinfo.setAttributers(attributer);
		
		String testjson = JSON.toJSONString(dinfo);
		System.out.println(testjson);
		
		String json =new Gson().toJson(dinfo);
		System.out.println(json);
		JsonObject jsonObject =new JsonParser().parse(json).getAsJsonObject();
		System.out.println(jsonObject);
		
		MessageVO<DevInfoDataVO> msg = new MessageVO<DevInfoDataVO>(dinfo);
		msg.setMsgId("1111111111111111010011");
		 
		System.out.println(">>"+JSON.toJSONString(msg));*/
		
	}
	
	
	/**
	 *数据格式转换
	 */
	public void SetTransformation(String topic,String message) {
		 
		
		Map<String,String> deviceInfoMap = new HashMap<String,String>();//需要传入
		Map<String,String> deviceFieldMap = new HashMap<String,String>();// map<来源字段，本系统字段>
		deviceInfoMap.put("deviceId", "123456");
		deviceInfoMap.put("namespace", "test");
		
		//test 基本字段
		deviceFieldMap.put("on", "objectName");
		deviceFieldMap.put("timestamp", "timeStamp");
		
		//test其他字段
		deviceFieldMap.put("con", "conNum");
		deviceFieldMap.put("uslen", "uslenUnite");
		
		//DeviceBean deviceBean =new DeviceBean();
		//1,根据设备编号，获取设备 协议类型 
		JSONObject jsonObject = JSONUtil.parseObj(message);//前提条件 MESSAGE必须是JSON结构  
		//1,遍历KEY VALUE
		java.util.Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
		DeviceBean device = new DeviceBean();
		device.setDeviceId(deviceInfoMap.get("deviceId"));
		device.setNamespace(deviceInfoMap.get("namespace"));
		while(it.hasNext()) {
			Entry<String, Object> item = it.next();
			String key = item.getKey();
			String value = (String)item.getValue();
			//查询设备模型表，获取此设备下字段之间的映射关系
			if(deviceFieldMap.containsKey(key)
				&& 	deviceFieldMap.get(key).equals("objectName")) {
				device.setNamespace(value);
			}
			if(deviceFieldMap.containsKey(key)
					&& 	deviceFieldMap.get(key).equals("timeStamp")) {
					device.setTimeStamp(value);
			}
			
			
			if(deviceFieldMap.containsKey(key)) {
				if(device.getTags() != null) {
					TagVO tag = new TagVO(deviceFieldMap.get(key),value);
					device.getTags().add(tag);
				}else {
					List<TagVO> tags = new ArrayList<TagVO>();
					TagVO tag = new TagVO(deviceFieldMap.get(key),value);
					tags.add(tag);
					device.setTags(tags);
				}
			}else {
				if(device.getTags() != null) {
					TagVO tag = new TagVO(key,value);
					device.getTags().add(tag);
				}else {
					List<TagVO> tags = new ArrayList<TagVO>();
					TagVO tag = new TagVO(key,value);
					tags.add(tag);
					device.setTags(tags);
				}
			}
		}
		 
		
		//DeviceBean device = gson.fromJson(jsonObject.toString(), DeviceBean.class);
		
		
		System.out.println(device.getDeviceId());
		System.out.println(device.getAgreement());
		
		System.out.println(jsonObject.keySet());
		
		
		/*JSONObject jsonObject1 = JSONUtil.parseObj(device);
		System.out.println(jsonObject1);*/
		
		
		/*List<TagVO> objList = new ArrayList<>();
		TagVO vo = new TagVO();
		vo.setKey("test1");
		vo.setValue(jsonObject.get("demo1").toString());
		deviceBean.setTags(objList);
		
		System.out.println(deviceBean.getTags());
		for(TagVO attribute : deviceBean.getTags()) {
		  System.out.println("Key = " + attribute.getKey() + ", Value = " + attribute.getValue());
		}
		*/
		
		
		//存放未知数据
		List<Map<String, Object>> listTags = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapTags = new HashMap<String, Object>();
		mapTags.put("conNum", jsonObject.get("conNum").toString());
		mapTags.put("uslen", jsonObject.get("uslen").toString());
		mapTags.put("uslenUnite", jsonObject.get("uslenUnite").toString());
		listTags.add(mapTags);

		//存放已有数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("theme", topic);
		map.put("deviceId", device.getDeviceId());
		map.put("agreement", device.getAgreement());
		map.put("tags", listTags);
		JSONObject json = new JSONObject(map);
		System.out.println(json);  

	}
	
	
	
	
	
	
	/**
	 * 数据格式转换Test
	 */
	public void SetTransformationTest(String topic, String message) {
		Map<String, String> deviceFieldMap = new HashMap<String, String>();// map<来源字段，本系统字段>
		//基本字段
		deviceFieldMap.put("obj", "objectName");
		deviceFieldMap.put("time", "timeStamp");
		//其他字段
		deviceFieldMap.put("agree", "agreement");

		JSONObject jsonObject = JSONUtil.parseObj(message);// 前提条件 MESSAGE必须是JSON结构
		//遍历KEY VALUE
		java.util.Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
		DeviceBean device = new DeviceBean();
		//统一字段
		device.setDeviceId(jsonObject.get("deviceId").toString());
		device.setNamespace(jsonObject.get("namespace").toString());
		
		while (it.hasNext()) {
			Entry<String, Object> item = it.next();
			String key = item.getKey();
			String value = (String) item.getValue();
			if(key.equals("deviceId")||key.equals("namespace")) {
				continue;
			}
			// 查询设备模型表，获取此设备下字段之间的映射关系
			if (deviceFieldMap.containsKey(key) && deviceFieldMap.get(key).equals("objectName")) {
				device.setObjectName(value);
				continue;
			}
			if (deviceFieldMap.containsKey(key) && deviceFieldMap.get(key).equals("timeStamp")) {
				device.setTimeStamp(value);
				continue;
			}
			if (deviceFieldMap.containsKey(key) && deviceFieldMap.get(key).equals("agreement")) {
				device.setAgreement(value);
				continue;
			}
			
			//不识别的字段
			if(deviceFieldMap.containsKey(key)) {
				if(device.getTags() != null) {
					TagVO tag = new TagVO(deviceFieldMap.get(key),value);
					device.getTags().add(tag);
				}else {
					List<TagVO> tags = new ArrayList<TagVO>();
					TagVO tag = new TagVO(deviceFieldMap.get(key),value);
					tags.add(tag);
					device.setTags(tags);
				}
			}else {
				if(device.getTags() != null) {
					TagVO tag = new TagVO(key,value);
					device.getTags().add(tag);
				}else {
					List<TagVO> tags = new ArrayList<TagVO>();
					TagVO tag = new TagVO(key,value);
					tags.add(tag);
					device.setTags(tags);
				}
			}
		}
		
		JSONObject deviceJson = JSONUtil.parseObj(device);
		System.out.println(deviceJson);
		
	}

}
