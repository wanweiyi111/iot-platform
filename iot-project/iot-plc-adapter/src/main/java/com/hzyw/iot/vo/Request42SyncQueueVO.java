package com.hzyw.iot.vo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import com.hzyw.iot.vo.dc.ModbusInfo;
import io.netty.channel.Channel;

public class Request42SyncQueueVO {
	public Request42SyncQueueVO(){}
	/**
	 * 消息ID
	 */
	private String msgId;
	/**
	 * plc id
	 */
	private String plcId; 
	/**
	 * plc node id
	 */
	private String nodeId;
	
	private String method;
	
	/**
	 * 消息时间
	 */
	private long timestamp;
	
	private Channel channel;
	private ModbusInfo modbusInfo;
	
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public ModbusInfo getModbusInfo() {
		return modbusInfo;
	}
	public void setModbusInfo(ModbusInfo modbusInfo) {
		this.modbusInfo = modbusInfo;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getPlcId() {
		return plcId;
	}
	public void setPlcId(String plcId) {
		this.plcId = plcId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
 	 
	
}
