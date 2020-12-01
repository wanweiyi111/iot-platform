package com.hzyw.iot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hzyw.iot.bean.DeviceBean;
import com.hzyw.iot.vo.TagVO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
 
public class Format {
	/**
	 * 数据转换
	 */
	public void transFormation(Map<String, Object> deviceInfoMap, Map<String, Object> deviceFieldMap, String message,DeviceBean device) {
		JSONObject jsonObject = JSONUtil.parseObj(message);
		java.util.Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
		Format format = new Format();
		// 查询设备模型表，获取此设备下字段之间的映射关系
		back:
		while (it.hasNext()) {
			Entry<String, Object> item = it.next();
			String key = item.getKey();
			String value = (String) item.getValue();
			
			// 查询设备模型表，获取此设备下字段之间的映射关系
			if (deviceFieldMap.containsKey(key)) {
				format.otherMapping(deviceFieldMap.get(key).toString(), value,device);
				continue;
			}else {
				// 找出基本字段
				for (String infoKey : deviceInfoMap.keySet()) {
					if (key.equals(infoKey)) {
						String infoValue = (String) deviceInfoMap.get(infoKey);
						format.basicMapping(infoKey, infoValue, device);
						continue back;
					}
				}
				//其他字段
				if(CollUtil.isNotEmpty(device.getTags())) {
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

	}

	
	/**
	 * 基本字段映射
	 */
	public void basicMapping(String infoKey,String infoValue,DeviceBean device) {
		switch(infoKey) {
		case "deviceId":
			device.setDeviceId(infoValue);;
			break;
			
		case "namespace":
			device.setNamespace(infoValue);;
			break;
		}
	}
	
	
	
	/**
	 * 其他字段映射
	 */
	public void otherMapping(String mappingKey,String value,DeviceBean device) {
		switch(mappingKey) {
		case "objectName":
			device.setObjectName(value);
			break;
			
		case "timeStamp":
			device.setTimeStamp(value);;
			break;
		
		case "agreement":
			device.setAgreement(value);;
			break;	
		}
	}
	
	
	
	/**
	 * 配置数据
	 */
	public void configure(String topic, String message) {
		JSONObject jsonObject = JSONUtil.parseObj(message);// 前提条件 MESSAGE必须是JSON结构
		Map<String,Object> deviceInfoMap = new HashMap<String,Object>();//需要传入
		Map<String,Object> deviceFieldMap = new HashMap<String,Object>();// map<来源字段，本系统字段>
		DeviceBean device = new DeviceBean();
		//基本字段
		deviceInfoMap.put("deviceId", jsonObject.get("deviceId"));
		deviceInfoMap.put("namespace", jsonObject.get("namespace"));
		//其他字段
		deviceFieldMap.put("obj", "objectName");
		deviceFieldMap.put("time", "timeStamp");
		deviceFieldMap.put("agree", "agreement");
		
		Format format = new Format();
		format.transFormation(deviceInfoMap,deviceFieldMap,message,device);
		
		
		JSONObject deviceJson = JSONUtil.parseObj(device);
		System.out.println(deviceJson);
	}		
	
	
}
