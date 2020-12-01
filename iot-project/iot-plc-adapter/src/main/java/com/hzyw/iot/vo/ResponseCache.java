package com.hzyw.iot.vo;

public class ResponseCache {
	private String method;
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
	
	/**
	 * 消息时间
	 */
	private long timestamp;
	 
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ResponseCache(String method,String msgId,String plcid,String nodeId){
		this.method = method;
		this.msgId = msgId;
		this.plcId = plcid;
		this.nodeId = nodeId;
		this.timestamp = System.currentTimeMillis();//毫秒
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
