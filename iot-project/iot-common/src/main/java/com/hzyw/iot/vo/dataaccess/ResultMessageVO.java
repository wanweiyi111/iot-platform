package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;

/**
 * 设备数据接入-上报消息外层结构
 */
public class ResultMessageVO<T> extends MessageVO<T> implements Serializable {
	private static final long serialVersionUID = 2097112223328452858L;

	private int messageCode;
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}
  
}
