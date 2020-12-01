package com.hzyw.iot.vo.dataaccess;

/**
 * 
 * 设备数据接入-消息类型
 *
 */
public enum DataType {
	ServiceOnline("serviceOnline"),   //平台上线
	ServiceOffline("serviceOffline"),//平台下线
	DevLogin("devLogin"),   //DEV登陆认证    暂时用不到，每次请求都会做消息认证
	/**
	 * DEV在线离线
	 */
	DevOnline("devOnline"),    
	  
	Request("request"),     //下发请求
	/**
	 * 上报'下发请求'结果
	 */
	Response("response"),   
	/**
	 * 属性上报
	 */
	DevInfoResponse("devInfoResponse"),    
	/**
	 * 设备状态数据上报
	 */
	MetricInfoResponse("metricInfoResponse"),  
	/**
	 * 设备信号上报
	 */
	DevSignalResponse("devSignalResponse");     

	private String messageType;

	private DataType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public static DataType getByValue(String value) {
		for (DataType code : values()) {
			if (value!=null && value.equals(code.getMessageType()) ) {
				return code;
			}
		}
		return null;
	}
    

}
