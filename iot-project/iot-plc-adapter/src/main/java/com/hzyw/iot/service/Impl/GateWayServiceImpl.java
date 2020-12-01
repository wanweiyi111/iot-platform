package com.hzyw.iot.service.Impl;


import com.hzyw.iot.service.RedisService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.DataSendDownConsumer;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.service.GateWayService;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;
 
/**
 * 网关服务
 *
 */
@Service
public class GateWayServiceImpl implements GateWayService {
	private static final Logger logger = LoggerFactory.getLogger(GateWayServiceImpl.class);
	@Autowired
	private KafkaCommon kafkaCommon; //KAFKA工具类
	
	@Autowired
	private ApplicationConfig applicationConfig;//全局配置

	@Autowired
	private RedisService redisService;
	/* 
	 * 数据下发-消费KAFKA获取下发数据
	 */
	@Override
	public void dataSendDown(RedisService redisService) {
		//从KAFKA获取下发消息 DataSendDownConsumer
		new Thread(new DataSendDownConsumer(kafkaCommon,applicationConfig,this,redisService),"数据下发::消费KAFKA").start();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.GateWayService#getPLCMetricInfo()
	 */
	@Override
	public void getPLCMetricInfo() {
		//PLC类型设备所有节点
		//List<Map<String, String>> nodelist = IotInfoConstant.plc_relation_plcsnToNodelist.get("12345").get("xxx");
		for(String port :IotInfoConstant.plc_relation_plcsnToNodelist.keySet()){
			for(String plc_sn : IotInfoConstant.plc_relation_plcsnToNodelist.get(port).keySet()){
				List<Map<String, String>> nodelist = IotInfoConstant.plc_relation_plcsnToNodelist.get(port).get(plc_sn);
				for (int i = 0; i < nodelist.size(); i++) {
					try{
						if(IotInfoConstant.plc_relation_plcsnToNodelist.get(port) == null) return ;
						String flag = PlcProtocolsUtils.gloable_dev_status.get(plc_sn + "_login"); // 1表示已登陆
						// 0
						// 表示未登陆
						// （通过心跳判断是否掉线来设置）
						if (!"1".equals(flag)) {
							return ;
						} 
						flag = (String)redisService.get("plc_isconfig_"+plc_sn);
						if (!"1".equals(flag)) {
							return ;
						}
							
						Map<String, String> item = nodelist.get(i);
						String node_sn = item.get(IotInfoConstant.dev_plc_node_sn); // 节点SN
						String plc_node_id = (String)IotInfoConstant.allDevInfo.get(port).get(node_sn + "_defAttribute").get(IotInfoConstant.dev_plc_node_id); 
						String plc_id = (String)IotInfoConstant.allDevInfo.get(port).get(plc_sn + "_defAttribute").get(IotInfoConstant.dev_plc_plc_id);
						logger.info("->>>---Scheduled-----/查询节点详情[调度]发起请求../plc_sn/node_sn --," + plc_sn + "/" + node_sn );
						//构造请求的消息体
				        Map<String,String> tags = new HashMap<String,String>();
						tags.put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value); //指定接入类型是PLC接入类型
						List<Map> methods = new ArrayList<Map>();
						Map<String,Object> method = new HashMap<String,Object>();
						
						method.put("method", "sel_detail_node");
						List<Map> inlist = new ArrayList<Map>();
						Map<String,Object> in_1 = new HashMap<String,Object>();
						in_1.put("plc_node_cCode", "01");
						in_1.put("plc_node_id", plc_node_id);
						inlist.add(in_1);
						method.put("in",  inlist);
						methods.add(method);
						
				        RequestDataVO requestDataVO = new RequestDataVO();
						requestDataVO.setMethods(methods);
						requestDataVO.setTags(tags);
						requestDataVO.setId(plc_node_id);
						
				        JSONObject  request = new JSONObject();
				        request.put("msgId", "cmdcode45_"+UUID.randomUUID().toString());
				        request.put("gwId", plc_id);
				        request.put("type", "request");
				        request.put("timestamp", System.currentTimeMillis());
				        request.put("data", requestDataVO );
				        Thread.currentThread().sleep(1000);
						logger.info("->>>---Scheduled-----JSON="+ request.toJSONString());//如果没有上报，拷贝此JSON手动发送看看
						PlcProtocolsBusiness.protocals_process(request.toJSONString(),applicationConfig);
					}catch(Exception e){
						logger.error("ListenerService::GateWayServiceImpl::getPLCMetricInfo",e);
					}
				}
			}
		} 
	}
	 

}
