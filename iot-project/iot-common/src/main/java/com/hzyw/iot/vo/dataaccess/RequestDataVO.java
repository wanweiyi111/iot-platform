package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 设备数据接入-下发消息
 */
@SuppressWarnings("rawtypes")
public class RequestDataVO  extends DataVO implements Serializable {
  
	private static final long serialVersionUID = -1080455511252050149L;
	private String id; // 设备ID
	private List<Map> methods; // 操作方法 
	public List<Map> getMethods() {
		return methods;
	}
	public void setMethods(List<Map> methods) {
		this.methods = methods;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	 
 

}
