package com.hzyw.iot.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GatewayMqttUtil {
	  public static String getUUID(){
		  return UUID.randomUUID().toString();
	  }
 
	  public static final String dataModel_messageVO_type ="type";
	  public static final String dataModel_messageVO_data ="data";
	  public static final String dataModel_messageVO_seq ="seq";
	  public static final String dataModel_messageVO_timestamp ="timestamp";
	  public static final String dataModel_messageVO_msgId ="msgId";
	  public static final String dataModel_messageVO_messageCode ="messageCode";
	  public static final String dataModel_messageVO_message ="message";
	  
	  public static final String dataModel_messageVO_data_deviceId ="id";
	  public static final String dataModel_messageVO_data_status ="status";
	  public static final String dataModel_messageVO_data_attributers ="attributers";
	  public static final String dataModel_messageVO_data_methods ="methods";
	  public static final String dataModel_messageVO_data_definedAttributers ="definedAttributers";
	  public static final String dataModel_messageVO_data_definedMethods ="definedMethods";
	  public static final String dataModel_messageVO_data_signals ="signals";
	  public static final String dataModel_messageVO_data_tags ="tags";
	  public static final String dataModel_messageVO_data_messageCode ="messageCode";
	  
	  /**
	 * 网关ID
	 */
	public static final String dataModel_messageVO_data_gatewayId="gwId";
	
	public static final String rediskey_iot_cache_dataAccess="iotCacheDataAccess";
	
	/**
	 * 在线
	 */
	public static final String onLine="online";
	
	/**
	 * 下线
	 */
	public static final String offLine="offline";
	
	
	public static final String return_seccess_code="2000";
	public static final String return_seccess_message=" seccess! ";
	
	public static final String return_fail_code="2001";
	public static final String return_fail_message=" fail! ";
	
	public static final String return_devoffline_code="2002";
	public static final String return_devoffline_message="设备不在线! ";
	
	  
}
