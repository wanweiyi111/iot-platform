package com.hzyw.iot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.hzyw.iot.service.GateWayService;

@Component
@Order(value = 1)
public class StartApplication implements ApplicationRunner {
 
	@Autowired
	private GateWayService gateWayService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
	   
		//1,平台上线  ,调用MQTT ServicePubHandler.send(topic)
		//--更新平台上线状态(redis)，同时发送平台上线遗愿 (应该启一个遗愿主题的订阅处理单独进程，平台下线的时候由遗愿进程设置下线标记和所有设备下线标记)
		// 主题4，5   /hzyw/001/iotpub/serviceid  (/hzyw/001/iotpub/serviceid/status)
		// 同步执行过程
		//gateWayService.serviceOnline();
		
		//2，公共上报管道 ，订阅MQTT,设备端所有的发布消息即将通过此管道进入，根据消息类型来做分发处理，调用KAFKA producerHandler.send(topic)
		// 主题2，3  /hzyw/001/ns1/serviceid/iotpub (/hzyw/001/ns1/serviceid/iotpub/status)
		// 异步执行过程
		gateWayService.dataAccess(gateWayService);
		 
		//3，公共下发管道，消费KAFKA，获取需要消费的消息，根据消息类型处理，调用MQTT CommPubHandler.pub(topic)
		// 主题1  /hzyw/001/ns1/serviceid/iotpub/msg
		// 异步执行过程
		gateWayService.dataSendDown();
	}

}
