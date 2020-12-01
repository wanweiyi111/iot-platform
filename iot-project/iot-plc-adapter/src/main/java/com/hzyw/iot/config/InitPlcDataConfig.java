package com.hzyw.iot.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.utils.IotInfoConstant;

@Configuration
@PropertySource(value = { "classpath:initPlcData.properties" }, encoding = "utf-8")
public class InitPlcDataConfig {
	private static Logger logger = Logger.getLogger(InitPlcDataConfig.class); 
	@Autowired
   	private  ObjectService objectService; //mogoDb
	@Value(value = "${iot.plc.initdata.plc}")
	private String  plcJson;
	
	@Value(value = "${iot.plc.initdata.node}")
	private String  plcNodeJson;
	 
	@Bean
	public String initDevData(){
		//mongoDb
    	JSONObject json = new JSONObject();  
    	json.put("vendor_name", "innosmart"); 
    	json.put("type", "plc_jzq");
    	List<JSONObject> plc = objectService.findObjects(json);
    	//List<JSONObject> plcNode = objectService.findPlcNode(json);
    	//IotInfoConstant.plc_json = plc.toString();
		//IotInfoConstant.plc_node_json = plcNode.toString();
		
		
		/*IotInfoConstant.plc_json = plcJson;
		IotInfoConstant.plc_node_json = plcNodeJson;*/
		IotInfoConstant.initData(plc,objectService);
		return new String();
	}

	
	

}
