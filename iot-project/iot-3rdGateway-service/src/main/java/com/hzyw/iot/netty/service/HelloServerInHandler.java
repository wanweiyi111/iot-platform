package com.hzyw.iot.netty.service;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.controller.OrderController;
import com.hzyw.iot.netty.GlobalInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
 
// 该handler是InboundHandler类型
public class HelloServerInHandler extends ChannelInboundHandlerAdapter {
	 private static final Logger LOGGER = LoggerFactory.getLogger(HelloServerInHandler.class);
	 private int loss_connect_counts = 0;
    @Override
    public boolean isSharable() {
        System.out.println("==============handler-sharable==============");
        return super.isSharable();
    }
    //连接service前
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-register==============");
    }
    //service断开后
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-unregister==============");
        
        //断开连接后删除上下文(后期需要channelID去识别)
        Map ctxTest = GlobalInfo.CHANNEL_INFO_MAP;//获取上下文
        Iterator<String> iter = ctxTest.keySet().iterator();
        while(iter.hasNext()){
               if(iter.next().equals("nettyTest")){
                        iter.remove();
                        System.out.println("删除ctx上下文(此时HTTP下发不会成功)");
               }
        }
        
    }
    // 连接成功后，向client发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-active==============");
 
    }
    //client端重连机制
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-inactive==============");
    }
    // 接收client端的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("==============channel-read==============");
    	ByteBuf buf = (ByteBuf) msg; 
    	byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req);
    	System.out.println("接收到的数据:"+body);
    	
    	GlobalInfo.CHANNEL_INFO_MAP.put("nettyTest", ctx.channel()); //上下文管道保存
    	/*Channel ctxTest = GlobalInfo.CHANNEL_INFO_MAP.get("test");
    	System.out.println(ctxTest);
    	byte[] byteTest ="helloWorld".getBytes();
    	ByteBuf byteBuf= ctxTest.alloc().buffer();
		byteBuf.writeBytes(byteTest);
		ctxTest.writeAndFlush(byteBuf);*/
    	
    }
    
    //心跳机制
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	System.out.println("==============channel--userEventTriggered==============");
    	/*super.userEventTriggered(ctx, evt);  
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {//读操作超时
                	System.out.println("client端断开后进入此方法？");
                	handlerBusiness(ctx);
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {  
            	LOGGER.info("---------心跳----写操作----------" );
            } else if (event.state().equals(IdleState.ALL_IDLE)) {  
            	LOGGER.info("---------心跳----ALL_IDLE操作------" );
            }
        }
    	ctx.channel().close();*/
    }
    
  /*  //设备下线
    private void handlerBusiness(ChannelHandlerContext ctx){
    	if (ctx == null || ctx.channel() == null || GlobalInfo.CHANNEL_INFO_MAP.get(ctx.channel().id()) == null) {
    		LOGGER.error("---------心跳HeartBeatHandler,handlerBusiness处理warning!!! 无法获取到ctx .." );
    		return;
    	}
    	if (GlobalInfo.CHANNEL_INFO_MAP.get(ctx.channel().id()) == null) {
    		LOGGER.error("---------心跳HeartBeatHandler,handlerBusiness处理warning!!! CHANNEL_INFO_MAP中没有发现此连接对应的channel .." );
    		return;
    	}
    	
    }*/
    
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel-read-complete==============");
        ctx.flush();
    }
}