package com.hzyw.iot.vo.dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hzyw.iot.vo.dataaccess.DevInfoDataVO;

/**
 * 非盒子设备接入，硬件设备信息初始化定义，此信息最终会上报到物联网平台
 */
public class IotInfo {
    private String id; //待确定
    private Map<String, String> data; //临时定义的变量，只限于 逻辑里面临时使用的 
    private List<DevInfoDataVO> devInfoDataVOList; //硬件设备信息列表 (可以直接上报的,硬件本身自带的)  里面包含了SN等信息

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

	public List<DevInfoDataVO> getDevInfoDataVOList() {
		return devInfoDataVOList;
	}

	public void setDevInfoDataVOList(List<DevInfoDataVO> devInfoDataVOList) {
		this.devInfoDataVOList = devInfoDataVOList;
	}
	
	public static String[] getUUID(int number){
        if(number < 1){
            return null;
        }
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = getUUID();
        }
        return retArray;
    }
	
	public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号 
        return uuid.replaceAll("-", "");
    }
	 
 
	/**
	 * 定义一个PLC
	 * 
	 * @param plc_iotInfo_
	 * @param SN1  集中器地址
	 * @param plc_attributers
	 */
	public static void addPlc(Map<String, Map<String, String>> plc_iotInfo_,String SN1,String... plc_attributers) {
		Map<String,String> init_value = new HashMap<String,String>();
		//有的属性上报的时候就已经默认有值了,通过手工配置默认值
        for(String attr : plc_attributers){
        	init_value.put(attr, "");//没指定则默认空
        }
    	plc_iotInfo_.put(SN1, init_value);
	}
	
	public static void initDevAttribute(Map<String, Map<String, Object>> plc_iotInfo_,String atrType,String... plc_attributers) {
        if(plc_iotInfo_.containsKey(atrType)){
        	for(String attr : plc_attributers){
        		plc_iotInfo_.get(atrType).put(attr, "");
            }
        }else{
        	Map<String,Object> init_value = new HashMap<String,Object>();
            for(String attr : plc_attributers){
            	init_value.put(attr, "");//没指定则默认空
            }
        	plc_iotInfo_.put(atrType, init_value);
        }
    	
	}
	public static void initMethod(Map<String, Map<String, Object>> plc_iotInfo_,String atrType,String... plc_methods) {
		Map<String,Object> init_value = new HashMap<String,Object>();
        for(String method : plc_methods){
        	String[] ms = method.split(":");
        	init_value.put(ms[0], ms[1]); 
        }
    	plc_iotInfo_.put(atrType, init_value);
	}
	public static void initCmd(Map<String, Map<String, Object>> plc_iotInfo_,String atrType,String... plc_methods) {
		Map<String,Object> init_value = new HashMap<String,Object>();
        for(String method : plc_methods){
        	String[] ms = method.split(":");
        	init_value.put(ms[1] , ms[0]); 
        }
    	plc_iotInfo_.put(atrType, init_value);
	}
	public static void initSignl(Map<String, Map<String, Object>> plc_iotInfo_,String atrType,String... plc_Signl) {
		initMethod( plc_iotInfo_, atrType, plc_Signl);
	}
 	 

	@Override
    public String toString() {
        return "IotInfo{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
