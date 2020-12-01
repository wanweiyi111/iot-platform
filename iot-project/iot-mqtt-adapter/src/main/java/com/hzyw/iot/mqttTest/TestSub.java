package com.hzyw.iot.mqttTest;

import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TestSub {
	/**
	 * 订阅端
	 */

	public static void main(String[] args) throws MqttException {
		String HOST = "tcp://47.106.189.255:1883";
		String TOPIC = "sub";
		int qos = 1;
		String clientid = "subClient123";
		String userName = "test";
		String passWord = "test";
		try {
			test1( HOST, clientid, userName, passWord, TOPIC, qos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static MqttClient client = null;
	static MqttConnectOptions options= null;
	
	public static void test1(String HOST,String clientid,String userName,String passWord,String TOPIC,int qos) throws MqttSecurityException, MqttException{
		// host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
		Random rr = new Random();		
		System.out.println("===============test1===开始初始化订阅...=============");
		//String id = rr.nextLong()+"";
		//System.out.println("===============id=== =" +id);
		 client = new MqttClient(HOST, clientid, new MemoryPersistence());
					// MQTT的连接设置
					 options = new MqttConnectOptions();
					// 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
					options.setCleanSession(true);
					// 设置连接的用户名
					options.setUserName(userName);
					// 设置连接的密码
					options.setPassword(passWord.toCharArray());
					/*// 设置超时时间 单位为秒
					options.setConnectionTimeout(10);
					// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
					options.setKeepAliveInterval(20);*/
					// 设置回调函数
					client.setCallback(new MqttCallback() {
						public void connectionLost(Throwable cause) {
							System.out.println("------------------connectionLost---!!!!!");
							try {
								//test1( HOST, clientid, userName, passWord, TOPIC, qos);
								reConnection();
							}catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						public void messageArrived(String topic, MqttMessage message) throws Exception {
							System.out.println("topic:" + topic);
							System.out.println("Qos:" + message.getQos());
							System.out.println("message content:" + new String(message.getPayload()));

						}

						public void deliveryComplete(IMqttDeliveryToken token) {
							System.out.println("deliveryComplete---------" + token.isComplete());
						}

					});
					client.connect(options);
					// 订阅消息
					client.subscribe(TOPIC, qos);
					System.out.println("订阅中---------"  );
	}
	public static void reConnection(){
		while (true) {
			try {
				if (!client.isConnected()) {
					System.out.println("---断线重连。。");
					client.connect(options);
					client.subscribe("sub", 1);
					System.out.println("-----重新订阅成功!-------");
					break;
				} else {
					client.disconnect(); // 反复尝试去断开现有的连接
					System.out.println("---断线重连-先断开连接 ---------");
				}
			} catch (Exception e) {
				continue;
			}
		}
		System.out.println("---完毕---------");
		 
	}

}
