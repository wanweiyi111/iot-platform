package com.hzyw.iot.netty.vo;

import java.util.List;


public class FilterBufVo {
	//private byte[] fullData = new byte[1];
	private byte[] body;
	private List<String> listStr;
	private List<String> listBugStr;
	
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public List<String> getListStr() {
		return listStr;
	}

	public void setListStr(List<String> listStr) {
		this.listStr = listStr;
	}

	public List<String> getListBugStr() {
		return listBugStr;
	}

	public void setListBugStr(List<String> listBugStr) {
		this.listBugStr = listBugStr;
	}
	
	
}
