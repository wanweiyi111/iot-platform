package com.hzyw.iot.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "mqtt.connection") 
@PropertySource(value = { "classpath:mqttConnection.properties" }, encoding = "utf-8")
public class MqttConnectionConfig {
	
	private Map<String,String>  comm = new HashMap<String,String>();
	private Map<String,String>  serviceOnline = new HashMap<String,String>();
	private Map<String,String>  serviceOffline = new HashMap<String,String>();
	/**
	 * 下发，主题相关配置
	 */
	private Map<String,String>  request = new HashMap<String,String>();
	//下发后返回结果，主题相关配置
	private Map<String,String>  response = new HashMap<String,String>();
	private Map<String,String>  responseFailover = new HashMap<String,String>();
	
	public Map<String, String> getComm() {
		return comm;
	}

	public void setComm(Map<String, String> comm) {
		this.comm = comm;
	}

	public Map<String, String> getServiceOnline() {
		return serviceOnline;
	}

	public void setServiceOnline(Map<String, String> serviceOnline) {
		this.serviceOnline = serviceOnline;
	}

	public Map<String, String> getServiceOffline() {
		return serviceOffline;
	}

	public void setServiceOffline(Map<String, String> serviceOffline) {
		this.serviceOffline = serviceOffline;
	}

	public Map<String, String> getRequest() {
		return request;
	}

	public void setRequest(Map<String, String> request) {
		this.request = request;
	}

	public Map<String, String> getResponse() {
		return response;
	}

	public void setResponse(Map<String, String> response) {
		this.response = response;
	}

	public Map<String, String> getResponseFailover() {
		return responseFailover;
	}

	public void setResponseFailover(Map<String, String> responseFailover) {
		this.responseFailover = responseFailover;
	}
	
	
	

}
