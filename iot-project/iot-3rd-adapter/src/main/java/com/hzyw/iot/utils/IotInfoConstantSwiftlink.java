package com.hzyw.iot.utils;

 import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 速亿联-614型号设备-人脸识别温度计-设备初始化
 *
 */
public class IotInfoConstantSwiftlink {
	private static final Logger logger = LoggerFactory.getLogger(IotInfoConstantSwiftlink.class);
    //public static final Map<String, Map<String, Map<String, Object>>> allDevInfo = new HashMap<String, Map<String, Map<String, Object>>>();
    public static final Map<String, Map<String, Object>> slk_face_iotInfo_ = new HashMap<String, Map<String, Object>>();
 
    public static final String dev_dataaccess_key = "agreement"; //接入类型
    public static final String dev_dataaccess_value = "8209_4128";      //接入类型值 公司编号+产品编号
    
    //基本属性
    public static final String uuid ="uuid";  //=deviceId
    public static final String sn ="sn";
    public static final String device_type_name ="device_type_name";
    public static final String device_type_code ="device_type_code";
    public static final String vendor_name ="vendor_name";
    public static final String vendor_code ="vendor_code";
    public static final String model ="model";
    public static final String version_software ="version_software";
    public static final String version_hardware ="version_hardware";
    public static final String date_of_production ="date_of_production";
    public static final String up_time ="up_time";
    public static final String ipaddr_v4 ="ipaddr_v4";
    public static final String ipaddr_v6 ="ipaddr_v6";
    public static final String mac_addr ="mac_addr";
    public static final String online ="online";
    public static final String malfunction ="malfunction";
     
    
    //614型号人脸识别设备-自定义属性
    /**
     * deviceId
     */
    public static final String slk_614_face_dev_addr ="face_dev_addr"; //地址
    public static final String slk_614_face_dev_no ="face_dev_no"; //编号
    public static final String slk_614_match_company ="match_company"; //单位
    public static final String slk_614_temperatur_threshold ="temperatur_threshold"; //发烧温度判断阈值
    
    static String[] slk_614_face_def_attributers={slk_614_face_dev_addr,slk_614_face_dev_no,slk_614_temperatur_threshold};
     
    //private static final Map<String, String > tomda_charger_unit_ = new HashMap<String, String>();//指标值单位
     
	
     
    public static void initUnitData(ObjectService objectService){
    	//设备
    	//tomda_charger_unit_.put(xx, "℃"); //充电量
    }
    
    public static void initData(ObjectService objectService){
    	initUnitData(objectService);
    	try{
    		//allDevInfo.put(base_tomda_charger_port, tomda_charger_iotInfo_);
       	 
    		logger.info("===============初始化人脸识别设备614 数据 =====>>>=============="); 
        	//JSONArray  tomda_charger_data_map = JSONArray.parseArray(tomda_charger_json);
        	
        	//获取充电桩设备配置数据
    		JSONObject json = new JSONObject(); 
    		json.put("vendor_code", 8209);// 公司编号  
    		json.put("device_type_code", 4128); //614型号传感器类型的设备
        	List<JSONObject> _data_map = objectService.findObjects(json);
        	
        	//test 
        	/*_data_map = new ArrayList<JSONObject>();
        	String ss = "{ \"match_company\": \"武汉佰钧成\",\"date_of_production\": \"\", \"device_type_code\": 4128, \"device_type_name\": \"face camera\", \"ipaddr_v4\": \"\", \"ipaddr_v6\": \"\", \"mac_addr\": \"\", \"malfunction\": 0, \"model\": \"IPC-FACE\", \"sn\": \"012363-EA6924-86A8EE\", \"up_time\": 0, \"uuid\": \"1020-222c7a122cf961a0-2011-ffff-9e69\", \"vendor_code\": 8209, \"vendor_name\": \"swiftlink\", \"version_hardware\": \"mlx614\", \"version_software\": \"1.0\" } ";
        	JSONObject contentJson = JSON.parseObject(ss);
          	_data_map.add(contentJson);
        	*/
        	logger.info(">>>获取到设备数  =" + _data_map.size());
        	for(int p=0; p < _data_map.size(); p++){
        		JSONObject jsonObject = (JSONObject) _data_map.get(p);
            	String _sn = (String)jsonObject.get("sn");   
            	logger.info("===sn==>>>" + _sn);
             	//基本属性
            	slk_face_iotInfo_.put(_sn+"_attribute", new HashMap<String,Object>());
            	slk_face_iotInfo_.get(_sn+"_attribute").put(sn, jsonObject.get("sn")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(uuid, jsonObject.get("uuid")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(device_type_name, jsonObject.get("device_type_name")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(device_type_code, jsonObject.get("device_type_code")); 

            	slk_face_iotInfo_.get(_sn+"_attribute").put(vendor_name, jsonObject.get("vendor_name")); 

            	slk_face_iotInfo_.get(_sn+"_attribute").put(vendor_code, jsonObject.get("vendor_code")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(model, jsonObject.get("model")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(version_software, jsonObject.get("version_software")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(version_hardware, jsonObject.get("version_hardware")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(date_of_production, jsonObject.get("date_of_production")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(up_time, jsonObject.get("up_time")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(ipaddr_v4, jsonObject.get("ipaddr_v4")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(ipaddr_v6, jsonObject.get("ipaddr_v6")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(mac_addr, jsonObject.get("mac_addr")); 
            	slk_face_iotInfo_.get(_sn+"_attribute").put(malfunction, jsonObject.get("malfunction")); 
            	
            	//自定义属性
            	slk_face_iotInfo_.put(_sn+"_defAttribute", new HashMap<String,Object>());
            	slk_face_iotInfo_.get(_sn+"_defAttribute").put(slk_614_face_dev_addr, "");   //deviceId
            	slk_face_iotInfo_.get(_sn+"_defAttribute").put(slk_614_face_dev_no, ""); 
            	slk_face_iotInfo_.get(_sn+"_defAttribute").put(slk_614_face_dev_no, ""); 
            	slk_face_iotInfo_.get(_sn+"_defAttribute").put(slk_614_match_company, jsonObject.get("match_company")); 
            	slk_face_iotInfo_.get(_sn+"_defAttribute").put(slk_614_temperatur_threshold, jsonObject.get("temperatur_threshold"));
        	}
        	logger.info("===============初始化人脸识别设备614数据 完毕=====<<<=============="); 
        	  
    	}catch(Exception e){
    		logger.info("===============初始化人脸识别设备614 异常、请检查数据是否合理!!!===================");
    		logger.error("===============初始化人脸识别设备614数据 异常、请检查数据是否合理!!!===================",e);
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
