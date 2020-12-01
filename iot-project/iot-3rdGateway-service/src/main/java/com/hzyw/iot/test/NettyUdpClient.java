package com.hzyw.iot.test;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyUdpClient {
	private static final Logger log = LoggerFactory.getLogger(NettyUdpClient.class);
	 
    @Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
 
    @Autowired
    ClientChannelInitializer clientChannelInitializer;
 
    //与服务端建立连接后得到的通道对象
    private Channel channel;
 
    /**
     * 初始化 `Bootstrap` 客户端引导程序
     *
     * @return
     */
    private final Bootstrap getBootstrap() {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioDatagramChannel.class)//数据包通道，udp通道类型
                .handler(clientChannelInitializer)//通道处理者
                .option(ChannelOption.SO_BROADCAST, true);//开启广播
        return b;
    }
 
    /**
     * 建立连接，获取连接通道对象
     *
     * @return
     */
    public void connect() {
        ChannelFuture channelFuture = getBootstrap().connect(HOST, PORT).syncUninterruptibly();
        if (channelFuture != null && channelFuture.isSuccess()) {
            log.warn("udp client connect host = {}, port = {} success", HOST, PORT);
            channel = channelFuture.channel();
        } else {
            log.error("udp client connect host = {}, port = {} failed!", HOST, PORT);
        }
    }
 
    /**
     * 向服务器发送消息
     *
     * @param msg
     * @throws Exception
     */
    public void sendMsg(Object msg) {
        if (channel != null) {
            channel.writeAndFlush(msg);
        } else {
            log.warn("消息发送失败,连接尚未建立!");
        }
    }

}
