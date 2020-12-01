package com.hzyw.iot.utils;

/**
 * PLC接入
 * 	方法和命令码 对应关系
 * 
 * @author Administrator
 *
 */
public class IotInfoMethod {
  
    public static final	String[] plc_methods = {"operator_70:70",
    			"operator_71:71",
    			"operator_73:73",
    			"operator_82:82",
    			"operator_83:83",
    			"operator_84:84",
    			"operator_8c:8c",
    			"operator_8e:8e",
    			"operator_8f:8f",
    			"operator_96:96",
    			"operator_97:97",
    			"operator_98:98",
    			"operator_99:99",
    			"operator_f0:f0",
    			"operator_f1:f1",
    			"operator_f2:f2",
    			"operator_f3:f3",
    			"operator_f4:f4",
    			"operator_f5:f5",
    			"operator_f6:f6",
    			"set_brightness:42",  //调灯光
    			"set_onoff:42",       //开关
    			"operator_f7:f7",
    			"operator_45:45",
    			"operator_fb:fb",
    			"operator_fc:fc",
    			"operator_fd:fd",
    			"operator_9a:9a",
    			"operator_9b:9b",
    			"operator_9c:9c",
    			"operator_60:60",
    			"operator_61:61",
    			"operator_46:46",
    			"operator_fe:fe",
    			"operator_62:62",
    			"operator_63:63",
    			"operator_66:66",
    			"operator_67:67",
    			"operator_9e:9e",
    			"operator_9d:9d",
    			"operator_69:69",
    			"operator_4a:4a",
    			"operator_f8:f8",
    			"operator_f9:f9",
    			"operator_6a:6a",
    			"operator_6b:6b",
    			"operator_6c:6c",
    			"operator_6d:6d",
    			"operator_6f:6f",
    			"operator_47:47",
    			"operator_48:48",
    			"operator_49:49",
    			"operator_4b:4b",
    			"operator_4c:4c"
    	 };

	
	//双向映射，方便根据数据
    public static final String[] plc_req_ack = {
			"00:80", //控制码 ：应答码
			"01:80",
			"02:80",
			"03:80",
			"04:80",
			"80:80"
	};
    
    public static final String[] plc_ack_req = {
			"80:00", //应答码:控制码  
			"80:01",
			"80:02",
			"80:03",
			"80:04",
			"80:80"
	};
	
    public static final String[] plc_signls = {
    		"current_source_bit8:109001",//温度过高报警位
    		"current_source_bit7:109002",//温度过低报警位
    		"current_source_bit6:109003",//无法启动报警位
    		"current_source_bit5:109004",//输出短路报警位
    		"current_source_bit4:109005",//输出开路报警位
    		"current_source_bit3:109006",//功率过高报警位
    		"current_source_bit2:109007",//输入电压过高报警位
    		"current_source_bit1:109008",//输入电压过低报警位
    		"current_source_bit0:109009",//电源状态位；0：电源关，1：电源开
    		"controller_bit0:109010",//A 继电器状态位；0表示关，1表示开
    		"controller_bit1:109011",//欠流报警位
    		"controller_bit2:109012",//过流报警位
    		"controller_bit3:109013",//欠压报警位
    		"controller_bit4:109014",//过压警位
    		"controller_bit5:109015",//欠载报警位
    		"controller_bit6:109016",//过载报警位
    		"controller_bit7:109017",//继电器失效报警位
    		"controller_bit8:109018",//B 继电器状态位；0表示关，1表示开    
    		"controller_bit9:109019",//欠流报警位 
    		"controller_bit10:109020",//过流报警位
    		"controller_bit11:109021",//欠压报警位
    		"controller_bit12:109022",//过压警位
    		"controller_bit13:109023",//欠载报警位
    		"controller_bit14:109024",//过载报警位
    		"controller_bit15:109025",//继电器失效报警位     
    };
    
}
