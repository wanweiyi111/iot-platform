package com.hzyw.iot.service;

public interface ReportService {
	/**
	 * 上报设备属性数据
	 */
	public void AudioDeviceInfo();
	/**
	 * 上报状态数据（解析推送过来的结算通知消息）
	 */
	public void AudioMetricInfo();
	/**
	 * 上报信号（解析推送过来的事件消息）
	 */
	public void AudioAlarm();
	
	/**
	 * 设备上下线（解析推送过来的事件消息）
	 */
	public void AudioDevSignalResponse(String uuid,String OnOff);
	
	
}
