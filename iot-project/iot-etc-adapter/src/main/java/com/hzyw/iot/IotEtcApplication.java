package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hzyw.iot.netty.client.EtcClient;
/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
public class IotEtcApplication {
    public static void main(String[] args) throws Exception {
		SpringApplication.run(IotEtcApplication.class, args);
    }
    
}
