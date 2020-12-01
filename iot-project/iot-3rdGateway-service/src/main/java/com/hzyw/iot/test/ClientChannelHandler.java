package com.hzyw.iot.test;

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
 

@Component
@ChannelHandler.Sharable
@Slf4j
public class ClientChannelHandler extends SimpleChannelInboundHandler<DatagramPacket>{
	@Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
 
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
        String s = "客户端连接成功后发送的第一条消息！";
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(s, CharsetUtil.UTF_8), SocketUtils.socketAddress(HOST, PORT));
        ctx.channel().writeAndFlush(datagramPacket);
    }
 
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.out.println("[UDP] client 收到的消息：" + datagramPacket.content().toString(CharsetUtil.UTF_8));
    }

}
