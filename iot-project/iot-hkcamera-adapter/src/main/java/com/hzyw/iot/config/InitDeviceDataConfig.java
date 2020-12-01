package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.hzyw.iot.utils.IotInfoConstantN;

@Configuration
@PropertySource(value = { "classpath:initDeviceData.properties" }, encoding = "utf-8")
public class InitDeviceDataConfig {
	 
	@Value(value = "${iot.device.initdata}")
	private String  initdata;
	 
	@Bean
	public String initDevData(){
		IotInfoConstantN.device_json = initdata;
		//IotInfoConstant.initData();
		return new String();
	}
	

}
