package com.hzyw.iot.mqttTest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class TestMqttConnection {
	private static final Logger LOG = LogManager.getLogger(TestMqttConnection.class);
	/*public synchronized boolean connect() {
        try {
            if(null == client) {
                //host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，
                // MemoryPersistence设置clientid的保存形式，默认为以内存保存
                client = new MqttClient(host, client_id, new MemoryPersistence());
                //设置回调
                client.setCallback(new TestPushCallBack(TestMqttConnection.this, devDao));
            }
            //获取连接配置
            getOption();
            client.connect(option);
            LOG.info("[MQTT] connect to Mqtt Server success...");
            return isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/
	
	private void getOption() {
		MqttConnectOptions option = new MqttConnectOptions();
        //设置是否清空session,false表示服务器会保留客户端的连接记录，true表示每次连接到服务器都以新的身份连接
        option.setCleanSession(true);
        //设置连接的用户名
        option.setUserName("wanweiyi");
        //设置连接的密码
        option.setPassword("hello".toCharArray());
        //设置超时时间 单位为秒
        option.setConnectionTimeout(10);
        //设置会话心跳时间 单位为秒 服务器会每隔(1.5*keepTime)秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        option.setKeepAliveInterval(20);
        option.setAutomaticReconnect(true);
        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//            option.setWill(topic, "close".getBytes(), 2, true);
    }
}
