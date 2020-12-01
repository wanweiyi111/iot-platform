package com.hzyw.iot.netty.client;
import org.springframework.stereotype.Component;

import com.hzyw.iot.vo.dc.RTUInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
 
 
//InboundHandler类型
public class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
	boolean flag = false;
	//登记
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--register==============");
    }
 
    //注册
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--unregistered==============");
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
        
        String str = "wan";
        byte[] bytes = str.getBytes();
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(bytes);
        ctx.writeAndFlush(byteBuf);
        //ctx.channel().writeAndFlush(byteBuf);
        //ctx.writeAndFlush(byteBuf).sync();
    }
 
    // 接收server端的消息，并打印出来
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf buf = (ByteBuf) msg; 
    	byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req);
    	System.out.println("接收到的数据:"+body);
    }
   
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
