package com.hzyw.iot.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzyw.iot.util.constant.ProtocalAdapter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;

/**
 * 下发节点(96H) 主机向集中器发下某些节点
 */
public class TestRequest_96H {
		public static void main(String[] args) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "192.168.3.183:9901,192.168.3.183:9902,192.168.3.183:9903");//47.106.189.255:9092
	        props.put("acks", "all");
	        props.put("retries", 0);
	        props.put("batch.size", 16384);
	        props.put("linger.ms", 1);
	        props.put("buffer.memory", 33554432);
	        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        Producer<String, String> producer = new KafkaProducer<>(props);
	        
			Map<String,Object> setListMap =new HashMap<String,Object>();
	        List<Map> inList = new ArrayList<Map>();
	        Map<String,Object> inMap =new HashMap<String,Object>();
			//Map<String,Object> inMap1 =new HashMap<String,Object>();
			//inMap.put("nodeID", "1010-71d8537795b8f25e-3001-ffff-812d"); //3组(00000200053A,000002000533)
			inMap.put("nodeID", "1010-341bfdc842f3af1f-3001-ffff-9bad"); //2组(0000020004E8,00000200053C)

			inMap.put("groupID", "02"); //组号, 00:不分组号，默认发全部节点
			inMap.put("devType", "0aH"); //设备类型
			inMap.put("code", "02H"); //02H:下发一组节点,03H:下发全部节点
			inList.add(inMap);
			inMap =new HashMap<String,Object>();
			//inMap.put("nodeID", "1010-c06f1c384d8175c1-3001-ffff-c827"); //3组 000002000533
			inMap.put("nodeID", "1010-9d8f96e03af5c4a0-3001-ffff-08f4"); //2组 00000200053C
			inMap.put("groupID", "02"); //组号, 00:不分组号，默认发全部节点
			inMap.put("devType", "0aH"); //设备类型
			inMap.put("code", "02H"); //02H:下发一组节点,03H:下发全部节点
			inList.add(inMap);

			Map<String,Object> tags =new HashMap<String,Object>();
	        tags.put("agreement", "plc");
	        
	        List listMethods = new ArrayList();
	        Map<String,Object> methods =new HashMap<String,Object>();
	        methods.put("method", "put_node_list");
	        methods.put("in", inList);
	        listMethods.add(methods);
			setListMap.put("id", "");  //为空
	        setListMap.put("methods", listMethods);
	        
	        setListMap.put("tags", tags);
	        
	        Map<String,Object> mapVo =new HashMap<String,Object>();
	        mapVo.put("type", "request");
	        mapVo.put("timestamp",System.currentTimeMillis());
	        mapVo.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954a999");
	        mapVo.put("gwId", "2000-d8b38d288d431464-3001-ffff-c8f0");
	        mapVo.put("data", setListMap);
	        ProtocalAdapter protocalAdapter = new ProtocalAdapter();
	        JSONObject jsonObject = JSONUtil.parseObj(mapVo);
	        System.out.println(jsonObject);
			try {
				producer.send(new ProducerRecord<>("iot_topic_dataAcess_request", jsonObject.toString()));
				//iot_topic_dataAcess_request_test
			    producer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
		}
}
