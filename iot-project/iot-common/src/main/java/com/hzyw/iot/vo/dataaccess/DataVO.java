package com.hzyw.iot.vo.dataaccess;

import java.util.Map;

/**
 * 设备数据接入-base 
 * --tag 标签 对于需要自身定义属性的情况下 可以把属性放到标签下
 */
@SuppressWarnings("rawtypes")
public class DataVO {
	private Map tags;

	public Map getTags() {
		return tags;
	}

	public void setTags(Map tags) {
		this.tags = tags;
	}

}
