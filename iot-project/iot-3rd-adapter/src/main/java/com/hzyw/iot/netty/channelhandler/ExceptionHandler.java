package com.hzyw.iot.netty.channelhandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常控制器
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-11-15 09:26
 */
@Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(">>>异常控制器Error: channelid="+ctx.channel().id(), cause);
        ctx.channel().close();
        ctx.fireExceptionCaught(cause);
    }
}