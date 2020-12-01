package com.hzyw.iot.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.util.constant.ProtocalAdapter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import redis.clients.jedis.Jedis;

/**
 * 测试
 * @author Administrator
 *
 */
public class TestRequest_storm {
		public static void main_onof_one_light() {
			Properties props = new Properties();
	        //props.put("bootstrap.servers", "47.106.189.255:9092");//47.106.189.255:9092
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
	        //inMap.put("program", "pictrue_and_multy_text.vsn");
	        inMap.put("level", 80);
	        inMap.put("ab", "03");
	        inMap.put("code", "03");
	        inMap.put("onoff", 1);
	        inList.add(inMap);

			Map<String,Object> tags =new HashMap<String,Object>();
	        tags.put("agreement", "plc");
	        
	        List listMethods = new ArrayList();
	        Map<String,Object> methods =new HashMap<String,Object>();
	        methods.put("method", "set_onoff");//set_onoff  set_brightness
	        methods.put("in", inList);
	        listMethods.add(methods);
			//setListMap.put("id", "1010-71d8537795b8f25e-3001-ffff-812d");  //00000200053A
			//setListMap.put("id", "1010-dc8b3f4d54a1bdeb-3001-ffff-2dbe");  //0000020004EE
			setListMap.put("id", "1010-c06f1c384d8175c1-3001-ffff-c827");  //000002000533
			//setListMap.put("id", "1010-341bfdc842f3af1f-3001-ffff-9bad");  //0000020004E8
			//setListMap.put("id", "1010-9d8f96e03af5c4a0-3001-ffff-08f4");  //00000200053C
			//setListMap.put("id", "1010-6b5ee9d1976d5687-3001-ffff-82ca");  //00000200053E
	        setListMap.put("methods", listMethods);
	        setListMap.put("tags", tags);
	        
			try {
				//测试相同设备+指令下发
				long start = System.currentTimeMillis();
				for(int testNum=0;testNum<50000;testNum++){
					Map<String,Object> mapVo =new HashMap<String,Object>();
					mapVo.put("next", testNum);
			        mapVo.put("type", "request");
			        mapVo.put("timestamp",1566205651);
			        mapVo.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954af97");
			        mapVo.put("gwId", "2000-d8b38d288d431464-3001-ffff-c8f0");//000000000100-1010-d8b38d288d431464-3001-ffff-36cf
			        mapVo.put("data", setListMap);
			        ProtocalAdapter protocalAdapter = new ProtocalAdapter();
			        JSONObject jsonObject = JSONUtil.parseObj(mapVo);
			        System.out.println(jsonObject);
					producer.send(new ProducerRecord<>("iot_topic_dataAcess_request", jsonObject.toString()));
				}
				long end = System.currentTimeMillis();
				System.out.println("==========总耗时============ts/"+(end-start));
			    producer.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
		}
		public static void main(String[] args) {
			//main_onof_one_light();//测试单个灯开关 ,也可以测试同时对单个灯发风暴指令
			main_batch_onoff_light();//测试同时开多个灯
		}
		
		public static void main_batch_onoff_light() {
			Properties props = new Properties();
	        //props.put("bootstrap.servers", "47.106.189.255:9092");//47.106.189.255:9092
	        props.put("bootstrap.servers", "192.168.3.183:9901,192.168.3.183:9902,192.168.3.183:9903");//47.106.189.255:9092
	        props.put("acks", "all");
	        props.put("retries", 0);
	        props.put("batch.size", 16384);
	        props.put("linger.ms", 1);
	        props.put("buffer.memory", 33554432);
	        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        Producer<String, String> producer = new KafkaProducer<>(props);
			try {
				//测试相同设备+指令下发
				long start = System.currentTimeMillis();
				//setListMap.put("id", "1010-71d8537795b8f25e-3001-ffff-812d");  //00000200053A
				//setListMap.put("id", "1010-dc8b3f4d54a1bdeb-3001-ffff-2dbe");  //0000020004EE
				//setListMap.put("id", "1010-c06f1c384d8175c1-3001-ffff-c827");  //000002000533
				//setListMap.put("id", "1010-341bfdc842f3af1f-3001-ffff-9bad");  //0000020004E8
				//setListMap.put("id", "1010-9d8f96e03af5c4a0-3001-ffff-08f4");  //00000200053C
				//setListMap.put("id", "1010-6b5ee9d1976d5687-3001-ffff-82ca");  //00000200053E
				/*String[] lightids = {"1010-71d8537795b8f25e-3001-ffff-812d"
						,"1010-dc8b3f4d54a1bdeb-3001-ffff-2dbe"
						,"1010-c06f1c384d8175c1-3001-ffff-c827"
						,"1010-341bfdc842f3af1f-3001-ffff-9bad"
						,"1010-9d8f96e03af5c4a0-3001-ffff-08f4"
						,"1010-6b5ee9d1976d5687-3001-ffff-82ca"};*/
				String[] lightids = {"1010-71d8537795b8f25e-3001-ffff-812d"};
				for(String lightid : lightids){
					Map<String,Object> setListMap =new HashMap<String,Object>();
			        List<Map> inList = new ArrayList<Map>();
			        Map<String,Object> inMap =new HashMap<String,Object>();
			        //inMap.put("program", "pictrue_and_multy_text.vsn");
			        inMap.put("level", 100);
			        inMap.put("ab", "03");//03H AB灯
			        inMap.put("code", "01");//01H
			        inMap.put("onoff", 0);
			        
			        /*inMap.put("plc_node_a_brightness", 80);
			        inMap.put("plc_node_type", "03");//03H
			        inMap.put("plc_node_cCode", "01");//01H
			        inMap.put("plc_node_a_onoff", 0);
			        */

			        inList.add(inMap);
					Map<String,Object> tags =new HashMap<String,Object>();
			        tags.put("agreement", "plc");
			        
			        List listMethods = new ArrayList();
			        Map<String,Object> methods =new HashMap<String,Object>();
			        methods.put("method", "set_brightness");//set_onoff  set_brightness
			        methods.put("in", inList);
			        listMethods.add(methods);
					//setListMap.put("id", "1010-71d8537795b8f25e-3001-ffff-812d");  //00000200053A
					//setListMap.put("id", "1010-dc8b3f4d54a1bdeb-3001-ffff-2dbe");  //0000020004EE
					setListMap.put("id", lightid);  //000002000533
					//setListMap.put("id", "1010-341bfdc842f3af1f-3001-ffff-9bad");  //0000020004E8
					//setListMap.put("id", "1010-9d8f96e03af5c4a0-3001-ffff-08f4");  //00000200053C
					//setListMap.put("id", "1010-6b5ee9d1976d5687-3001-ffff-82ca");  //00000200053E
			        setListMap.put("methods", listMethods);
			        setListMap.put("tags", tags);
					
					
					Map<String,Object> mapVo =new HashMap<String,Object>();
			        mapVo.put("type", "request");
			        mapVo.put("timestamp",1566205651);
			        mapVo.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954af97");
			        mapVo.put("gwId", "2000-d8b38d288d431464-3001-ffff-c8f0");//000000000100-1010-d8b38d288d431464-3001-ffff-36cf
			        mapVo.put("data", setListMap);
			        ProtocalAdapter protocalAdapter = new ProtocalAdapter();
			        JSONObject jsonObject = JSONUtil.parseObj(mapVo);
			        System.out.println(jsonObject);
			        Thread.currentThread().sleep(200);
					//producer.send(new ProducerRecord<>("iot_topic_plcOrder_storm", jsonObject.toString()));
					producer.send(new ProducerRecord<>("iot_topic_dataAcess_request", jsonObject.toString()));
				}
				long end = System.currentTimeMillis();
				System.out.println("==========总耗时============ts/"+(end-start));
			    producer.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
		}
		
		
}
