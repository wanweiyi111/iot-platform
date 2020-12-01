package com.hzyw.iot.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.vo.dc.IotInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
public class IotInfoConstant {

    /**
     * 缓存‘端口/设备属性方法  ’关系
     *          
     *     1：allDevInfo= Map<port, Map<sn设备地址_attribute, Map<key设备字段, value设备字段默认值>>>
     *     1：allDevInfo= Map<port, Map<sn设备地址_defAttribute, Map<key设备字段, value设备字段默认值>>>
     *     2：allDevInfo= Map<port, Map<sn设备地址_method, Map<方法名, 命令码>>>
     *     3：allDevInfo= Map<port, Map<sn设备地址_cmd, Map<命令码, 方法名>>>
     *     4：allDevInfo= Map<port, Map<sn设备地址_signl, Map<key信号字段, value信号字段默认值>>>	   如节点离线，节点告警
     *    
     */
    public static final Map<String, Map<String, Map<String, Object>>> allDevInfo = new HashMap<String, Map<String, Map<String, Object>>>();
    private static final Map<String, Map<String, Object>> plc_iotInfo_ = new HashMap<String, Map<String, Object>>();
    
    
    /**
     * 缓存‘端口/plcSN/node详情’ 对应关系
     * --用于支持根据port, plc_sn 查询 plc下对应的节点列表和详细信息
     * Map<port, Map<plc_sn, Map<节点属性, 值>>>   
     */
    public static final Map<String, Map<String, List<Map<String,String>>>> plc_relation_plcsnToNodelist = new HashMap<String, Map<String, List<Map<String,String>>>>();//plc和node关联关系
    
    
    /**
     * 根据 deviceID查询 , sn
     * 
     * Map<Map<id,  sn> deviceID肯定是唯一的
     */
    public static final Map<String, String> plc_relation_deviceToSn = new HashMap<String, String>();//缓存plc和node对应关系
    
    public static final Map<String, String> plc_relation_plcIDToPort = new HashMap<String, String>();//缓存plc_id和端口对应关系

    //=================接入PLC 初始化信息========start=========
     
    //公共
    public static final String base_plc_port = "12345";
    public static final String ftp_server_port = "ftp_server_port"; //开放端口
    public static final String dev_plc_dataaccess_key = "agreement"; //接入类型
    public static final String dev_plc_dataaccess_value = "plc";      //每种接入，初始化数据的时候应该要指定接入类型
    
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
    
    //PLC集中器-属性
    /**
     * deviceId
     */
    public static final String dev_plc_plc_id ="plc_plc_id";  //deviceId
    public static final String dev_plc_plc_sn ="plc_sn"; //集中器地址，唯一标识
    //public static final String dev_plc_objectName ="plc_objectName"; //集中器名称字段
    public static final String dev_plc_a_voltage ="plc_a_voltage"; 			//集中器，A相电压
    public static final String dev_plc_a_electric ="plc_a_electric"; //集中器，A相电流
    public static final String dev_plc_a_power ="plc_a_power"; 				//集中器，A相功率:
    public static final String dev_plc_a_pf ="plc_a_pf"; 					//集中器，A相功率因素
    public static final String dev_plc_elect_energy ="plc_elect_energy"; //集中器，电能
    public static final String dev_plc_ad1_in ="plc_ad1_in"; //集中器，AD1输入
    public static final String dev_plc_b_voltage ="plc_b_voltage";          //集中器，B相电压
    public static final String dev_plc_b_electric ="plc_b_electric";        //集中器，B相电流
    public static final String dev_plc_b_power ="plc_b_power"; 				//集中器，B相功率
    public static final String dev_plc_b_pf ="plc_b_pf"; 					//集中器，B相功率因素
    public static final String dev_plc_all_power ="plc_all_power"; 		//集中器，总功率
    public static final String dev_plc_ad2_in ="plc_ad2_in"; 			//集中器，AD2输入
    public static final String dev_plc_c_voltage ="plc_c_voltage";   //集中器，C相电压
    public static final String dev_plc_c_electric ="plc_c_electric"; //集中器，C相电流
    public static final String dev_plc_c_power ="plc_c_power"; 			//集中器，C相功率
    public static final String dev_plc_c_pf ="plc_c_pf";       			//集中器，C相功率因素
    public static final String dev_plc_3_power ="plc_3_power"; 			//集中器，三相功率因素
    
    static String[] plc_def_attributers={dev_plc_plc_id,dev_plc_plc_sn,dev_plc_a_voltage,dev_plc_a_electric,dev_plc_a_power,dev_plc_a_pf
			,dev_plc_elect_energy,dev_plc_ad1_in,dev_plc_b_voltage,dev_plc_b_electric,dev_plc_b_power
			,dev_plc_b_pf,dev_plc_all_power,dev_plc_ad2_in,dev_plc_c_voltage,dev_plc_c_electric
			,dev_plc_c_power,dev_plc_c_pf,dev_plc_3_power};
    
    //集中器属性(设备接入前配置引入的属性)
	public static final String dev_plc_cfg_longitude ="plc_cfg_longitude";
	public static final String dev_plc_cfg_latitude ="plc_cfg_latitude";
	public static final String dev_plc_cfg_sq ="plc_cfg_sq";
	public static final String dev_plc_cfg_gksd_start ="plc_cfg_gksd_start";
	public static final String dev_plc_cfg_gksd_end ="plc_cfg_gksd_end";
    public static final String dev_plc_cfg_systime ="plc_cfg_systime"; //时钟
    public static final String dev_plc_cfg_step5_groupAtuo ="plc_cfg_step5_groupAtuo"; //组网个数   
    
    static String[] plc_def_attributers_initcfg={ dev_plc_cfg_longitude, dev_plc_cfg_latitude, dev_plc_cfg_sq ,dev_plc_cfg_gksd_start 
    		,dev_plc_cfg_gksd_end,dev_plc_cfg_systime ,dev_plc_cfg_step5_groupAtuo,ftp_server_port};
     
    private static final Map<String, String > plc_unit_ = new HashMap<String, String>();
    
    //灯具属性
    public static final String dev_plc_node_id ="plc_node_id"; //deviceId 
    public static final String dev_plc_node_sn ="plc_node_sn"; //节点地址
    //public static final String dev_plc_node_type ="plc_node_type";  //节点类型  :jie点类型    单灯(OL)，双灯(TL)，HID ,LED
    public static final String dev_plc_node_temperature ="plc_node_temperature";//温度(电源或灯控的)
    public static final String dev_plc_node_voltage_in ="plc_node_voltage_in";//输入电压
    public static final String dev_plc_node_voltage_out ="voltage";//输出电压
    public static final String dev_plc_node_capacity_out ="capacity";
    public static final String dev_plc_node_a_electri_in ="plc_node_a_electri_in";//A路输入电流	
    public static final String dev_plc_node_a_electri_out ="current";//A路输出电流

    public static final String dev_plc_node_a_power ="capacity";//A路有功功率
    public static final String dev_plc_node_a_pf ="plc_node_a_pf";//A路功率因数
    //public static final String dev_plc_node_a_lighting ="plc_node_a_lighting";//A亮度
    public static final String dev_plc_node_a_brightness ="plc_node_a_brightness";//A亮度   0-100
    //灯具属性  (设备接入前配置引入的属性)
	public static final String dev_plc_node_type ="plc_node_type";        //控制类型   单灯A(01)，单灯B(02),双灯(03) 
	public static final String dev_plc_node_devCode ="plc_node_devcode";  //设备码
	public static final String dev_plc_node_group ="plc_node_group";  	  //节点所属组
	//灯具属性 (调灯光协议引入)
	public static final String dev_plc_node_cCode ="plc_node_cCode";  //控制码
    public static final String dev_plc_node_a_status ="plc_node_a_status";// 在线状态  0-离线 1-在线
    public static final String dev_plc_node_a_onoff ="plc_node_a_onoff";  // 开关状态  1-开灯 0-关灯
    //灯具属性 (状态数据上报协议引入)
    public static final String dev_plc_node_power ="plc_node_power";//输出功率
    public static final String dev_plc_node_runtime ="plc_node_runtime";//灯具运行时长
    public static final String dev_plc_node_electri_energy ="plc_node_electri_energy";//电能
    public static final String dev_plc_node_error_runtime ="plc_node_error_runtime";//故障时长
    
    //新增属性
    public static final String dev_plc_node_electri_in ="plc_node_electri_in";//输入电流
    public static final String dev_plc_node_electri_out ="plc_node_electri_out";//输出电流
    public static final String dev_plc_node_power_in ="plc_node_power_in";//输入功率
    public static final String dev_plc_node_pf ="plc_node_pf";//功率因素
    public static final String dev_plc_node_exception_state ="plc_node_exception_state";//异常状态
    public static final String dev_plc_node_b_electri_in ="plc_node_b_electri_in";//B路输入电流
    public static final String dev_plc_node_b_power ="plc_node_b_power";//B路有功功率
    public static final String dev_plc_node_b_pf ="plc_node_b_pf";//B路功率因数
    public static final String dev_plc_node_b_brightness ="plc_node_b_brightness";//B路亮度
    public static final String dev_plc_node_brightness ="plc_node_brightness";//调光亮度
    public static final String dev_plc_node_state ="plc_node_state";//状态
    
    public static final String dev_plc_node_temperature_li ="plc_node_temperature_li";//灯具温度
    
	static String[] plc_node_def_attributers={dev_plc_node_sn,dev_plc_node_type,dev_plc_node_temperature,dev_plc_node_temperature_li,dev_plc_node_voltage_in
			,dev_plc_node_voltage_out,dev_plc_node_a_electri_in,dev_plc_node_a_electri_out,dev_plc_node_a_power
			,dev_plc_node_a_pf,dev_plc_node_a_brightness,dev_plc_node_a_status,dev_plc_node_a_onoff
			,dev_plc_node_devCode,dev_plc_node_group,dev_plc_node_cCode
			,dev_plc_node_power,dev_plc_node_runtime,dev_plc_node_electri_energy,dev_plc_node_error_runtime,dev_plc_node_electri_in
			,dev_plc_node_electri_out,dev_plc_node_power_in,dev_plc_node_pf,dev_plc_node_exception_state,dev_plc_node_b_electri_in
			,dev_plc_node_b_power,dev_plc_node_b_pf,dev_plc_node_b_brightness,dev_plc_node_brightness,dev_plc_node_state
			};
	
	
	
	public static String plc_json = "";
    public static String plc_node_json = ""; 
    public static String getUnit(String attributer){
    	return plc_unit_.get(attributer);
    }
    public static void initUnitData(){
    	//节点
    	plc_unit_.put(dev_plc_node_temperature, "℃"); //温度
    	plc_unit_.put(dev_plc_node_voltage_in, "mV"); //输入电压
    	plc_unit_.put(dev_plc_node_voltage_out, "mV"); //输出电压
    	plc_unit_.put(dev_plc_node_a_electri_in, "mA"); //A路输入电流
    	plc_unit_.put(dev_plc_node_a_electri_out, "mA"); //A路输出电流
    	plc_unit_.put(dev_plc_node_a_power, "mW"); //A路有功功率
    	plc_unit_.put(dev_plc_node_a_pf, "%"); //A路功率因数
    	//plc_unit_.put(dev_plc_node_a_brightness, ""); //A亮度   0-100
    	//plc_unit_.put(dev_plc_node_type, ""); //控制类型   单灯A(01)，单灯B(02),双灯(03) 
    	//plc_unit_.put(dev_plc_node_devCode, ""); //设备码
    	//plc_unit_.put(dev_plc_node_group, ""); //节点所属组
    	//plc_unit_.put(dev_plc_node_cCode, ""); //控制码
    	//plc_unit_.put(dev_plc_node_a_status, ""); // 在线状态  0-离线 1-在线
    	//plc_unit_.put(dev_plc_node_a_onoff, ""); // 开关状态  0-开灯 1-关灯
    	
    	plc_unit_.put(dev_plc_node_power, "mW");
    	plc_unit_.put(dev_plc_node_runtime, "h");
    	plc_unit_.put(dev_plc_node_electri_energy, "W");
    	plc_unit_.put(dev_plc_node_error_runtime, "h");
    	
    	//集中器
    	plc_unit_.put(dev_plc_a_voltage, "mV"); //集中器，A相电压
    	plc_unit_.put(dev_plc_a_electric, "mA");//集中器，A相电流
    	plc_unit_.put(dev_plc_a_power, "mW"); //集中器，A相功率:
    	plc_unit_.put(dev_plc_a_pf, "F"); //集中器，A相功率因素
    	//plc_unit_.put(dev_plc_ad1_in, ""); //集中器，AD1输入
    	plc_unit_.put(dev_plc_b_voltage, "mV"); //集中器，B相电压
    	
    	plc_unit_.put(dev_plc_b_electric, "mA"); //集中器，B相电流
    	plc_unit_.put(dev_plc_b_power, "mW"); //集中器，B相功率
    	plc_unit_.put(dev_plc_b_pf, "F"); //集中器，B相功率因素
    	plc_unit_.put(dev_plc_all_power, "mW"); //集中器，总功率
    	//plc_unit_.put(dev_plc_ad2_in, "℃"); //集中器，AD2输入
    	plc_unit_.put(dev_plc_c_voltage, "mV"); //集中器，C相电压
    	plc_unit_.put(dev_plc_c_electric, "mA"); //集中器，C相电流
    	plc_unit_.put(dev_plc_c_power, "mW"); //集中器，C相功率
    	plc_unit_.put(dev_plc_c_pf, "F"); //集中器，C相功率因素
    	plc_unit_.put(dev_plc_3_power, "F"); //集中器，三相功率因素
    	plc_unit_.put(dev_plc_node_capacity_out, "mW"); 
    	
    	
    	plc_unit_.put(dev_plc_node_electri_in, "mA"); //输入电流
    	plc_unit_.put(dev_plc_node_electri_out, "mA"); //输出电流
    	plc_unit_.put(dev_plc_node_power_in, "W"); //输入功率
    	plc_unit_.put(dev_plc_node_pf, "%"); //功率因素
    	//plc_unit_.put(dev_plc_node_exception_state, "℃"); //异常状态
    	plc_unit_.put(dev_plc_node_b_electri_in, "mA"); //B路输入电流
    	plc_unit_.put(dev_plc_node_b_power, "mW"); //B路有功功率
    	plc_unit_.put(dev_plc_node_b_pf, "%"); //B路功率因数
    	plc_unit_.put(dev_plc_node_b_brightness, "%"); //B路亮度
    	plc_unit_.put(dev_plc_node_brightness, "%"); //调光亮度
    	plc_unit_.put(dev_plc_node_temperature_li, "℃"); 
    	
    	//plc_unit_.put(dev_plc_node_state, ""); //状态
    }
    
    public static void initData(List<JSONObject> plc_data_map,ObjectService objectService){
    	JSONObject field = new JSONObject();  
    	field.put("type", "plc_unit"); //能判断出是集中器的字段
    	List<JSONObject> unitJson = objectService.findUnits(field);
    	if(unitJson.size()<=0) {
    		System.out.println("===============PLC查询单位有误，为null==============,请检查mogoDb!!!!!!!!!!!"); 
    		return;
    	}
    	//System.out.println("-----mogoDB下拿到的数据(plc_unit)-------"+unitJson.get(0).toString() ); 
        Iterator iter = unitJson.get(0).entrySet().iterator();
    	 while (iter.hasNext()) {
             Map.Entry entry = (Map.Entry) iter.next();
             plc_unit_.put(entry.getKey().toString(),  entry.getValue().toString()); 
         } 
    	//initUnitData();
    	try{
    		allDevInfo.put(base_plc_port, plc_iotInfo_);
    		System.out.println("===============初始化集中器、灯具节点数据 =====>>>=============="); 
        	//JSONArray  plc_data_map = JSONArray.parseArray(plc_json);
        	for(int p=0; p < plc_data_map.size(); p++){
        		JSONObject jsonObject = (JSONObject) plc_data_map.get(p);
        		//System.out.println("-----mogoDB下拿到的数据(集中器)-------"+jsonObject.toString() ); 
        		String plc_id = (String)jsonObject.get("plc_id");   //deviceID  有算法计算
            	String plc_sn = (String)jsonObject.get("plc_sn");   //集中器地址
            	
            	System.out.println("-----new一个集中器--plc_id/plc_sn-------" + plc_id + "/" + plc_sn ); 
            	
        		String plc_cfg_step1_longitude = (String)jsonObject.get("plc_cfg_step1_longitude"); //经度           N22°32′43.86\\″   转化成整形表示  
            	String plc_cfg_step1_latitude = (String)jsonObject.get("plc_cfg_step1_latitude"); //维度     转化成整形表示
            	String plc_cfg_step1_sq= (String)jsonObject.get("plc_cfg_step1_sq"); //时区        转化成整形表示
            	String plc_cfg_step1_gksd_start= (String)jsonObject.get("plc_cfg_step1_gksd_start"); //光控时段-开始时分
            	String plc_cfg_step1_gksd_end= (String)jsonObject.get("plc_cfg_step1_gksd_end"); //光控时段  结束时分
            	String plc_cfg_step2_timestamp= String.valueOf(System.currentTimeMillis()); //时钟      时间戳
            	String plc_cfg_step5_groupAtuo = (String)jsonObject.get("plc_cfg_step5_groupAtuo");    //组网个数
            	
            	String vendor_name = (String)jsonObject.get("vendor_name");   
            	String vendor_code = (String)jsonObject.get("vendor_code");  
            	String model = (String)jsonObject.get("model");
            	String device_type_code_plc = (String)jsonObject.get("device_type_code");
            	String server_port = (String)jsonObject.get("ftp_server_port");
            	
            	//----new一个集中器 并put到plc_iotInfo_
            	//基本属性
            	IotInfo.initDevAttribute(plc_iotInfo_, plc_sn+"_attribute", base_attributers);  
            	//plc_iotInfo_.get(plc_sn+"_attribute").put("device_type_code", "");//设备类型代码
            	plc_iotInfo_.get(plc_sn+"_attribute").put("device_type_name", "集中控制器");//设备类型名称
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_sn, plc_sn);//PLC SN
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_vendor_name, vendor_name);//厂家名字
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_vendor_code, Integer.parseInt(vendor_code));//厂家代码
            	
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_model, model);//设备型号
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_uuid, plc_id);//uuid
            	plc_iotInfo_.get(plc_sn+"_attribute").put(dev_base_device_type_code, Integer.parseInt(device_type_code_plc));//设备类型代码
            	//def属性
            	IotInfo.initDevAttribute(plc_iotInfo_, plc_sn+"_defAttribute", plc_def_attributers);//硬件自带
            	IotInfo.initDevAttribute(plc_iotInfo_, plc_sn+"_defAttribute", plc_def_attributers_initcfg);//设备接入前配置引入的属性
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_plc_id, plc_id);   //deviceId
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_plc_sn, plc_sn); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_dataaccess_key, dev_plc_dataaccess_value); //接入类型
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_longitude, plc_cfg_step1_longitude); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_latitude, plc_cfg_step1_latitude); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_sq, plc_cfg_step1_sq); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_gksd_start, plc_cfg_step1_gksd_start); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_gksd_end, plc_cfg_step1_gksd_end); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_systime, plc_cfg_step2_timestamp); 
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(dev_plc_cfg_step5_groupAtuo, plc_cfg_step5_groupAtuo);  
            	//---->其他def属性
            	plc_iotInfo_.get(plc_sn+"_defAttribute").put(ftp_server_port, server_port); 
            	 
            	IotInfo.initMethod(plc_iotInfo_, plc_sn+"_method", IotInfoMethod.plc_methods); //方法
            	IotInfo.initCmd(plc_iotInfo_, plc_sn+"_cmd", IotInfoMethod.plc_methods);       //命令码
            	IotInfo.initSignl(plc_iotInfo_, plc_sn+"_signl", IotInfoMethod.plc_signls); //信号
            	IotInfo.initMethod(plc_iotInfo_, plc_sn+"_req_ack", IotInfoMethod.plc_req_ack); //控制码和应答码对应关系
            	IotInfo.initMethod(plc_iotInfo_, plc_sn+"_ack_req", IotInfoMethod.plc_ack_req); //应答码和控制码对应关系
            	
            	//---deviceId和SN关系
            	plc_relation_deviceToSn.put(plc_id, plc_sn);  
            	plc_relation_plcIDToPort.put(plc_id, server_port);
            	
            	//集中器和节点关系
            	List<Map<String, String>> nodelist = new ArrayList<Map<String, String>>();
            	
            	//初始化当前PLC下的灯具节点数据
            	//JSONArray  plc_node_data_map = JSONArray.parseArray(plc_node_json);
            	JSONObject json = new JSONObject();  
            	json.put("vendor_name", "innosmart");
            	json.put("type", "plc_Node"); //能判断出是集中器的字段
            	List<JSONObject> plc_node_data_map = objectService.findObjects(json);
            	
            	for(int j=0; j < plc_node_data_map.size(); j++){
            		JSONObject it = (JSONObject) plc_node_data_map.get(j);
            		//System.out.println("-----mogoDB下拿到的数据(灯节点)-------"+it.toString() ); 
            		String plc_id_temp = (String)it.get("plc_id"); //deviceID
            		if(!plc_id_temp.equals(plc_id)){
            			continue;
            		}
            		
            		String plc_node_id = (String)it.get("plc_node_id"); //deviceID
                	String plc_node_sn = (String)it.get("plc_node_sn"); //灯具地址  0000020004ee
                	System.out.println("     new一个灯具--plc_node_id/plc_node_sn-------" + plc_node_id + "/" + plc_node_sn ); 
                	String plc_node_type = (String)it.get("plc_node_type");      
                	String plc_node_devCode = (String)it.get("plc_node_devCode");   //设备码   ,设备码只和灯具有关 ，和产品类型对齐
                	String plc_node_group = (String)it.get("plc_node_group");      //组号   1~255
                	
                	String node_vendor_name = (String)it.get("vendor_name");   
                	String node_vendor_code = (String)it.get("vendor_code");  
                	String node_model = (String)it.get("model");
                	String node_device_type_code = (String)it.get("device_type_code");
                	
                	
                	//----new一个灯具信息 并put到plc_iotInfo_
                	IotInfo.initDevAttribute(plc_iotInfo_, plc_node_sn+"_attribute", base_attributers);  //基本属性
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_vendor_name, node_vendor_name);//厂家名字
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_vendor_code, Integer.parseInt(node_vendor_code));//厂家代码
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_model, node_model);//设备型号
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_uuid, plc_node_id);//uuid
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_sn, plc_node_sn);//SN
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_device_type_code, Integer.parseInt(node_device_type_code));//设备类型代码
                	plc_iotInfo_.get(plc_node_sn+"_attribute").put(dev_base_device_type_name, "灯节点"); 
                	
                	IotInfo.initDevAttribute(plc_iotInfo_, plc_node_sn+"_defAttribute", plc_node_def_attributers);//自定义属性
            		//plc_iotInfo_.get(plc_node_sn+"_defAttribute_node").put(IotInfoConstant.dev_plc_plc_sn, plc_sn);
            		plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(IotInfoConstant.dev_plc_node_sn, plc_node_sn);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_id, plc_node_id);  //deviceId
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_plc_sn, plc_sn);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_type, plc_node_type); 
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_devCode, plc_node_devCode);
                	
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_voltage_out, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_power, 0);
                	//test
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_voltage_in, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_electri_in, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_power_in, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_pf, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_brightness, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_b_pf, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_runtime, 0);
                	
                	
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_onoff, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_b_electri_in, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_capacity_out, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_status, 1);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_electri_out, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_temperature_li, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_b_brightness, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_b_power, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_pf, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_brightness, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_temperature, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_a_electri_in, 0);
                	plc_iotInfo_.get(plc_node_sn+"_defAttribute").put(dev_plc_node_electri_out, 0);
                	
                	
                	//---deviceId和SN关系
                	plc_relation_deviceToSn.put(plc_node_id, plc_node_sn);
                	
                	//----plc_sn和node关系
                	Map<String, String> _it = new HashMap<String,String>();
                	_it.put(dev_plc_plc_id, plc_id);
                	_it.put(dev_plc_node_id, plc_node_id);
                	_it.put(dev_plc_plc_sn, plc_sn);  				 //sn (plc)
                	_it.put(dev_plc_node_sn, plc_node_sn);  		 //sn (node)
                	_it.put(dev_plc_node_type, plc_node_type);  	 //设备类型
                	_it.put(dev_plc_node_devCode, plc_node_devCode); //设备码
                	_it.put(dev_plc_node_group, plc_node_group); 	 //组   必须 1~255
                	nodelist.add(_it);
            	}
            	if(plc_relation_plcsnToNodelist.get(base_plc_port) == null){
            		Map<String,List<Map<String, String>>> plc_nodes = new HashMap<String,List<Map<String, String>>>();
            		plc_nodes.put(plc_sn , nodelist);
            		plc_relation_plcsnToNodelist.put(base_plc_port, plc_nodes);
            	}else{
            		plc_relation_plcsnToNodelist.get(base_plc_port).put(plc_sn, nodelist);
            	}
            	System.out.println(""  ); 
        	}
    		System.out.println("===============初始化集中器、灯具节点数据 完毕=====<<<=============="); 
        	  
    	}catch(Exception e){
    		System.out.println("===============初始化集中器、灯具节点数据异常、请检查数据是否合理!!!===================");
    		e.printStackTrace();
    	}
    }
     
    
    
  //=================接入PLC 初始化信息========end=========
    /*public static void main(String[] args){
    	//设备列表
    	for(String att_type : plc_iotInfo_.keySet()){
    		System.out.println("======type :" + att_type);
    		for(String key :plc_iotInfo_.get(att_type).keySet()){
    			System.out.println("           -" + key + "/" +plc_iotInfo_.get(att_type).get(key));
    		}
    	}
    }*/
}
