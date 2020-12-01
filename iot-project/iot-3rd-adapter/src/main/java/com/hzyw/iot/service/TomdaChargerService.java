package com.hzyw.iot.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 充电桩服务
 * @author Administrator
 *
 */
public interface TomdaChargerService {

	/**
	 * 上报设备属性数据
	 */
	public void parseTmdChargerDeviceInfo(JSONObject chargerDataMode,String time,String type);
	
	/**
	 * 上报状态数据（解析推送过来的结算通知消息）
	 */
	public void parseTmdChargerMetricInfo(JSONObject chargerDataMode,String time,String type);
	
	/**
	 * 上报信号（解析推送过来的事件消息）
	 */
	public void parseTmdChargerAlarm(JSONObject chargerDataMode,String time,String type);
	/**
	 *	工作数据上报
	 */
	public void parseTmdChargerWorkData(JSONObject contentJson, String time, String type);
	
	
	/**
	 *	wifi
	 */
	public void wifi();
}
