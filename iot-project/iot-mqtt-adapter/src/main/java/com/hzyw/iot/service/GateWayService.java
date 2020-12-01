package com.hzyw.iot.service;

import com.hzyw.iot.vo.dataaccess.MessageVO;

public interface GateWayService {

	/**
	 * 服务上线
	 */
	void serviceOnline();
	
	/**
	 * 数据上报
	 */
	void dataAccess(GateWayService gateWayService);
	
	/**
	 * 数据下发(获取KAFKA下发数据，并调用下面的数据下发接口)
	 */
	void dataSendDown();
	
	/**
	 * 数据下发
	 * @param messageVO
	 */
	<T> void dataSendDown(MessageVO<T> messageVO);
	
	/**
	 * 平台是否在线
	 * @param serviceId
	 * @return
	 */
	boolean serviceOnLine(String serviceId);
	
	/**
	 * 设备是否在线
	 * @param deviceId
	 * @return
	 */
	boolean deviceOnLine(String deviceId);
}
