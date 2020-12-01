package com.hzyw.iot.service;

import java.text.ParseException;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Administrator
 *
 */
public interface SwiftlinkService {
 
	public void parseDeviceInfo();
	 
	public String parseMetricInfo(JSONObject json) throws ParseException;
	
  
}
