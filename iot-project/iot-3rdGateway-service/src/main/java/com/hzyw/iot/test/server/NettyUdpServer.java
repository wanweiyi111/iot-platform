package com.hzyw.iot.test.server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import javax.annotation.PreDestroy;

@Component
public class NettyUdpServer {
	private static final Logger log = LoggerFactory.getLogger(NettyUdpServer.class);
	 
    private static final EventLoopGroup group = new NioEventLoopGroup(1);
 
    @Autowired
    ServerChannelInitializer serverChannelInitializer;
 
    @Value("${port}")
    private int port;
 
    //监听端口的通道，即server的处理通道
    private Channel channel;
 
    /**
     * 开启udp server服务
     *
     * @return
     */
    public ChannelFuture start() {
        //启动类
        Bootstrap serverBootstrap = new Bootstrap();
        serverBootstrap.group(group)//组配置，初始化ServerBootstrap的线程组
                .channel(NioDatagramChannel.class)//数据包通道，udp通道类型
                .option(ChannelOption.SO_BROADCAST, true)//支持广播
                .handler(serverChannelInitializer);//通道处理者
        //Future：异步任务的生命周期，可用来获取任务结果
        ChannelFuture channelFuture1 = serverBootstrap.bind(port).syncUninterruptibly();//绑定端口，开启监听,同步等待
        if (channelFuture1 != null && channelFuture1.isSuccess()) {
            log.info("[UDP] server start success, port = {}", port);
            channel = channelFuture1.channel();//获取通道
            System.out.println("service通道:"+channel);
        } else {
            log.error("udp server start failed!!");
            channelFuture1.cause().printStackTrace();
        }
        return channelFuture1;
    }
 
    /**
     * 停止udp server服务
     * 销毁前的拦截
     */
    @PreDestroy
    public void destroy() {
        try {
            if (channel != null) {
                ChannelFuture await = channel.close().await();
                if (!await.isSuccess()) {
                    log.error("udp channel close fail, {}", await.cause());
                }
            }
            Future<?> future1 = group.shutdownGracefully().await();
            if (!future1.isSuccess()) {
                log.error("udp group shutdown fail, {}", future1.cause());
            }
            log.info("udp shutdown success");
        } catch (InterruptedException e) {
            log.info("udp shutdown fail");
            e.printStackTrace();
        }
    }
}
