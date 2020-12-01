package com.hzyw.iot.controller;

import com.hzyw.iot.service.ObjectService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

/**
 * 设备接入定义
 * @author z
 *
 */
@RestController
public class ObjectController {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectController.class);
 
	@Autowired
	private ObjectService objectService; 
	
	/**
	 * 新增
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/object/saveObjects", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveObjects(@RequestBody List<JSONObject> list) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			objectService.saveObjects(list);

			resultJsonParam.put("errorCode", "200");
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", "");
		}
		return resultJsonParam.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/object/removeById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String removeById(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			objectService.removeById(jsonParam.getString("_id") );
			resultJsonParam.put("errorCode", "200");
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", "");
		}
		return resultJsonParam.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/object/removeByObject", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String removeByObject(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			Object obj = objectService.removeByObject(jsonParam );
			resultJsonParam.put("errorCode", "200");
			resultJsonParam.put("data", obj );
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", e.getMessage());
		}
		return resultJsonParam.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/object/findObject", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String findObject(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			Object obj = objectService.findObjects(jsonParam );
			resultJsonParam.put("errorCode", "200");
			resultJsonParam.put("data", obj);
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", "");
		}
		return resultJsonParam.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/object/saveUnits", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnits(@RequestBody List<JSONObject> list) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			objectService.saveUnits(list);

			resultJsonParam.put("errorCode", "200");
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", "");
		}
		return resultJsonParam.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/object/findUnits", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String findUnits(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try {
			Object obj = objectService.findUnits(jsonParam );
			resultJsonParam.put("errorCode", "200");
			resultJsonParam.put("data", obj);
		} catch (Exception e) {
			resultJsonParam.put("errorCode", "201");
			resultJsonParam.put("errorDesc", "fail");
			resultJsonParam.put("data", "");
		}
		return resultJsonParam.toJSONString();
	}
	
}
 