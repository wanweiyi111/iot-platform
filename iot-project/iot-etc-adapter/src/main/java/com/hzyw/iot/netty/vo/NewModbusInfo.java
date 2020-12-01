package com.hzyw.iot.netty.vo;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import io.netty.buffer.ByteBuf;

public class NewModbusInfo {
	private byte[] stx;//帧开始标志，2 字节，取值为 FFFFH；
	private byte[] com;//指令
	private byte[] len;//DATA 域的长度，4个字节
	private byte[] data;//data数据
	private byte[] xor;//xor
	private byte[] etx;//结束符ff
	private byte[] fullData;//总数据
	
	
	public NewModbusInfo(ByteBuf buf) { //上报场景
		this.source = buf; 
		this.source.resetReaderIndex(); //从新回到起始位置
		this.data = new byte[this.source.readableBytes()- stx.length - com.length- len.length  - xor.length - etx.length];
		//System.out.println(this.source.readableBytes());
		this.fullData = new byte[this.source.readableBytes()];
        this.source.readBytes(stx)
        .readBytes(com)
        .readBytes(len)
        .readBytes(data)
        .readBytes(xor)
        .readBytes(etx);
        this.source.resetReaderIndex(); //从新回到起始位置
        this.source.readBytes(fullData); //全部数据包
	}

    private ByteBuf source; //源数据
    {
    	stx = new byte[2];
    	com = new byte[1];
    	len = new byte[1];
    	xor = new byte[1];
    	etx = new byte[1];
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


	public byte[] getLen() {
		return len;
	}
	
	public String getLen_str() {
		return len!=null?HeadHandlerUtil.convertByteToHexString(len):"";
	}
	
	public void setLen(byte[] len) {
		this.len = len;
	}

	public byte[] getXor() {
		return xor;
	}
	
	public String getXor_str() {
		return xor!=null?HeadHandlerUtil.convertByteToHexString(xor):"";
	}

	public void setXor(byte[] xor) {
		this.xor = xor;
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


	public byte[] getCom() {
		return com;
	}
	public String getCom_str() {
		return com!=null?HeadHandlerUtil.convertByteToHexString(com):"";
	}
	public void setCom(byte[] com) {
		this.com = com;
	}

	public byte[] getEtx() {
		return etx;
	}
	public String getEtx_str() {
		return etx!=null?HeadHandlerUtil.convertByteToHexString(etx):"";
	}

	public void setEtx(byte[] etx) {
		this.etx = etx;
	}
}
