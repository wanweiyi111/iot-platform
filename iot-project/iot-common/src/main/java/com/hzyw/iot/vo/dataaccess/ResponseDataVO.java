package com.hzyw.iot.vo.dataaccess;



/**
 * 设备数据接入-下发消息请求-返回下发消息
 * 
 * 例如：{
		deviceId:dg01
		attributer:
	    [{ 		属性：温度
	        	属性值：29
	        	属性值单位："C
	      },
	       { 	属性：开关
	        	属性值：1
	        	属性值单位：""
	      },
	      ...........
	    ]
	}
 *
 *
 */
public class ResponseDataVO extends RequestDataVO {
	private static final long serialVersionUID = 1109560064440750698L;
    //如果后面要框定入参出参，覆盖methods的定义即可

}
