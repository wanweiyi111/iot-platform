package com.hzyw.iot.netty.udpClient;



import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.json.JSONObject;


//@org.springframework.boot.autoconfigure.SpringBootApplication//@EnableAutoConfiguration @ComponentScan
public class SpringBootApplication implements CommandLineRunner{
	/*public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }*/
 
    @Autowired
    NettyUdpClient nettyTcpClient;
    @Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
 
    @Override
    public void run(String... args) throws Exception {
        //启动客户端，发送数据
        //nettyTcpClient.connect();
    }
}
