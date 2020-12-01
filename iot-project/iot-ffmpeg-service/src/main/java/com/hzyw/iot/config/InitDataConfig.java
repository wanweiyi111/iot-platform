package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = { "classpath:initData.properties" }, encoding = "utf-8")
public class InitDataConfig {
	 
	@Value(value = "${iot.camera.initdata}")
	private String  plcJson;
	 
	@Bean
	public String initDevData(){
		//IotInfoConstant.plc_json = plcJson;
		//IotInfoConstant.plc_node_json = plcNodeJson;
		//IotInfoConstant.initData();
		return new String();
	}
	

}
