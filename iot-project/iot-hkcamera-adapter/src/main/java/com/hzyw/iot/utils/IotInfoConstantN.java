package com.hzyw.iot.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
 
public class IotInfoConstantN {
	private static Logger logger = LoggerFactory.getLogger(IotInfoConstantN.class);
    public static final List<JSONObject> allDevInfo = new ArrayList<JSONObject>();
     
    //定义了以下设备属性字段在JSON中
	public static String deviceIp = "deviceIp";
	public static String deviceCode = "deviceCode";
	//public static String parkCode = "parkCode";
	public static String connUser = "connUser";
	public static String connPasswd = "connPasswd";
	public static String connPort = "connPort";
 	
    public static String device_json = ""; 
    public static void initData(){
    	try{
    		logger.info("===============初始化设备数据 =====>>>=============="); 
        	JSONArray  plc_data_map = JSONArray.fromObject(device_json);
        	for(int p=0; p < plc_data_map.size(); p++){
        		JSONObject jsonObject = (JSONObject) plc_data_map.get(p);
        		System.out.println(" --definde device: " +   jsonObject.toString() ); 
        		/*String deviceIp = (String)jsonObject.get("deviceIp");   
            	String deviceCode = (String)jsonObject.get("deviceCode");   
        		String parkCode = (String)jsonObject.get("parkCode");    
            	String connUser = (String)jsonObject.get("connUser");  
            	String connPasswd = (String)jsonObject.get("connPasswd"); 
            	String connPort = (String)jsonObject.get("connPort"); */
            	//----new一个集中器 并put到plc_iotInfo_
            	allDevInfo.add(jsonObject);
        	}
        	logger.info("===============初始化设备数据 完毕=====<<<=============="); 
        	  
    	}catch(Exception e){
    		logger.info("===============初始化设备数据异常、请检查数据是否合理!!!===================");
     	}
    }
      
}
