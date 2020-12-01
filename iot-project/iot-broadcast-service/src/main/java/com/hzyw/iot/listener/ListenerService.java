package com.hzyw.iot.listener;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.hzyw.iot.service.AudioService;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.ReportService;
import com.hzyw.iot.util.IotInfoConstant;

import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
 
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;



 
/**
 * 
 * 监听服务
 * 
 * @author Administrator
 *
 */
@Component
public class ListenerService implements CommandLineRunner {
    //public static final TimedCache<String, String> timedCache1 = CacheUtil.newTimedCache(10000);//创建缓存，默认10秒过期(保证数据不完整后关闭连接)
    
	/*@Autowired
    private AudioService  audioService;*/
    @Autowired
    private ReportService reportService;
    
    @Autowired
	private ObjectService objectService;
    
    @Autowired
    private AudioService audioService;
    /**
     * 启动线程
     */
    @Override
    public void run(String... args) throws Exception {
    	System.out.println("测试开始");
    	timingTasks();
    	kafka();
    }
    //初始化数据
    @Bean
	public String initDevData(){
    	IotInfoConstant.initData(objectService);
    	reportService.AudioDeviceInfo();//初始化上报
		return new String();
	}
    
    public void timingTasks() {
    	try {
            //schedule方法：定时任务三个参数：执行任务，起始时间，时间间隔
    		Timer timer = new Timer("任务调度器");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("执行调度。。。1");
                    try {
                    	reportService.AudioMetricInfo();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }, new Date(System.currentTimeMillis()), 10000);
            
           /* timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("执行调度。。。2");
                    try {
						heartbeat();//心跳
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }, new Date(System.currentTimeMillis()), 3000);*/
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void kafka() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.3.183:9901,192.168.3.183:9902,192.168.3.183:9903");
		props.put("group.id", "logGroup");
		props.put("enable.auto.commit", "false");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		Consumer<String, String> consu = new KafkaConsumer<String, String>(props);
		Collection<String> topics = Arrays.asList("mytest");
		// 消费者订阅topic
		consu.subscribe(topics);
		ConsumerRecords<String, String> consumerRecords = null;
		while (true) {
			// 接下来就要从topic中拉去数据
			consumerRecords = consu.poll(100);
			// 遍历每一条记录
			for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
				try {
				/*long offset = consumerRecord.offset();
				int partition = consumerRecord.partition();
				Object key = consumerRecord.key();*/				
				Object value = consumerRecord.value();
				//System.out.println("收到的kafka数据:"+value);
				
				
				JSONObject json = new JSONObject(value);
				//Map<String, Object> data = new HashMap<String, Object>();
				JSONObject jsondata = new JSONObject(json.get("data"));
				
				JSONObject jsonIn = new JSONObject(((List) jsondata.get("methods")).get(0));
				JSONObject jsonOnoff = new JSONObject(((List)jsonIn.get("in")).get(0));
				//System.out.println("播放方式:"+jsonOnoff.get("onoff"));// 0关，1播放文件，2播放文字
				
				if(jsonIn.get("method").equals("FileSessionSetStat")&&(int)jsonOnoff.get("onoff")==1) {
				System.out.println("进入文件播放");
				JSONObject jsonSetProg = new JSONObject();
				jsonSetProg.put("uuid", jsondata.get("id"));
				jsonSetProg.put("file", jsonOnoff.get("file"));
				audioService.FileSessionSetProg(jsonSetProg);
				//返回response
				continue;
				}
				
				if(jsonIn.get("method").equals("TextPlay")&&(int)jsonOnoff.get("onoff")==1) {
					System.out.println("进入文本播放");
					JSONObject jsonSetProg = new JSONObject();
					jsonSetProg.put("uuid", jsondata.get("id"));
					jsonSetProg.put("text", jsonOnoff.get("text"));
					audioService.TextPlay(jsonSetProg);
					//返回response
					continue;
					}
				if(jsonIn.get("method").equals("TermVolSet")) {
					System.out.println("进入调节音量");
					JSONObject jsonvolume = new JSONObject();
					jsonvolume.put("uuid", jsondata.get("id"));
					jsonvolume.put("volume", jsonOnoff.get("volume"));
					audioService.TermVolSet(jsonvolume);
					//返回response
					continue;
				}
				
				if((int)jsonOnoff.get("onoff")==0) {
					System.out.println("进入关闭广播");
					audioService.FileSessionDestory(jsondata.get("id").toString());
					//返回response
					continue;
				}
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}

	}

}