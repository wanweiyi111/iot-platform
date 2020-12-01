package com.hzyw.iot.service.Impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.commandManager.CommandManagerImpl;
import com.hzyw.iot.commandManager.data.CommandTasker;
import com.hzyw.iot.service.FFmpegCommandService;
import com.hzyw.iot.utils.IotInfoConstant;

@Service
public class FFmpegCommandServiceImpl implements FFmpegCommandService {
	 
	/*@Autowired
	private ApplicationConfig applicationConfig;//全局配置
*/
	 
	@Override
	public void liveStart(JSONObject para) {
		//ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/1010-d8b38d288d431464-3001-ffff-37cf
	   /* {
	        "camera_device_id": "101101",
	        "camera_operator": "live",
	        "camera_sn": "0000001",
	        "camera_rtsp_url": "rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream",
	        "camera_rtmp_url": "rtmp://192.168.3.183:1935/rtmplive",
	        "camera_command":"ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/1010-d8b38d288d431464-3001-ffff-37cf",
	        "messageId": "msgid-live1-01"

	    }*/
		//JSONObject para = new JSONObject();
		//para.put("camera_device_id", "101101"); //设备ID
		//para.put("camera_operator", "live"); //直播操作
		//para.put("camera_sn", "101101sn");
		//para.put("camera_rtsp_url", "rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream"); //源流
		//para.put("camera_rtmp_url", "rtmp://192.168.3.183:1935/rtmplive/101101"); //转化为rtmp流
		//para.put("camera_command", "ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/1010-d8b38d288d431464-3001-ffff-37cf"); //后门  提供测试用 可直接传入ffmpeg指令
		//para.put("messageId", "msgid-live1-01");
		
		String operatorId = para.getString("camera_device_id")+ "," +para.getString("camera_operator");
		String camera_rtsp_url = para.getString("camera_rtsp_url");
		String camera_rtmp_url = para.getString("camera_rtmp_url");
		
		CommandManagerImpl manager = new CommandManagerImpl();
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//默认方式发布任务
		String command_option1 =" -vcodec copy -f flv -s 640x360 -an "; //5~6秒延迟
		String command_option2 =" -acodec aac -strict experimental -ar 44100 -ac 2 -b:a 96k -r 50 -b:v 500k -s 640*480 -f flv ";//延迟2秒
		String command_option = para.getString("command_option")==null?command_option2 : para.getString("camera_command");
		String command = "ffmpeg -i "+camera_rtsp_url+command_option+camera_rtmp_url +"/"+operatorId;
		
		command = para.getString("camera_command")==null?command : para.getString("camera_command");
		System.out.println(">>> operatorID: " + operatorId);
		System.out.println(">>> command: " + command);
		CommandTasker task = manager.start(operatorId, command);
		if(task != null){
			//记录此操作管理实例
			if(IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id"))!=null){
				IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).put(operatorId, manager);
			}else{
				Map<String, CommandManagerImpl> cmMap = new HashMap<String, CommandManagerImpl>();
				cmMap.put(operatorId, manager); //某个摄像头或视频设备的某个操作  如直播 如点播 如截图 如录制
				IotInfoConstant.gloable_CommandManager.put(para.getString("camera_device_id"), cmMap);
			}
		}
		if(IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id"))!=null
				&& IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).get(operatorId) != null){
			Collection<CommandTasker> alltask = IotInfoConstant.gloable_CommandManager
					.get(para.getString("camera_device_id"))
					.get(operatorId).getTaskDao().getAll();
			System.out.println("alltask:" + JSON.toJSONString(alltask));
		}else{
			System.out.println("alltask:  none .."  );
		}
 		
	}

	@Override
	public void liveStop(JSONObject para) {
		String operatorId = para.getString("camera_device_id")+ "," +para.getString("camera_operator");
		if(IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id"))!=null
				&& IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).get(operatorId) != null){
			Collection<CommandTasker> alltask = IotInfoConstant.gloable_CommandManager
					.get(para.getString("camera_device_id"))
					.get(operatorId).getTaskDao().getAll();
			System.out.println("alltask:" + JSON.toJSONString(alltask));
			
			System.out.println("---------将要关闭此句柄--------" + IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).get(operatorId));
			IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).get(operatorId).stopAll(); //关闭
			IotInfoConstant.gloable_CommandManager.get(para.getString("camera_device_id")).remove(operatorId) ; //清理此操作
		}else{
			System.out.println("alltask:  none ../没有在缓存中找到此直播管理句柄 ！"  );
		}
		
	}
	
	@Override
	public List<Map> liveList(JSONObject para) {
		List<Map> alltask = new ArrayList<Map>();
		for(String deviceid : IotInfoConstant.gloable_CommandManager.keySet()){
			Map<String,Object> it = new HashMap<String,Object>();
			it.put("deviceId", deviceid);
			Map<String, CommandManagerImpl> item = IotInfoConstant.gloable_CommandManager.get(deviceid);
			List m = new ArrayList();
			for(String deviceidAndOpratorCode : item.keySet()){
				m.add( deviceidAndOpratorCode);
			}
			it.put("deviceId-operator", m);
			alltask.add(it);
		}
		return alltask;
	}
	
	@Override
	public void queryVODList() {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
