package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 启动类
 */
@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
public class Iot3rdApplication {
    public static void main(String[] args) {
		SpringApplication.run(Iot3rdApplication.class, args);
    }
    
}
