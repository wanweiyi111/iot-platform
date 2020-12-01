package com.hzyw.iot.mqttTest;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MqttCallbackImpl implements MqttCallback{
	 public void connectionLost(Throwable cause) {
         System.out.println("connectionLost");
     }

     public void messageArrived(String topic, MqttMessage message) {
         System.out.println("topic:"+topic);//订阅的主题(厂商/设备唯一标识)
         System.out.println("Qos:"+message.getQos());
         System.out.println("接收，message content:"+new String(message.getPayload()));//接收的byte数据
         
         
         /*Format format = new Format();
         format.configure(topic,new String(message.getPayload()))*/;
     }

     public void deliveryComplete(IMqttDeliveryToken token) {
         System.out.println("deliveryComplete---------"+ token.isComplete());
     }

 }	
	
