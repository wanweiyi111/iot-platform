package com.hzyw.iot.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.FFmpegCommandService;
 
@RestController
public class FFmpegCommandController {
	private static final Logger logger = LoggerFactory.getLogger(FFmpegCommandController.class);
	
	@Autowired
	private FFmpegCommandService ffmpegCommand;
	
	/**
	 *
	   {
	        "camera_device_id": "101101",
	        "camera_operator": "live",
	        "camera_sn": "0000001",
	        "camera_rtsp_url": "rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream",
	        "camera_rtmp_url": "rtmp://192.168.3.183:1935/rtmplive",
	        "camera_command":"ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/1010-d8b38d288d431464-3001-ffff-37cf",
	        "messageId": "msgid-live1-01"
	    }	 
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/camera/live/start", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String startLive(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("start param: " + jsonParam.toJSONString());
			if(jsonParam.getString("camera_device_id") == null || jsonParam.getString("camera_operator") == null){
				logger.info("param is invalid!!! " );
				resultJsonParam.put("msg", "fail");
			    resultJsonParam.put("data", "请检查入参!");
				return resultJsonParam.toJSONString();
			}
			ffmpegCommand.liveStart(jsonParam);
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "");
		}catch(Exception e){
			logger.warn("FFmpegCommandController::startLive ;  exception" + " /camera_device_id=" + jsonParam.getString("camera_device_id") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	/**
	 *
	   {
	        "camera_device_id": "101101",
	        "camera_operator": "live",
	        "messageId": "msgid-live1-02"
	    }	 
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/camera/live/stop", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
   public String stopLive(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("stop param: " + jsonParam.toJSONString());
			if(jsonParam.getString("camera_device_id") == null || jsonParam.getString("camera_operator") == null){
				logger.info("param is invalid!!! " );
				resultJsonParam.put("msg", "fail");
			    resultJsonParam.put("data", "请检查入参!");
				return resultJsonParam.toJSONString();
			}
			ffmpegCommand.liveStop(jsonParam);
		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "");
		}catch(Exception e){
			logger.warn("FFmpegCommandController::stopLive ;  exception" + " /camera_device_id=" + jsonParam.getString("camera_device_id") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
   }
	@ResponseBody
	@RequestMapping(value = "/camera/live/list", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	 public String ListLive(@RequestBody JSONObject jsonParam) {
			JSONObject resultJsonParam = new JSONObject();
			try{
				logger.info("list param: " + jsonParam.toJSONString());
				List<Map> rs = ffmpegCommand.liveList(jsonParam);
			    resultJsonParam.put("msg", "seccess");
			    resultJsonParam.put("data", rs);
			}catch(Exception e){
				logger.warn("FFmpegCommandController::ListLive ;  exception" + " /camera_device_id=" + jsonParam.getString("camera_device_id") ,e); 
				resultJsonParam.put("msg", "fail");
			    resultJsonParam.put("data", "exception :" + e.getMessage());
			}
		    return resultJsonParam.toJSONString();
	   }
	
}
