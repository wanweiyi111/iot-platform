package com.hzyw.iot.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.hzyw.iot.vo.TagVO;
		
public class DeviceBean {
	//@SerializedName(value = "namespace", alternate = {"namespace"})
	private String namespace;//分类或命名空间
    private String deviceId;//设备ID
    private String objectName;//设备名称
    private String agreement;//协议
    private String timeStamp;//时间戳	
	
    private List<TagVO> tags;
        
	public List<TagVO> getTags() {
		return tags;
	}
	public void setTags(List<TagVO> tags) {
		this.tags = tags;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getAgreement() {
		return agreement;
	}
	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	

}
