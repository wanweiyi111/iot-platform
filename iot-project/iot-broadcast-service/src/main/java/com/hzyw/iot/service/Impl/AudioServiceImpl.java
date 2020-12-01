package com.hzyw.iot.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hzyw.iot.config.AudioConfig;
import com.hzyw.iot.service.AudioService;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.util.IotInfoConstant;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
@Service
public class AudioServiceImpl implements AudioService{
	public static final Map<String, Object> overallMap = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);
	@Autowired
	private AudioConfig audioConfig;
	
	@Autowired
	private ObjectService objectService;
	/**
	 * 登录音柱 (拿到JSessionID)
	 */
	@Override
	public void Login() {
		JSONObject jsonLogin = new JSONObject();	
		jsonLogin.put("User", "admin");
		jsonLogin.put("Passwd", "admin");
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/login").body(jsonLogin.toString()).execute().body();
		JSONObject jsonId = new JSONObject(returnData);
		overallMap.put("JSessionID", jsonId.get("JSessionID").toString());
	}
	/**
	 * 获取终端清单
	 */
	@Override
	public List getTermIds() {
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/getTermIds"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).execute().body();
		JSONObject jsonId = new JSONObject(returnData);
		//System.out.println("TermIds:"+jsonId.get("TermIds"));
		return  (List) jsonId.get("TermIds");
	}
	/**
	 * 获取终端状态
	 */
	@Override
	public List getTermState(List TermIds) {
		JSONObject json = new JSONObject();	
		json.put("TermIDs", TermIds);
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/getTermState"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(json.toString()).execute().body();
		JSONObject jsonId = new JSONObject(returnData);
		//System.out.println("Terms:"+jsonId.get("Terms"));
		return  (List) jsonId.get("Terms");
	}
	
	
	@Override
	public JSONObject error(JSONObject TermIds) {
		if(TermIds==null||(int)TermIds.get("Ret")!=0) {
			if((int)TermIds.get("Ret")==8001) {
				Login();
				logger.info("===============重新获取JSESSIONID");
			}
			return null;
		}
		return TermIds;
	}
	
	/**
	 *	创建广播会话
	 */
	@Override
	public int FileSessionCreate(String uuid) {
		com.alibaba.fastjson.JSONObject MogoDB = new com.alibaba.fastjson.JSONObject();
		MogoDB.put("vendor_name", "Audio");
		MogoDB.put("type", "Audio");
		MogoDB.put("deviceId", uuid);
    	List<com.alibaba.fastjson.JSONObject> data_map = objectService.findObjects(MogoDB);
    	if(data_map==null) {
    		System.out.println("mogoDb未找到初始化信息");
    		return -1;
    	}
    	String mogoDbSN =  data_map.get(0).get("serialNum").toString();
    	
		List TermIds= getTermIds();
		List Terms=getTermState(TermIds);//获取设备状态
		for(int i = 0;i < Terms.size(); i ++){
			JSONObject jsonTerms = new JSONObject(Terms.get(i));
			String ip = jsonTerms.get("IP").toString();
			if(mogoDbSN.equals(ip)) {
				int id = (int)jsonTerms.get("ID");
				//overallMap.put(uuid, id);//uuid绑定设备ID
				JSONObject FileSessionCreate = new JSONObject();
				List list=new ArrayList();
				list.add(id);
				FileSessionCreate.put("Tids", list);
				FileSessionCreate.put("PlayPrior", 0);
				FileSessionCreate.put("Name", "test");
				String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/FileSessionCreate"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(FileSessionCreate.toString()).execute().body();
				JSONObject jsonId = new JSONObject(returnData);
				overallMap.put(uuid, (int)jsonId.get("Sid"));//uuid绑定sid
				return (int)jsonId.get("Sid");
			}
			
		}
		
		
		return -1;
	}
	
	/**
	 *	播放媒体文件
	 */
	@Override
	public String FileSessionSetProg(JSONObject json) {
		int create = (int)FileSessionCreate(json.get("uuid").toString());//获取到sid(会话ID)
		int file = (int) json.get("file");//获取到sid(会话ID)
		if(create==-1) {
			System.out.println("创建广播失败");
			return null;
		}
		JSONObject FileSessionCreate = new JSONObject();
		FileSessionCreate.put("Sid", create);
		FileSessionCreate.put("ProgId", file);//播放文件
		FileSessionCreate.put("Name", "SetProg");
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/FileSessionSetProg"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(FileSessionCreate.toString()).execute().body();
		JSONObject returnDataJson = new JSONObject(returnData);
		if((int)returnDataJson.get("Ret")!=0) {
			System.out.println("播放文件失败");
			return null;
		}
		
		return returnDataJson.toString();
	}
	
	/**
	 *	删除广播会话
	 */
	@Override
	public String FileSessionDestory(String uuid) {
		if(!overallMap.containsKey(uuid)) {
			System.out.println("该会话不存在");
			return null;
		}
		
		JSONObject FileSessionDestory = new JSONObject();
		FileSessionDestory.put("Sid", overallMap.get(uuid));
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/FileSessionDestory"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(FileSessionDestory.toString()).execute().body();
		JSONObject returnDataJson = new JSONObject(returnData);
		if((int)returnDataJson.get("Ret")!=0) {
			System.out.println("删除文件失败");
			return null;
		}
		
		return returnDataJson.toString();
	}
	
	/**
	 *	文本广播
	 */
	@Override
	public String TextPlay(JSONObject json) {
		/*if(!overallMap.containsKey(json.get("uuid"))) {
			FileSessionCreate(json.get("uuid").toString());
			System.out.println("创建会话");
		}*/
		List TermIds= getTermIds();
		List Terms=getTermState(TermIds);//获取设备状态
		
		for(int i = 0;i < Terms.size(); i ++){
		JSONObject jsonTerms = new JSONObject(Terms.get(i));
		List list=new ArrayList();
		list.add(jsonTerms.get("ID"));
		Date date = DateUtil.date(System.currentTimeMillis());
		String format = DateUtil.format(date, "yyyyMMddHHmmss");
		JSONObject TextPlay = new JSONObject();
		TextPlay.put("Content", json.get("text"));
		TextPlay.put("TargetIds", list);
		TextPlay.put("TargetType", 1);
		TextPlay.put("Time", format);
		TextPlay.put("Playtime", 1);
		TextPlay.put("PlayPrior", 1);
		TextPlay.put("TextCode", 2);
		
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/TextPlay"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(TextPlay.toString()).execute().body();
		JSONObject returnDataJson = new JSONObject(returnData);
		if((int)returnDataJson.get("Ret")!=0) {
			System.out.println("文本广播失败");
			return null;
		}
		
		}
		return null;
	}
	
	
	/**
	 *	终端音量设置
	 */
	@Override
	public String TermVolSet(JSONObject json) {
		List TermIds= getTermIds();
		List Terms=getTermState(TermIds);//获取设备状态
		
		for(int i = 0;i < Terms.size(); i ++){
		JSONObject jsonTerms = new JSONObject(Terms.get(i));
		List list=new ArrayList();
		list.add(jsonTerms.get("ID"));
		
		JSONObject TermVolSet = new JSONObject();
		TermVolSet.put("TermIds", list);
		TermVolSet.put("Volume", json.get("volume"));
		
		String returnData =HttpRequest.post("http://"+audioConfig.getHost()+"/TermVolSet"+";JSESSIONID="+AudioServiceImpl.overallMap.get("JSessionID")).body(TermVolSet.toString()).execute().body();
		JSONObject returnDataJson = new JSONObject(returnData);
		if((int)returnDataJson.get("Ret")!=0) {
			System.out.println("音量调节失败");
			return null;
		}
		
		}
		
		
		return null;
	}
	
	@Override
	public String MLCreateNode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String FileUpload(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String MLListDir(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
