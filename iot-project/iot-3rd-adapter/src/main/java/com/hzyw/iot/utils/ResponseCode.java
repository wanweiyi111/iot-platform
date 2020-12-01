package com.hzyw.iot.utils;

public class ResponseCode {

	/**
	 * 指令执行成功
	 */
	public static final int HZYW_PLC_EXCU_SUCCESSFUL = 30000;
	
	/**
	 * 指令执行失败
	 */
	public static final int HZYW_PLC_EXCU_ERROR = 30001;
	
	
	/**
	 * 集中器返回成功
	 */
	public static final int HZYW_PLC_RESPONSE_SUCCESSFUL = 30002;  
	
	/**
	 * 集中器返回格式无效
	 */
	public static final int HZYW_PLC_RESPONSE_FORMAT_ERROR = 30003;  
	
	/**
	 * 集中器返回忙
	 */
	public static final int HZYW_PLC_RESPONSE_TIMEOU = 30004;    
	
	/**
	 * 设备正在处理中,忽略此操作
	 */
	public static final int HZYW_PLC_RESPONSE_BUSINESS = 30005; 
	
	   
	 
}
