package com.hzyw.iot.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hzyw.iot.util.constant.ProtocalAdapter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;

/**
 * 删除节点(99H) 删除集中器中的节点
 */
public class TestRequest_99H {
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
			inMap.put("code", "01H"); //控制码   01H: 删除节点的ID;  02H: 删除一组节点; 03H: 删除全部节点
			inList.add(inMap);

			Map<String,Object> tags =new HashMap<String,Object>();
	        tags.put("agreement", "plc");
	        
	        List listMethods = new ArrayList();
	        Map<String,Object> methods =new HashMap<String,Object>();
	        methods.put("method", "del_node"); //指令码 删除节点(99H)
	        methods.put("in", inList);
	        listMethods.add(methods);
			setListMap.put("id", "1010-dc8b3f4d54a1bdeb-3001-ffff-2dbe");  //组号(0~255) 或节点ID 或 广播(空)
	        setListMap.put("methods", listMethods);
	        
	        setListMap.put("tags", tags);
	        
	        Map<String,Object> mapVo =new HashMap<String,Object>();
	        mapVo.put("type", "request");
	        mapVo.put("timestamp",System.currentTimeMillis());
	        mapVo.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954a666");
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
