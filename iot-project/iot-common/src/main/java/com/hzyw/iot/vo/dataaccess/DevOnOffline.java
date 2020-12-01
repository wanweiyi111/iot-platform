package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class DevOnOffline extends DataVO implements Serializable{
	private static final long serialVersionUID = 411173193328785566L;
	private String id; // 设备ID
	private String status; // 在线:online   离线:offline
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
