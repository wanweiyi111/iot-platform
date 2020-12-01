package com.hzyw.iot.test;



import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import com.hzyw.iot.test.server.NettyUdpServer;


@org.springframework.boot.autoconfigure.SpringBootApplication//@EnableAutoConfiguration @ComponentScan
public class SpringBootApplication implements CommandLineRunner{
	public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
 
    @Autowired
    NettyUdpServer nettyTcpServer;
    @Autowired
    NettyUdpClient nettyTcpClient;
    @Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
 
    @Override
    public void run(String... args) throws Exception {
        //启动服务端
        ChannelFuture start = nettyTcpServer.start();
        //启动客户端，发送数据
        nettyTcpClient.connect();
        String s = "客户端send";
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8), SocketUtils.socketAddress(HOST, PORT));
        nettyTcpClient.sendMsg(datagramPacket);
 
        //服务端管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
        start.channel().closeFuture().syncUninterruptibly();
    }
}
