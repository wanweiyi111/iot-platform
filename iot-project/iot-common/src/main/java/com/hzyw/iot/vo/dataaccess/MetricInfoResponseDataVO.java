package com.hzyw.iot.vo.dataaccess;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MetricInfoResponseDataVO extends DataVO implements Serializable {
	private static final long serialVersionUID = 4111731933287812345L;
	private String id; // 设备ID
	private List<Map> attributers; // 详细属性
	private List<Map> definedAttributers; // 详细属性
	

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Map> getDefinedAttributers() {
		return definedAttributers;
	}

	public void setDefinedAttributers(List<Map> definedAttributers) {
		this.definedAttributers = definedAttributers;
	}

	public List<Map> getAttributers() {
		return attributers;
	}

	public void setAttributers(List<Map> attributers) {
		this.attributers = attributers;
	}
}
