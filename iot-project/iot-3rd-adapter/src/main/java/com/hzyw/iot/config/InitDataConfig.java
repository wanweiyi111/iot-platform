package com.hzyw.iot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.SwiftlinkService;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.IotInfoConstantSwiftlink;

@Configuration
//@PropertySource(value = { "classpath:InitTomdaData.properties" }, encoding = "utf-8")
public class InitDataConfig {
	 
	/*@Value(value = "${iot.threeRd.initdata.threeRd}")
	private String threeRd;*/
	
	@Autowired
   	private  ObjectService objectService;
	
	@Autowired
   	private  SwiftlinkService swiftlinkService;
	 
	@Bean
	public String initDevData(){
		//IotInfoConstant.initData(objectService);
		//人脸识别-614型号传感器类型的设备,数据初始化，并上报
		IotInfoConstantSwiftlink.initData(objectService);
		swiftlinkService.parseDeviceInfo();
		return new String();
	}
	

}
