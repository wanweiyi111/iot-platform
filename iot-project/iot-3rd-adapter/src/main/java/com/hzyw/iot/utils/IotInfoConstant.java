package com.hzyw.iot.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.vo.dc.IotInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class IotInfoConstant {
	private static final Logger logger = LoggerFactory.getLogger(IotInfoConstant.class);
    //public static final Map<String, Map<String, Map<String, Object>>> allDevInfo = new HashMap<String, Map<String, Map<String, Object>>>();
    public static final Map<String, Map<String, Object>> tomda_charger_iotInfo_ = new HashMap<String, Map<String, Object>>();
    
    
    /**
     * 缓存‘端口/tomda_chargerSN/node详情’ 对应关系
     * --用于支持根据port, tomda_charger_sn 查询 tomda_charger下对应的节点列表和详细信息
     * Map<port, Map<tomda_charger_sn, Map<节点属性, 值>>>   
     */
   // public static final Map<String, Map<String, List<Map<String,String>>>> tomda_charger_relation_tomda_chargersnToNodelist = new HashMap<String, Map<String, List<Map<String,String>>>>();//tomda_charger和node关联关系
    
    
    /**
     * 根据 deviceID查询 , sn
     * 
     * Map<Map<id,  sn> deviceID肯定是唯一的
     */
    //public static final Map<String, String> tomda_charger_relation_deviceToSn = new HashMap<String, String>();//缓存tomda_charger和node对应关系
    
    //public static final Map<String, String> tomda_charger_relation_tomda_chargerIDToPort = new HashMap<String, String>();//缓存tomda_charger_id和端口对应关系

    //=================接入tomda_charger 初始化信息========start=========
     
    //公共
    //public static final String base_tomda_charger_port = "tomdaCharger";
    //public static final String ftp_server_port = "ftp_server_port"; //开放端口
    public static final String dev_dataaccess_key = "agreement"; //接入类型
    public static final String dev_dataaccess_value = "3rd_TomdaCharger";      //每种接入，初始化数据的时候应该要指定接入类型
    
    //基本属性
    public static final String dev_base_uuid ="uuid";  //=deviceId
    public static final String dev_base_sn ="sn";
    public static final String dev_base_device_type_name ="device_type_name";
    public static final String dev_base_device_type_code ="device_type_code";
    public static final String dev_base_vendor_name ="vendor_name";
    public static final String dev_base_vendor_code ="vendor_code";
    public static final String dev_base_model ="model";
    public static final String dev_base_version_software ="version_software";
    public static final String dev_base_version_hardware ="version_hardware";
    public static final String dev_base_date_of_production ="date_of_production";
    public static final String dev_base_up_time ="up_time";
    public static final String dev_base_ipaddr_v4 ="ipaddr_v4";
    public static final String dev_base_ipaddr_v6 ="ipaddr_v6";
    public static final String dev_base_mac_addr ="mac_addr";
    public static final String dev_base_online ="online";
    public static final String dev_base_malfunction ="malfunction";
    
    static String[] base_attributers={dev_base_uuid,dev_base_sn,dev_base_device_type_name,dev_base_device_type_code, dev_base_vendor_name,dev_base_vendor_code 
			,dev_base_model ,dev_base_version_software , dev_base_version_hardware,dev_base_date_of_production , dev_base_up_time
			,dev_base_ipaddr_v4 ,dev_base_ipaddr_v6 ,dev_base_mac_addr ,dev_base_online ,dev_base_malfunction  };
    
    //充电桩-属性
    /**
     * deviceId
     */
    public static final String dev_tomda_charger_id ="tomda_charger_id";  //deviceId
    public static final String dev_tomda_charger_serialNum ="tomda_charger_serialNum"; //充电桩序列号
    public static final String dev_tomda_charger_station_addr ="tomda_charger_station_addr"; //站点位置
    static String[] tomda_charger_def_attributers={dev_tomda_charger_id,dev_tomda_charger_serialNum,dev_tomda_charger_station_addr};
     
    private static final Map<String, String > tomda_charger_unit_ = new HashMap<String, String>();//指标值单位
     
	
	public static String tomda_charger_json = "";
     public static String getUnit(String attributer){
    	return tomda_charger_unit_.get(attributer);
    }
    public static void initUnitData(ObjectService objectService){
    	//设备
    	//tomda_charger_unit_.put(xx, "℃"); //充电量
    }
    
    public static void initData(ObjectService objectService){
    	initUnitData(objectService);
    	try{
    		//allDevInfo.put(base_tomda_charger_port, tomda_charger_iotInfo_);
       	 
    		logger.info("===============初始化充电桩数据 =====>>>=============="); 
        	//JSONArray  tomda_charger_data_map = JSONArray.parseArray(tomda_charger_json);
        	
        	//获取充电桩设备配置数据
    		JSONObject json = new JSONObject(); 
    		json.put("vendor_name", "Tomda");//汤姆达
    		json.put("type", "charger"); //充电桩
        	List<JSONObject> tomda_charger_data_map = objectService.findObjects(json);
        	
        	for(int p=0; p < tomda_charger_data_map.size(); p++){
        		JSONObject jsonObject = (JSONObject) tomda_charger_data_map.get(p);
        		String deviceId = (String)jsonObject.get("deviceId");   
            	String serialNum = (String)jsonObject.get("serialNum");   
            	String sitePosition = (String)jsonObject.get("sitePosition");
            	int device_type_code = (int)jsonObject.get("device_type_code");
            	String device_type_name = (String)jsonObject.get("device_type_name");
            	String dev_base_vendor_name1 = (String)jsonObject.get("dev_base_vendor_name");
            	int vendor_code = (int)jsonObject.get("vendor_code");
            	String model = (String)jsonObject.get("model");
            	
            	//基本属性
            	//String vendor_name = (String)jsonObject.get("vendor_name");   
            	//String model = (String)jsonObject.get("model");
            	//自定义属性
            	String tomda_charger_station_addr = (String)jsonObject.get("tomda_charger_station_addr");
            	
             	//基本属性
            	IotInfo.initDevAttribute(tomda_charger_iotInfo_, serialNum+"_attribute", base_attributers);  
            	//tomda_charger_iotInfo_.get(tomda_charger_sn+"_attribute").put("device_type_code", "");//设备类型代码
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_sn, serialNum);//tomda_charger SN
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_uuid, deviceId);//uuid
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_device_type_code, device_type_code);//设备类型代码
            	//tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_device_type_name, device_type_name);//设备类型名称
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_vendor_name, dev_base_vendor_name1);//厂家名字
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_vendor_code, vendor_code);//厂家代码
            	//tomda_charger_iotInfo_.get(serialNum+"_attribute").put(dev_base_model, model);//设备型号
            	/*tomda_charger_iotInfo_.get(serialNum+"_attribute").put("version_software", "");//设备软件版本
            	tomda_charger_iotInfo_.get(serialNum+"_attribute").put("version_hardware", "");//设备硬件版本*/   
            	
            	
            	//自定义属性
            	IotInfo.initDevAttribute(tomda_charger_iotInfo_, serialNum+"_defAttribute", tomda_charger_def_attributers);//硬件自带
            	tomda_charger_iotInfo_.get(serialNum+"_defAttribute").put(dev_tomda_charger_id, deviceId);   //deviceId
            	tomda_charger_iotInfo_.get(serialNum+"_defAttribute").put(dev_tomda_charger_serialNum, serialNum); 
            	tomda_charger_iotInfo_.get(serialNum+"_defAttribute").put(dev_tomda_charger_station_addr, sitePosition);   
            	IotInfo.initSignl(tomda_charger_iotInfo_, serialNum+"_signl", IotInfoMethod.tomda_charger_signls); //信号    < 信号编码,英文字段>
            	IotInfo.initCmd(tomda_charger_iotInfo_, serialNum+"_signl_2", IotInfoMethod.tomda_charger_signls); //信号    < 英文字段 ,信号编码>  initCmd刚好是反过来的，这里复用下
        	}
        	logger.info("===============初始化充电桩数据 完毕=====<<<=============="); 
        	  
    	}catch(Exception e){
    		logger.info("===============初始化充电桩数据 异常、请检查数据是否合理!!!===================");
    		logger.error("===============初始化充电桩数据 异常、请检查数据是否合理!!!===================",e);
    	}
    }
     
    
    
  //=================接入tomda_charger 初始化信息========end=========
    /*public static void main(String[] args){
    	//设备列表
    	for(String att_type : tomda_charger_iotInfo_.keySet()){
    		System.out.println("======type :" + att_type);
    		for(String key :tomda_charger_iotInfo_.get(att_type).keySet()){
    			System.out.println("           -" + key + "/" +tomda_charger_iotInfo_.get(att_type).get(key));
    		}
    	}
    }*/
}
