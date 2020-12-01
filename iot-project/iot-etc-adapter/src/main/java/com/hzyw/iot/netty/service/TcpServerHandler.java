package com.hzyw.iot.netty.service;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;



public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG =  LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.print("TcpServerHandler.................................channelRead0....................");
		ctx.channel().writeAndFlush("yes, server is accepted you ,nice !"+msg);
		
    }		             
		
    //接收client数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf buf = (ByteBuf) msg; 
    	byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req);
    	System.out.println("Server接收到的数据:"+body);
		
    	
    	//发送数据
    	String str = "wan";
        byte[] bytes = str.getBytes();
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(bytes);
        ctx.writeAndFlush(byteBuf);
    }	
 

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
            Throwable cause) throws Exception {
        LOG.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}