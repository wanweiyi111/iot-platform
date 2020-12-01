package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

 
@SpringBootApplication
//@EnableDiscoveryClient
//@EnableCaching  //开启缓存
@ComponentScan(basePackages = {"com.hzyw.iot"})
public class IotHeartApp {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(IotHeartApp.class);
		SpringApplication.run(IotHeartApp.class, args);
	}
	

}
