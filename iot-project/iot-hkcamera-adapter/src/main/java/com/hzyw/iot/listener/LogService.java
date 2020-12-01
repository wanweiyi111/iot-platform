package com.hzyw.iot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.hzyw.iot.sdk.InitSdk;
import com.hzyw.iot.service.RedisService;

@Component
public class LogService implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);
    @Autowired
	private RedisService redisService;
    
    @Override
    public void run(String... strings) throws Exception {
    	//InitSdk.init();
    	redisService.set("testa", "ssssssss");
    	String a  =redisService.get("testa");
    	System.out.println("-------p -" + a);
    }
}
