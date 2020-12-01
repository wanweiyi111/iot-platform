package com.hzyw.iot.netty.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
 
public class EtcClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(EtcClient.class);
    public void connect(String host, int port) throws Exception {
 
        EventLoopGroup workerGroup = new NioEventLoopGroup();
 
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	//byte[] ffff = {(byte) 65535};//ffff字节
                	//设置特殊分隔符 （这样就可以判断每次请求什么时候结束）
    				//ByteBuf buf = Unpooled.copiedBuffer(ffff);
                    ch.pipeline().addLast(
                    		
                            //new IntegerToByteEncoder(),
                    		//new ByteToIntegerDecoder(),
                    		//new MyServerInitializer(),//读写心跳设置
                    		//new FixedLengthFrameDecoder(5)//设置定长字符串接收
                    		//new DelimiterBasedFrameDecoder(1024, buf),
                    		new LengthFieldBasedFrameDecoder(1024, 4, 4, 2, 0),//https://blog.csdn.net/u014801432/article/details/81909902
                            new EtcClientIntHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        }catch(Exception e){
        	LOGGER.error("未连接到服务器------------------",e);
    	} finally {
            workerGroup.shutdownGracefully();
        }
    }
 
    public static void main(String[] args) throws Exception {
        EtcClient client = new EtcClient();
         client.connect("127.0.0.1", 9527);
         // client.connect("192.168.3.191", 9527);
    }
}