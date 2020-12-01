package com.hzyw.iot.test.server;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@ChannelHandler.Sharable
@Slf4j
public class ServerChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket>{
	@Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.out.println("[UDP] server 收到的消息：" + datagramPacket.content().toString(CharsetUtil.UTF_8));
        System.out.println("收到的通道:"+channelHandlerContext.channel());
        String response = "{" + datagramPacket.content().toString(CharsetUtil.UTF_8) + "}的响应，我是服务端啊！！！";
        DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8), datagramPacket.sender());
        channelHandlerContext.channel().writeAndFlush(datagramPacket1);
    }
 
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
}
