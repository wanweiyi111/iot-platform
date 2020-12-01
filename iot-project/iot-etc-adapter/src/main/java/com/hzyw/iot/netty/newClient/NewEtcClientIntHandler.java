package com.hzyw.iot.netty.newClient;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hzyw.iot.netty.newClient.handler.NewActiveHandler;
import com.hzyw.iot.netty.newClient.handler.NewReadHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
 
 
//InboundHandler类型
@Component
public class NewEtcClientIntHandler extends ChannelInboundHandlerAdapter {
	 private static final Logger LOGGER = LoggerFactory.getLogger(NewEtcClientIntHandler.class);
	 
	 private final NewReadHandler newReadHandler = new NewReadHandler();//处理service接收的数据 (新版)
	 private final NewActiveHandler newActiveHandler = new NewActiveHandler();//处理service接收的数据(新版)
	 
	//连接service前
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==============channel--register==============");
    }
 
    //service断开后
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
        newActiveHandler.activeHandler(ctx);
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
    	newReadHandler.readHandler(ctx,buf);
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
