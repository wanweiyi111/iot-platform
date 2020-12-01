package com.hzyw.iot.service;

import java.util.List;
import com.alibaba.fastjson.JSONObject;

public interface ObjectService {

	public List<JSONObject> findObjects(JSONObject objcet );

	public void saveObjects(List<JSONObject> jsonParam);
	
	public void removeById(String id );
	
	public Object removeByObject(JSONObject object );
	
	public List<JSONObject> findObjectByPage(JSONObject para );
	
	public void saveUnits(List<JSONObject> jsonParam);
	
	public List<JSONObject> findUnits(JSONObject objcet );
}
