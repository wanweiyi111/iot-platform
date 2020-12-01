package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
public class IotFFmpegApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(IotFFmpegApplication.class);
		SpringApplication.run(IotFFmpegApplication.class, args);
		
    }
}
