package com.hzyw.iot.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface FFmpegCommandService {
	
	/**
	 * 直播开始
	 */
	public void liveStart(JSONObject para);
	
	/**
	 * 直播停止
	 */
	public void liveStop(JSONObject para);
	
	/**
	 * 获取缓存中活动的连接或者说是进程
	 * 
	 * @param para
	 * @return
	 */
	public List<Map> liveList(JSONObject para);
	 
	/**
	 * 查询点播列表
	 */
	public void queryVODList();
	

	
	
}
