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
import com.hzyw.iot.vo.dataaccess.RequestDataVO;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class KafkaProducerExampleByzhu {
	//盒子
	 public static void main(String[] args) {
	        Properties props = new Properties();
	        props.put("bootstrap.servers", "192.168.3.183:9901,192.168.3.183:9902,192.168.3.183:9903");
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
	        //inMap.put("src", "lan");
	        //plc_node_a_onoff
	        //plc_node_a_brightness
	        //plc_node_cCode
	        //plc_node_type

	        //test关灯
	        inMap.put("plc_node_a_onoff", 0); //调光值
	        inMap.put("plc_node_a_brightness", 50);
	        inMap.put("plc_node_cCode", "03");  //01：点控制 ，02：组控制。03：广播控制，   单灯控制给01
	        inMap.put("plc_node_type", "03");   //01：控制A灯， 02：控制B灯  ,  XX:其他灯，单灯控制给值01
	        
	        //test开灯
	       // inMap.put("plc_node_a_onoff", 1); //调光值
	      //  inMap.put("plc_node_a_brightness", 50);
	      //  inMap.put("plc_node_cCode", "03");  //01：点控制 ，02：组控制。03：广播控制，   单灯控制给01
	      //  inMap.put("plc_node_type", "03");   //01：控制A灯， 02：控制B灯  ,  XX:其他灯，单灯控制给值01
	        
	        inList.add(inMap);
	        //test结果： cCode的时候  根据节点ID来调灯是不成功的，通过广播的方式是可以的 
	         
	        Map<String,Object> tags =new HashMap<String,Object>();
	        tags.put("agreement", "plc");
	        
	        List listMethods = new ArrayList();
	        Map<String,Object> methods =new HashMap<String,Object>();
	        methods.put("method", "set_onoff");
	        methods.put("in", inList);
	        listMethods.add(methods);
	        setListMap.put("id", "test"); //plc_node_ID
	        setListMap.put("methods", listMethods);
	        
	        List list = new ArrayList();
	        setListMap.put("attributer", list);
	        setListMap.put("definedAttributer", list);
	        setListMap.put("definedMethod", list);
	        setListMap.put("tags", tags);
	        
	        Map<String,Object> map =new HashMap<String,Object>();
	        map.put("type", "request");
	        map.put("timestamp",1566205651);
	        map.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954af97");
	        map.put("gwId", "1000-f82d132f9bb018ca-2001-ffff-d28a"); //PLC_id
	        map.put("data", setListMap);
	        
	        JSONObject jsonObject = JSONUtil.parseObj(map);
	        System.out.println(jsonObject.toString());
	        producer.send(new ProducerRecord<>("test", jsonObject.toString()));

	        producer.close();
	    }
	
	
	
	 //PLC
	/* public static void main(String[] args) throws Exception {
	        Properties props = new Properties();
	        props.put("bootstrap.servers", "47.106.189.255:9092");
	        props.put("acks", "all");
	        props.put("retries", 0);
	        props.put("batch.size", 16384);
	        props.put("linger.ms", 1);
	        props.put("buffer.memory", 33554432);
	        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

	        Producer<String, String> producer = new KafkaProducer<>(props);
	        
	        List<String[]> pdt =new ArrayList<String[]>();
			pdt.add(new String[]{"","03H","C8H"});
	       
	        
	        Map<String,Object> setListMap =new HashMap<String,Object>();
	        List<Map> inList = new ArrayList<Map>();
	        Map<String,Object> inMap =new HashMap<String,Object>();
	        inMap.put("pdt", pdt);
	        inMap.put("code", "03H");//控制码
	        inList.add(inMap);
	        
	        
	        
	       Map<String,Object> tags =new HashMap<String,Object>();
	       tags.put("agreement", "plc");
	        
	        List listMethods = new ArrayList();
	        Map<String,Object> methods =new HashMap<String,Object>();
	        methods.put("method", "42H");//命令码
	        methods.put("in", inList);
	        listMethods.add(methods);
	        setListMap.put("id", "");
	        setListMap.put("methods", listMethods);
	        
	        setListMap.put("tags", tags);
	        
	        Map<String,Object> map =new HashMap<String,Object>();
	        map.put("type", "request");
	        map.put("timestamp",1566205651);
	        map.put("msgId", "1db179ce-c81e-4499-bff2-29e8a954af97");
	        map.put("gwId", "000000000100");
	        map.put("data", setListMap);
	        
	       
	        JSONObject jsonObject = JSONUtil.parseObj(map);
	        System.out.println(jsonObject.toString());
	        
	        System.out.println("json数据:"+JSON.parseObject(JSON.toJSONString(map)));
	        ProtocalAdapter protocalAdapter = new ProtocalAdapter();
	        String test = protocalAdapter.messageRequest(JSON.parseObject(JSON.toJSONString(map)));
	        System.out.println("返回数据:"+test);
	        
	       // CommandHandler.writeCommand("000000000100",test,1);
	        
	        ProtocalAdapter protocalAdapter = new ProtocalAdapter();
	        String test = protocalAdapter.testRequestCode("02H","42H");
	        System.out.println("下发的指令:"+test);
	        
	        
	        //全流程
	        producer.send(new ProducerRecord<>("iot_topic_dataAcess_request",JSON.parseObject(JSON.toJSONString(map)).toString()));
	        producer.close();
	    }*/
	 
}
