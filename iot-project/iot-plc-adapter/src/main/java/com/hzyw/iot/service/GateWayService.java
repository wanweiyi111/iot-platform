package com.hzyw.iot.service;

public interface GateWayService {

	/**
	 * kafka消费PLC指令
	 */
	public void dataSendDown(RedisService redisService);
	
	/**
	 * 获取当前PLC类型设备所有灯节点下的节点详情信息
	 */
	public void getPLCMetricInfo();
	
}
