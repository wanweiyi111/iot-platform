package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;

/**
 * 设备数据接入-服务消息
 */
public class ServiceDataVO extends DataVO  implements Serializable {
  
	private static final long serialVersionUID = -1777834634257025224L;
	private String id; // 服务ID
	private String status; // 状态   online /offline
	
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
