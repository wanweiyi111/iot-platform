package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
public class IotBroadcast {
    public static void main(String[] args) throws Exception {
		SpringApplication.run(IotBroadcast.class, args);
    }
    
}
