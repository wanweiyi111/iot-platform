package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 设备数据接入-信号返回
 */
@SuppressWarnings("rawtypes")
public class DevSignlResponseDataVO  extends DataVO  implements Serializable {
	private static final long serialVersionUID = 4111731933287831123L;
	private String id; // 设备ID
	private List<Map> signals; // [消息中断]  还待确定返回的消息结构
	
	public List<Map> getSignals() {
		return signals;
	}
	public void setSignals(List<Map> signals) {
		this.signals = signals;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	 
	 

}
