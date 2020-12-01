package com.hzyw.iot.vo.dc;

import com.hzyw.iot.util.ByteUtils;
import com.hzyw.iot.util.constant.ConverUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * modbus PLC -数据模型
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-23 14:16
 */
public class ModbusInfo {
	private static final Logger logger = LoggerFactory.getLogger(ModbusInfo.class);
	private byte[] headStart;  //1
    private byte[] address;  //6
    private byte[] headEnd;  //1
    private byte[] cCode;   //1
    private byte[] length;  //1
    private byte[] cmdCode;
    private byte[] pdt;
    private byte[] data;  //1字节命令码 + XX
    private byte[] crc;  //1
    private byte[] end;  //1
    private byte[] fullData;
    
    public  String getCacheMsgId(){
    	return this.getAddress_str()+"_"+this.getCmdCode_str()+"_"+this.getcCode_str();
    }
    
    /**
     * 获取下发到设备的CRC数据
     * @return
     */
    public  byte[] getNewCrcData(){
    	int len = headStart.length + address.length + headEnd.length + cCode.length + length.length 
    			+ cmdCode.length + pdt.length;   	
    	 //byte[] bt3 = new byte[len];  
         //System.arraycopy(headStart, 0, bt3, 0, headStart.length);  
         //System.arraycopy(address, 0, bt3, headStart.length, address.length);  
         
         ByteBuf byteBuf = Unpooled.buffer(len);
         byteBuf.writeBytes(headStart).writeBytes(address).writeBytes(headEnd).writeBytes(cCode).writeBytes(length)
         			.writeBytes(cmdCode).writeBytes(pdt); //CRC计算，根据CRC所在报文位置--》headStart所有的数据
         byte[] temp = byteBuf.array();
         //if(byteBuf !=null)byteBuf.release();
         if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
         return temp;  
          
    }
    
    /**
     * 提供响应或下发到设备
     * @return
     */
    public  byte[] getNewFullData(){
    	this.refresh(); //内容刷到最新  
    	int len = headStart.length + address.length + headEnd.length + cCode.length + length.length 
    			+ cmdCode.length + pdt.length + crc.length +end.length;   	
    	 //byte[] bt3 = new byte[len];  
         //System.arraycopy(headStart, 0, bt3, 0, headStart.length);  
         //System.arraycopy(address, 0, bt3, headStart.length, address.length);  
         
         ByteBuf byteBuf = Unpooled.buffer(len);
         byteBuf.writeBytes(headStart).writeBytes(address).writeBytes(headEnd).writeBytes(cCode).writeBytes(length)
         			.writeBytes(cmdCode).writeBytes(pdt).writeBytes(crc).writeBytes(end);
         
         byte[] temp = byteBuf.array();
         //if(byteBuf !=null)byteBuf.release();
         if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
         return temp;  
          
    }
    /**
     * 提供响应或下发到设备
     * @return
     */
    public  ByteBuf getNewFullDataWithByteBuf(){
    	this.refresh(); //内容刷到最新  
    	int len = headStart.length + address.length + headEnd.length + cCode.length + length.length 
    			+ cmdCode.length + pdt.length + crc.length +end.length;   	
    	 //byte[] bt3 = new byte[len];  
         //System.arraycopy(headStart, 0, bt3, 0, headStart.length);  
         //System.arraycopy(address, 0, bt3, headStart.length, address.length);  
         
         ByteBuf byteBuf = Unpooled.buffer(len);
         byteBuf.writeBytes(headStart).writeBytes(address).writeBytes(headEnd).writeBytes(cCode).writeBytes(length)
         			.writeBytes(cmdCode).writeBytes(pdt).writeBytes(crc).writeBytes(end);
         
         //byte[] temp = byteBuf.array();
         //if(byteBuf !=null)byteBuf.release();
         //if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
         return byteBuf;  
          
    }

    private ByteBuf source; //源数据

    {
    	headStart = new byte[ModBusModel.HEADSTART_LEN.len];
        address = new byte[ModBusModel.ADDRESS_LEN.len];
        headEnd = new byte[ModBusModel.HEADEND_LEN.len];
        cCode = new byte[ModBusModel.CCODE_LEN.len];
        length = new byte[ModBusModel.LENGTH_LEN.len];
        crc = new byte[ModBusModel.CRC_LEN.len];
        end = new byte[ModBusModel.END_LEN.len];
    }

    public ModbusInfo(){//下发场景
    	try {
			this.headStart=ConverUtil.hexStrToByteArr("68") ; //1
			this.headEnd=ConverUtil.hexStrToByteArr("68");  //1
	        this.end=ConverUtil.hexStrToByteArr("16");  //1
		} catch (Exception e) {
			e.printStackTrace();
		}  
    }
    public ModbusInfo(ByteBuf source) { //上报场景
    	try{
    		this.source = source; 
            //=pdt+cmdcode
            this.data = new byte[this.source.readableBytes()- headStart.length - address.length - headEnd.length  
                                 - cCode.length  - length.length - crc.length - end.length];
            this.fullData = new byte[this.source.readableBytes() - crc.length - end.length];

            this.source.readBytes(headStart)
                    .readBytes(address)
                    .readBytes(headEnd)
                    .readBytes(cCode)
                    .readBytes(length)
                    .readBytes(data) 
                    .readBytes(crc)
                    .readBytes(end);
            // fullData
            this.source.resetReaderIndex(); //从新回到起始位置
            this.source.readBytes(fullData); //全部数据包
            
            ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
            byte[] cmd = new byte[1];
            cmd[0] = data[0];
            this.setCmdCode(cmd);
            if(data.length >= 2){
            	byte[] pdt = new byte[data.length -1];
            	int i=0;
            	for(int p=1;p<data.length;p++){
            		pdt[i] = data[p];
            		i++;
            	}
            	this.setPdt(pdt);
            }else{
            	this.setPdt(new byte[0]);
            }
            
             
            //打印hex串
            String bw ="ModbusInfo{" +
    					        "headStart=" + ConverUtil.convertByteToHexString(headStart) +","+
    					        "address=" + ConverUtil.convertByteToHexString(address) + ","+
    					        "headEnd=" + ConverUtil.convertByteToHexString(headEnd) +", "+
    					        "cCode=" + ConverUtil.convertByteToHexString(cCode) +" ,"+
    					        "length=" + ConverUtil.convertByteToHexString(length) + ", "+
    					        "cmdCode=" + ConverUtil.convertByteToHexString(cmdCode) +" ,"+
    					        "pdt=" + (pdt.length>0?ConverUtil.convertByteToHexString(pdt):" ") + ","+
    					        "crc=" + ConverUtil.convertByteToHexString(crc) +","+
    					        "end=" + ConverUtil.convertByteToHexString(end) +
    					        '}';
            System.out.println("====>>>init ModbusInfo :====");
            System.out.println(bw);
            System.out.println("====<<<===");
            this.source.resetReaderIndex(); //从新回到起始位置
    	}catch(Exception e){
    		logger.error("///////////////上报数据 ModbusInfo模型转化异常:",e);
    		byte[] req = new byte[source.readableBytes()]; //
			source.readBytes(req);
			try {
				String body = new String(req, "UTF-8");
				logger.warn("///////////////上报数据 ModbusInfo模型转化异常 : responseMsg" + ByteUtils.bytesToHexString(req));
			} catch (UnsupportedEncodingException e1) {
				logger.error("///////////////上报数据 ModbusInfo模型转化异常:",e1);
			}
    	}
        
    }

    private enum ModBusModel {
    	HEADSTART_LEN(1),
        ADDRESS_LEN(6),
        HEADEND_LEN(1),
        CCODE_LEN(1),
        LENGTH_LEN(1),
        CRC_LEN(1),
    	END_LEN(1);

        int len;

        ModBusModel(int len) {
            this.len = len;
        }
    }

 

    public byte[] getPdt() {
		return pdt;
	}



	public void setPdt(byte[] pdt) {
		this.pdt = pdt;
	}



	public byte[] getHeadStart() {
		return headStart;
	}
	
	public String getHeadStart_str() {
		return ConverUtil.convertByteToHexString(headStart);
	}

	public void setHeadStart(byte[] headStart) {
		this.headStart = headStart;
	}

	public byte[] getAddress() {
		return address;
	}
	public String getAddress_str() {
		return address!=null?ConverUtil.convertByteToHexString(address):"";
	}
	 


	public void setAddress(byte[] address) {
		this.address = address;
	}



	public byte[] getHeadEnd() {
		return headEnd;
	}

	public String getHeadEnd_str() {
		return headEnd!=null?ConverUtil.convertByteToHexString(headEnd):"";
	}

	public void setHeadEnd(byte[] headEnd) {
		this.headEnd = headEnd;
	}



	public byte[] getcCode() {
		return cCode;
		
	}
	
	public String getcCode_str() {
		return cCode!=null?ConverUtil.convertByteToHexString(cCode):"";
	}



	public void setcCode(byte[] cCode) {
		this.cCode = cCode;
	}



	public byte[] getLength() {
		return length;
	}



	public void setLength(byte[] length) {
		this.length = length;
		
	}
	
	public void resetLength() {
		byte[] le = new byte[1];
		le = new byte[1];
		le[0] = (byte)(this.getCmdCode().length + this.getPdt().length);
		this.setLength(le);
	}



	public byte[] getCmdCode() {
		return cmdCode;
	}
	
	public String getCmdCode_str() {
		return cmdCode!=null?ConverUtil.convertByteToHexString(cmdCode):"";
	}



	public void setCmdCode(byte[] cmdCode) {
		this.cmdCode = cmdCode;
	}



	public byte[] getData() {
		return data;
	}



	public void setData(byte[] data) {
		this.data = data;
	}



	public byte[] getCrc() {
		return crc;
	}



	public void setCrc(byte[] crc) {
		this.crc = crc;
	}

	public void resetCrc() {
		//this.crc = crc;
		String checksum = ConverUtil.makeChecksum(ConverUtil.convertByteToHexString(getNewCrcData()));
		System.out.println("new crc=" + checksum);
		try {
			this.setCrc(ConverUtil.hexStrToByteArr(checksum));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 自动计算长度,CRC 
	 */
	public void refresh() {
		// 3,长度
		this.resetLength();  
		// 4,CRC计算
		this.resetCrc();
	}

	public byte[] getEnd() {
		return end;
	}
	
	public String getEnd_str() {
		return ConverUtil.convertByteToHexString(end);
	}

 
	public void setEnd(byte[] end) {
		this.end = end;
	}



	public byte[] getFullData() {
		return fullData;
	}



	public void setFullData(byte[] fullData) {
		this.fullData = fullData;
	}



	public ByteBuf getSource() {
		return source;
	}



	public void setSource(ByteBuf source) {
		this.source = source;
	}



	@Override
    public String toString() {
        String bw = "ModbusInfo{" + "\n" + "    headStart="
				+ ConverUtil.convertByteToHexString(getHeadStart()) + "\n"
				+ "    address="
				+ ConverUtil.convertByteToHexString(getAddress()) + "\n"
				+ "    headEnd="
				+ ConverUtil.convertByteToHexString(getHeadEnd()) + "\n"
				+ "    cCode="
				+ ConverUtil.convertByteToHexString(getcCode()) + "\n"
				+ "    length="
				+ ConverUtil.convertByteToHexString(getLength()) + "\n"
				+ "    cmdCode="
				+ ConverUtil.convertByteToHexString(getCmdCode()) + "\n"
				+ "    pdt=" + ConverUtil.convertByteToHexString(getPdt())
				+ "\n" + "    crc="
				+ ConverUtil.convertByteToHexString(getCrc()) + "\n"
				+ "    end=" + ConverUtil.convertByteToHexString(getEnd())
				+ "\n" + '}';
		 return bw;
    }
	public String toStringBW() {
        String bw =  ConverUtil.convertByteToHexString(getHeadStart())  
				+ ConverUtil.convertByteToHexString(getAddress())  
				+ ConverUtil.convertByteToHexString(getHeadEnd())  
				+ ConverUtil.convertByteToHexString(getcCode())  
				+ ConverUtil.convertByteToHexString(getLength())  
				+ ConverUtil.convertByteToHexString(getCmdCode())  
				+ ConverUtil.convertByteToHexString(getPdt())
				+ ConverUtil.convertByteToHexString(getCrc()) 
				+ ConverUtil.convertByteToHexString(getEnd()) ;
		 return bw;
    }
}
