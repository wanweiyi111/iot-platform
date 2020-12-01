package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IotDeviceServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(IotDeviceServiceApp.class, args);
	}
}
