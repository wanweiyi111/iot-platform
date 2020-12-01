package com.hzyw.iot.netty.vo;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import io.netty.buffer.ByteBuf;

public class ModbusInfo {
	private byte[] stx;//帧开始标志，2 字节，取值为 FFFFH；
	private byte[] ver;//协议版本号，当前版本为 00H
	private byte[] seq;//帧序列号
	private byte[] len;//DATA 域的长度，4个字节
	private byte[] crc;//从 VER 到 DATA 所有字节的 CRC16 校验值，2 字节,初始值为 FFFFH
	private byte[] data;//data数据
	private byte[] fullData;//总数据
	
	private byte[] dataType = new byte[1];//指令
	
	public ModbusInfo(ByteBuf buf) { //上报场景
		this.source = buf; 
		this.source.resetReaderIndex(); //从新回到起始位置
		this.data = new byte[this.source.readableBytes()- stx.length - ver.length - seq.length - len.length  - crc.length];
		//System.out.println(this.source.readableBytes());
		this.fullData = new byte[this.source.readableBytes()];
        this.source.readBytes(stx)
        .readBytes(ver)
        .readBytes(seq)
        .readBytes(len)
        .readBytes(data)
        .readBytes(crc);
        dataType[0] = data[0];//指令
        this.source.resetReaderIndex(); //从新回到起始位置
        this.source.readBytes(fullData); //全部数据包
	}

    private ByteBuf source; //源数据
    {
    	stx = new byte[2];
    	ver = new byte[1];
    	seq = new byte[1];
    	len = new byte[4];
    	crc = new byte[2];
    }
	 /*public ModbusInfo(){//下发场景
	    	try {
				this.stx=HeadHandlerUtil.hexStrToByteArr("ffff") ; //1
				this.ver=HeadHandlerUtil.hexStrToByteArr("00");  //1
		        this.crc=HeadHandlerUtil.hexStrToByteArr("ffff");  //1
			} catch (Exception e) {
				e.printStackTrace();
			}  
	    }*/

	public byte[] getStx() {
		return stx;
	}

	public String getStx_str() {
		return stx!=null?HeadHandlerUtil.convertByteToHexString(stx):"";
	}
	
	public void setStx(byte[] stx) {
		this.stx = stx;
	}

	public byte[] getVer() {
		return ver;
	}

	public String getVer_str() {
		return ver!=null?HeadHandlerUtil.convertByteToHexString(ver):"";
	}
	
	public void setVer(byte[] ver) {
		this.ver = ver;
	}

	public byte[] getSeq() {
		return seq;
	}

	public String getSeq_str() {
		return seq!=null?HeadHandlerUtil.convertByteToHexString(seq):"";
	}
	
	public void setSeq(byte[] seq) {
		this.seq = seq;
	}

	public byte[] getLen() {
		return len;
	}
	
	public String getLen_str() {
		return len!=null?HeadHandlerUtil.convertByteToHexString(len):"";
	}
	
	public void setLen(byte[] len) {
		this.len = len;
	}

	public byte[] getCrc() {
		return crc;
	}
	
	public String getCrc_str() {
		return crc!=null?HeadHandlerUtil.convertByteToHexString(crc):"";
	}

	public void setCrc(byte[] crc) {
		this.crc = crc;
	}

	public byte[] getFullData() {
		return fullData;
	}
	
	public String getFullData_str() {
		return fullData!=null?HeadHandlerUtil.convertByteToHexString(fullData):"";
	}

	public void setFullData(byte[] fullData) {
		this.fullData = fullData;
	}

	public byte[] getData() {
		return data;
	}

	public String getData_str() {
		return data!=null?HeadHandlerUtil.convertByteToHexString(data):"";
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getDataType() {
		return dataType;
	}

	public String getDataType_str() {
		return dataType!=null?HeadHandlerUtil.convertByteToHexString(dataType):"";
	}
	
	public void setDataType(byte[] dataType) {
		this.dataType = dataType;
	}

	
}
