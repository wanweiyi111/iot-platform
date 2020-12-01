package com.hzyw.iot.vo.dc;

import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

/**
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-25 16:10
 */
/**
 * @author Administrator
 *
 */
public class RTUChannelInfo {
    private ChannelId channelId;
    private String sn;  //plc_sn  设备地址  
    //private IotInfo iotInfo;
    //Map<String, String> devInfo;
    Map<String, Map<String,Object>> devInfo;
    private Channel channel;
    private ChannelHandlerContext ctx; //通过HTTP REST访问时候用此

    public static RTUChannelInfo build(String sn, ChannelId channelId) {
        return new RTUChannelInfo(sn, channelId);
    }

    private RTUChannelInfo(String sn, ChannelId channelId) {
        this.channelId = channelId;
        this.sn = sn;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public RTUChannelInfo setChannelId(ChannelId channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public RTUChannelInfo setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public RTUChannelInfo setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

	public Map<String, Map<String,Object>> getDevInfo() {
		return devInfo;
	}

	public void setDevInfo(Map<String, Map<String,Object>> devInfo) {
		this.devInfo = devInfo;
	}
	 
	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
    public String toString() {
        return "RTUChannelInfo{" +
                "channelId=" + channelId +
                ", sn='" + sn + '\'' +
                //", iotInfo=" + devInfo +
                ", channel=" + channel +
                '}';
    }
}
