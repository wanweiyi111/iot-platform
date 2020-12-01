package com.hzyw.iot.netty.udpClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hzyw.iot.netty.udpClient.handler.UdpActiveHandler;
import com.hzyw.iot.netty.udpClient.handler.UdpReadHandler;
import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.json.JSONObject;
 

@Component
@ChannelHandler.Sharable
@Slf4j
public class ClientChannelHandler extends SimpleChannelInboundHandler<DatagramPacket>{
	@Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
 
    private final UdpActiveHandler udpActiveHandler = new UdpActiveHandler();//处理service接收的数据
    private final UdpReadHandler udpReadHandler = new UdpReadHandler();//处理service接收的数据
    /**
     * 通道建立成功后
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当channel就绪后
        log.info("[UDP] client channel is ready!");
        udpActiveHandler.activeHandler(ctx);
        
        GlobalInfo.CHANNEL_INFO_MAP.put("etc", ctx);//上下文绑定
        /*String s = "客户端连接成功后发送的第一条消息！";
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8), SocketUtils.socketAddress(HOST, PORT));
        ctx.channel().writeAndFlush(datagramPacket);*/
    }
 
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
    	System.out.println("==============channel--read==============");
    	String msg = datagramPacket.content().toString(CharsetUtil.UTF_8);//字符串接收
        //System.out.println("[UDP] client 收到的消息：" + msg);
        udpReadHandler.readHandler(ctx,msg);
        
        
        //GlobalInfo.CHANNEL_INFO_MAP.put("etc", ctx.channel());
    }

}
