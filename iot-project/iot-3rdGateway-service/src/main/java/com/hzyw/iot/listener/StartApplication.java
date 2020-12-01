package com.hzyw.iot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hzyw.iot.netty.service.HelloServer;
import com.hzyw.iot.service.GateWayService;

@Component
@Order(value = 1)
public class StartApplication implements ApplicationRunner {
 
	@Autowired
	private GateWayService gateWayService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		//监听mqtt
		//gateWayService.dataAccess();
        try {
        	HelloServer server = new HelloServer();
            server.start(9151);//监听端口
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
