package com.hzyw.iot.mqttTest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.hzyw.iot.mqttTest.MqttCallbackImpl;
/**
 *订阅端
 */
public class TestConsumers1 {
	
	private MqttClient client;
	private MqttConnectOptions options; 
	//private final static String uri = "tcp://192.168.0.76:1883";
	//private final static String uri = "ssl://127.0.0.1:61617?transportOptions";
	private final static String uri = "tcp://47.106.189.255:1883";
	
	private final static String clientId = "server2";
	private final static String userName = "admin";
	private final static String password = "admin";
	private final static String topic    = "test/pub/willflag";
	//private MqttTopic mTopic;
	//private MqttMessage message;
	private ScheduledExecutorService service;
	
	public TestConsumers1 () throws MqttException {
		client = new MqttClient(uri, clientId, new MemoryPersistence());
	}
	
	//用于断线重连,或者因为client冲突而导致的断线
	public void startReconnect() {
		service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("连接状态:" + client.isConnected());
				System.out.println(options);
				if(!client.isConnected()) {
					System.out.println("与broker的连接断开,开始重新连接.");
					try {
						client.connect(options);
						System.out.println("client:L" + client.isConnected());
					} catch (MqttSecurityException e) {
						e.printStackTrace();
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}
		}
		, 0 * 1000, 10 * 1000 , TimeUnit.MILLISECONDS);
	}
	
	public void subTopic() throws Exception {
		options = new MqttConnectOptions();
		options.setCleanSession(true);
		// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制  
		options.setKeepAliveInterval(20);
		options.setUserName(null);
		options.setPassword(null);
		options.setConnectionTimeout(20);
		options.setKeepAliveInterval(10);
		/*options.setSocketFactory(SslUtil.getSocketFactory("C:/Users/Administrator/Desktop/ssl/client_cert", 
				"C:/Users/Administrator/Desktop/ssl/client1.ks", 
				"C:/Users/Administrator/Desktop/ssl/client1.ts", "inshn"));
		*/
		client.setCallback(new MqttCallbackImpl());
		client.getTopic(topic);
		//options.setWill(mTopic, ("我是客户端 " + client.getClientId() + ",我掉线了").getBytes(), 2, false);
		
		try {
			client.connect(options);
			client.subscribe("test/pub/willflag");
			System.out.println(client.isConnected());
			//client.unsubscribe(topic);
			//mTopic = client.getTopic(topic);
			
			//int[] qos = {1};
			//String[] topics = {"xiaoye"};
			//client.subscribe(topics);
			//System.out.println("服务器连接状态:" + client.isConnected());
			//client.unsubscribe(topics);//取消订阅
			
			//发布消息
			/*mTopic = client.getTopic(topic);
			message = new MqttMessage();
			message.setQos(0);
			message.setRetained(false);
			message.setPayload(("hello word,大家好哇").getBytes());
			//client.publish(topic, message);
			MqttDeliveryToken token = mTopic.publish(message);
			token.waitForCompletion();*/
			
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		TestConsumers1 c1;
		try {
			c1 = new TestConsumers1();
			c1.subTopic();
			c1.startReconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}
