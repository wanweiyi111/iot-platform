package com.hzyw.iot.vo.dc.enums;

import com.hzyw.iot.util.ByteUtils;

/**
 *  
 *接入类型
 */
public enum ERTUChannelFlag {
    WIFF(56789),//模拟其他设备进入
    PLC(12345); //PLC接入  68表示 传递的开头一个字节是 68H 
	//TEST("port");
	//ByteUtils.hex2byte("12345")
      
    private int type;

    ERTUChannelFlag(int type) {
        this.type = type;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

     
}
