package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
public class IotPlcAdapterApplication {
    public static void main(String[] args) {
		SpringApplication.run(IotPlcAdapterApplication.class, args);
    }
    
}
