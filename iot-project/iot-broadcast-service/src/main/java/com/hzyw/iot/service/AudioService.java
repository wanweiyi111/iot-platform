package com.hzyw.iot.service;

import java.util.List;

import cn.hutool.json.JSONObject;

public interface AudioService {
	/**
	 * 登录音柱
	 */
	public void Login();
	
	/**
	 * 获取终端清单
	 */
	public List getTermIds();
	/**
	 * 获取终端状态
	 */
	public List getTermState(List TermIds);
	
	
	
	/**
	 * 媒体文件上传
	 */
	public String MLCreateNode();
	
	
	
	/**
	 * 创建媒体节点
	 */
	public String FileUpload(JSONObject json);
	
	
	
	/**
	 * 列出所有节目
	 */
	public String MLListDir(JSONObject json);
	
	/**
	 *	创建广播会话
	 */
	public int FileSessionCreate(String uuid);
	
	/**
	 *	播放媒体文件
	 */
	public String FileSessionSetProg(JSONObject json);
	
	
	/**
	 *	删除广播会话
	 */
	public String FileSessionDestory(String uuid);
	
	
	/**
	 *	文本广播
	 */
	public String TextPlay(JSONObject uuid);
	
	
	/**
	 *	终端音量设置
	 */
	public String TermVolSet(JSONObject uuid);
	/**
	 * 错误码处理
	 */
	public JSONObject error(JSONObject TermIds);
}
