package com.hzyw.iot.netty.server;

import com.hzyw.iot.util.constant.ConverUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelHandlerAdapter {
	
    //@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { 
        try {
            ByteBuf in = (ByteBuf) msg;
            byte[] b_iso88591=null;
            if(in!=null&& in.readableBytes()>0){
                System.out.println("传输内容是");
                System.out.println(in.toString(CharsetUtil.ISO_8859_1));

                b_iso88591= ConverUtil.hexStrToByteArr(in.toString(CharsetUtil.ISO_8859_1));  //16进制转成字节数组

                //ByteBuf resp= Unpooled.copiedBuffer("收到信息$".getBytes());
                String sss="收到信息:"+ ConverUtil.byteArrToBinStr(b_iso88591)+"$";
                ByteBuf resp= Unpooled.copiedBuffer(sss.getBytes());
                //System.out.println("收到信息: "+ConverUtil.byteArrToBinStr(b_iso88591)+"$".getBytes());

                ctx.writeAndFlush(resp);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
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
