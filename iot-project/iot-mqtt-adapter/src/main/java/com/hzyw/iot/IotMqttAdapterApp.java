package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

 
@SpringBootApplication
//@EnableDiscoveryClient
@EnableCaching  //开启缓存
@ComponentScan(basePackages = {"com.hzyw.iot"})
public class IotMqttAdapterApp {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(IotMqttAdapterApp.class);
		//application.addInitializers(new ApplicationStartedListener());
		SpringApplication.run(IotMqttAdapterApp.class, args);
	}
	/*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(appController.class);
    }*/

}
