package com.hzyw.iot.netty.client;


import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hzyw.iot.netty.GlobalInfo;
import com.hzyw.iot.netty.RTUChannelInfo;
import com.hzyw.iot.netty.client.channelhandler.ChannelActiveHandler;
import com.hzyw.iot.netty.client.channelhandler.ChannelReadHandler;
import com.hzyw.iot.netty.client.channelhandler.newHandler.ActiveHandler;
import com.hzyw.iot.netty.client.channelhandler.newHandler.ReadHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
 
 
//InboundHandler类型
@Component
public class EtcClientIntHandler extends ChannelInboundHandlerAdapter {
	 private static final Logger LOGGER = LoggerFactory.getLogger(EtcClientIntHandler.class);
	// private final ChannelReadHandler channelReadHandler = new ChannelReadHandler();//处理service接收的数据 (旧版)
	// private final ChannelActiveHandler channelActiveHandler = new ChannelActiveHandler();//处理service接收的数据(旧版)
	 
	 
	 private final ReadHandler newReadHandler = new ReadHandler();//处理service接收的数据 (新版)
	 private final ActiveHandler newActiveHandler = new ActiveHandler();//处理service接收的数据(新版)
	 
	//连接service前
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--register==============");
  /*      LOGGER.info("new channel coming! --注册--> {}", ctx.channel());
        ChannelId channelId = ctx.channel().id();
        RTUChannelInfo channelInfo = GlobalInfo.CHANNEL_INFO_MAP.getOrDefault(channelId, RTUChannelInfo.build("unknownSN", channelId));
        GlobalInfo.CHANNEL_INFO_MAP.put(channelId, channelInfo);//设备建立连接的时候，实际上是没有交互过消息的，所以不可能知道当前是什么设备ID建立的连接
        ctx.fireChannelRegistered();
        System.out.println(GlobalInfo.CHANNEL_INFO_MAP.get(channelId));*/
    }
 
    //service断开后
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--unregistered==============");
       /* LOGGER.info("channel out! ---离线拉-> {}", ctx.channel());
        ChannelId channelId = ctx.channel().id();
        InetSocketAddress insocket = (InetSocketAddress)ctx.channel().localAddress();
        RTUChannelInfo channelInfo = GlobalInfo.CHANNEL_INFO_MAP.remove(channelId);
        GlobalInfo.SN_CHANNEL_INFO_MAP.remove(insocket.getPort()+ channelInfo.getSn());
       // PlcProtocolsUtils.setLoginStatus(channelInfo.getSn(), "0"); // 设置设备上线状态 1-上线 0-下线
        LOGGER.info("remove channel: {}", channelInfo);
        ctx.fireChannelUnregistered();*/
    }
    
    //心跳机制
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	System.out.println("==============channel--userEventTriggered==============");
    	if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "超时事件: " + eventType);
            ctx.channel().close();
        }
    }
    
    //重连机制
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--inactive==============");
    }
 
    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--active==============");
        //channelActiveHandler.activeHandler(ctx);//旧版
        newActiveHandler.activeHandler(ctx);//新版
        /*String str = "test";
	    byte[] bytes = str.getBytes();
	    ByteBuf byteBuf = ctx.alloc().buffer();
	    byteBuf.writeBytes(bytes);
	    ctx.writeAndFlush(byteBuf);*/
    }
 
    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("==============channel--read==============");
    	ByteBuf buf = (ByteBuf) msg; 
    	//channelReadHandler.readHandler(ctx,buf);//旧版
    	newReadHandler.readHandler(ctx,buf);//新版
    	/*byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req);
    	System.out.println("接收到的数据:"+body);*/
    }
   
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
