package com.hzyw.iot.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class TimeClientHandler extends ChannelHandlerAdapter {
    private byte[] req;
    public TimeClientHandler(){
        try {
            //String aa=ProtocalAdapter.messageRequest(null,"70H","03");
            //aa="68"+aa+"16";
            req="68 00 00 00 00 00 01 68 00 02 70 03 46 16".getBytes();
            //req=aa.getBytes();//ConverUtils.hexStrToByteArr(aa);
        } catch (Exception e) {
            e.printStackTrace();
        }
       // req="$tmb00035ET3318/08/22 11:5704026.75,027.31,20.00,20.00$".getBytes();
    }
    //@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message=null;
        for(int i=0;i<10;i++){
            message=Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }
    //@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(CharsetUtil.UTF_8));
        }  finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常就关闭
        cause.printStackTrace();
        ctx.close();
    }

}
