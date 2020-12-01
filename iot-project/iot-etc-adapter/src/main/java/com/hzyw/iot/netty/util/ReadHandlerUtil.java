package com.hzyw.iot.netty.util;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;

public class ReadHandlerUtil {
	/**
	 * 解析b0的数据
	 */
		public JSONObject b0Analysis(ByteBuf dataBuf) {
			/*byte[] temp = dataBuf.array();
			System.out.println(ByteUtils.toHexString(temp));*/
			Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
			byte[] temp1 = new byte[1];
			byte[] temp2 = new byte[1];
			byte[] temp3 = new byte[1];
			dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3);
			int psamNumInt =Integer.parseInt(ByteUtils.toHexString(temp3));
			int sum = 0;
			for(int i=0;i<psamNumInt;i++) {
				sum = sum+7;
			}
			byte[] temp4 = new byte[sum];
			byte[] temp5 = new byte[1];
			byte[] temp6 = new byte[1];
			byte[] temp7 = new byte[3];
			byte[] temp8 = new byte[2];
			byte[] temp9 = new byte[1];
			byte[] temp10 = new byte[4];
			dataBuf.readBytes(temp4).readBytes(temp5).readBytes(temp6).readBytes(temp7)
			.readBytes(temp8).readBytes(temp9).readBytes(temp10);
			
			String code =ByteUtils.toHexString(temp1);//指令
			String rsuStatus =ByteUtils.toHexString(temp2);//路侧单元主状态参数；00H 表示正常，否则表示异常
			String psamNum = ByteUtils.toHexString(temp3);//当前路侧单元中 PSAM 数量，记为 n
			String pasmInfo = ByteUtils.toHexString(temp4);//#PSAM卡版本号+PSAM卡终端机编号，n为PSAM卡个数
			String rsuAlgId = ByteUtils.toHexString(temp5);//算法标识，默认填写 00H
			String rsuManuID = ByteUtils.toHexString(temp6);//路侧单元厂商代码
			String rsuID = ByteUtils.toHexString(temp7);//路侧单元编号
			String rsuVersion = ByteUtils.toHexString(temp8);//路侧单元软件版本号，采用本标准版本不低于 20 00H
			String workstatus = ByteUtils.toHexString(temp9);//工作模式返回状态，默认填写 00H
			String reserved = ByteUtils.toHexString(temp10);//保留字节 00000000
			
			dataMap.put("code", code);
			dataMap.put("rsuStatus", rsuStatus);
			dataMap.put("psamNum", psamNum);
			dataMap.put("psamInfo", pasmInfo);
			dataMap.put("rsuAlgId", rsuAlgId);
			dataMap.put("rsuManuID", rsuManuID);
			dataMap.put("rsuID", rsuID);
			dataMap.put("rsuVersion", rsuVersion);
			dataMap.put("workstatus", workstatus);
			dataMap.put("reserved", reserved);
			
			JSONObject jsonObj=new JSONObject(dataMap);
			
			return jsonObj;
		}

	/**
	 * 解析b2的数据
	 */
	public JSONObject b2Analysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3);
		String code =ByteUtils.toHexString(temp1);//指令
		String obuId =ByteUtils.toHexString(temp2);//车载单元MAC地址
		String errorCode = ByteUtils.toHexString(temp3);//执行状态代码，取值为“00”时有后续数据
		if(!errorCode.equals("00")) {
			return null;
		}
		byte[] temp4 = new byte[8];
		byte[] temp5 = new byte[1];
		byte[] temp6 = new byte[1];
		byte[] temp7 = new byte[8];
		byte[] temp8 = new byte[4];
		byte[] temp9 = new byte[4];
		byte[] temp10 = new byte[1];
		byte[] temp11 = new byte[2];
		dataBuf.readBytes(temp4).readBytes(temp5).readBytes(temp6)
		.readBytes(temp7).readBytes(temp8).readBytes(temp9)
		.readBytes(temp10).readBytes(temp11);
		
		
		String issuerIdentifier = ByteUtils.toHexString(temp4);//发行商代码
		String contractType = ByteUtils.toHexString(temp5);//协约类型
		String contractVersion = ByteUtils.toHexString(temp6);//协约版本
		String serialNumber = ByteUtils.toHexString(temp7);//合同序列号
		String dateofIssue = ByteUtils.toHexString(temp8);//启用日期
		String dateofExpire = ByteUtils.toHexString(temp9);//过期日期
		String equitmentCV = ByteUtils.toHexString(temp10);//设备类型及版本
		String obuStatus = ByteUtils.toHexString(temp11);//车载单元状态
		
		dataMap.put("code", code);
		dataMap.put("obuId", obuId);
		dataMap.put("errorCode", errorCode);
		dataMap.put("issuerIdentifier", issuerIdentifier);
		dataMap.put("contractType", contractType);
		dataMap.put("contractVersion", contractVersion);
		dataMap.put("serialNumber", serialNumber);
		dataMap.put("dateofIssue", dateofIssue);
		dataMap.put("dateofExpire", dateofExpire);
		dataMap.put("equitmentCV", equitmentCV);
		dataMap.put("obuStatus", obuStatus);
		
		
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}

	public JSONObject b4Analysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[1];
		byte[] temp4 = new byte[1];
		byte[] temp5 = new byte[4];
		byte[] temp6 = new byte[50];
		byte[] temp7 = new byte[63];
		byte[] temp8 = new byte[6];
		byte[] temp9 = new byte[3];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9);
		
		String code =ByteUtils.toHexString(temp1);//指令
		String obuId = ByteUtils.toHexString(temp2);//车载单元MAC地址
		String errorCode = ByteUtils.toHexString(temp3);//执行状态代码
		String transType =ByteUtils.toHexString(temp4);//交易类型（10H：复合交易）
		String cardRestMoney = ByteUtils.toHexString(temp5);//卡余额
		String issuerInfo = ByteUtils.toHexString(temp6);//卡片发行信息
		String lastStation = ByteUtils.toHexString(temp7);//上次过站信息（国标卡过站信息0019文件记录，不足63字节补充0处理）
		String ef04 = ByteUtils.toHexString(temp8);//读取EF04数据
		String len0008 = ByteUtils.toHexString(temp9);//读取0008数据
		
		
		dataMap.put("code", code);
		dataMap.put("obuId", obuId);
		dataMap.put("errorCode", errorCode);
		dataMap.put("transType", transType);
		dataMap.put("cardRestMoney", cardRestMoney);
		dataMap.put("issuerInfo", issuerInfo);
		dataMap.put("lastStation", lastStation);
		dataMap.put("ef04", ef04);
		dataMap.put("len0008", len0008);
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}

	public JSONObject b5Analysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[1];
		byte[] temp4 = new byte[6];
		byte[] temp5 = new byte[7];
		byte[] temp6 = new byte[1];
		byte[] temp7 = new byte[4];
		byte[] temp8 = new byte[2];
		byte[] temp9 = new byte[4];
		byte[] temp10 = new byte[4];
		byte[] temp11 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9)
		.readBytes(temp10).readBytes(temp11);
		
		String code =ByteUtils.toHexString(temp1);//指令
		String obuId = ByteUtils.toHexString(temp2);//车载单元MAC地址
		String errorCode = ByteUtils.toHexString(temp3);//执行状态代码
		String psamNo =ByteUtils.toHexString(temp4);//PSAM卡终端机编号
		String transTime = ByteUtils.toHexString(temp5);//交易时间，BCD编码，格式：YYYYMMDDhhmmss
		String transType = ByteUtils.toHexString(temp6);//交易类型，取值为0x09（复合消费），其他保留
		String tac = ByteUtils.toHexString(temp7);//交易认证码
		String iccPayserial = ByteUtils.toHexString(temp8);//CPU用户卡脱机交易序号
		String psamTransSerial = ByteUtils.toHexString(temp9);//PSAM卡终端交易序号
		String cardBalance = ByteUtils.toHexString(temp10);//交易后余额
		String keyType = ByteUtils.toHexString(temp11);//交易使用的密钥标识，0-3DES，4-SM4
		
		
		dataMap.put("code", code);
		dataMap.put("obuId", obuId);
		dataMap.put("errorCode", errorCode);
		dataMap.put("psamNo", psamNo);
		dataMap.put("transTime", transTime);
		dataMap.put("transType", transType);
		dataMap.put("tac", tac);
		dataMap.put("iccPayserial", iccPayserial);
		dataMap.put("psamTransSerial", psamTransSerial);
		dataMap.put("cardBalance", cardBalance);
		dataMap.put("keyType", keyType);
		
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}

	public JSONObject b3Analysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[1];
		byte[] temp4 = new byte[12];
		byte[] temp5 = new byte[2];
		byte[] temp6 = new byte[1];
		byte[] temp7 = new byte[1];
		byte[] temp8 = new byte[4];
		byte[] temp9 = new byte[1];
		byte[] temp10 = new byte[1];
		byte[] temp11 = new byte[2];
		byte[] temp12 = new byte[3];
		byte[] temp13 = new byte[16];
		byte[] temp14 = new byte[16];
		byte[] temp15 = new byte[3];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9)
		.readBytes(temp10).readBytes(temp11).readBytes(temp12).readBytes(temp13)
		.readBytes(temp14).readBytes(temp15);
		
		String code =ByteUtils.toHexString(temp1);//指令
		String obuId = ByteUtils.toHexString(temp2);//车载单元MAC地址
		String errorCode = ByteUtils.toHexString(temp3);//执行状态代码
		String vehicleLicencePlateNumber =ByteUtils.toHexString(temp4);//OBU记载的车牌号
		String vehicleLicencePlateColor = ByteUtils.toHexString(temp5);//车牌颜色
		String vehicleClass = ByteUtils.toHexString(temp6);//车辆类型
		String vehicleUserType = ByteUtils.toHexString(temp7);//车辆用户类型
		String vehicleDimensions = ByteUtils.toHexString(temp8);//车辆尺寸,長寬高
		String vehicleWheels = ByteUtils.toHexString(temp9);//车轮数
		String vehicleAxles = ByteUtils.toHexString(temp10);//车轴数
		String vehicleWheelBases = ByteUtils.toHexString(temp11);//轴距
		String vehicleWeightLimits = ByteUtils.toHexString(temp12);//车辆载重
		String vehicleSpecificInformation = ByteUtils.toHexString(temp13);//车辆特征描述
		String vehicleEngineNumber = ByteUtils.toHexString(temp14);//车辆发动机号
		String truckInfo = ByteUtils.toHexString(temp15);//货车信息
		
		dataMap.put("code", code);
		dataMap.put("obuId", obuId);
		dataMap.put("errorCode", errorCode);
		dataMap.put("vehicleLicencePlateNumber", vehicleLicencePlateNumber);
		dataMap.put("vehicleLicencePlateColor", vehicleLicencePlateColor);
		dataMap.put("vehicleClass", vehicleClass);
		dataMap.put("vehicleUserType", vehicleUserType);
		dataMap.put("vehicleDimensions", vehicleDimensions);
		dataMap.put("vehicleWheels", vehicleWheels);
		dataMap.put("vehicleAxles", vehicleAxles);
		dataMap.put("vehicleWheelBases", vehicleWheelBases);
		dataMap.put("vehicleWeightLimits", vehicleWeightLimits);
		dataMap.put("vehicleSpecificInformation", vehicleSpecificInformation);
		dataMap.put("vehicleEngineNumber", vehicleEngineNumber);
		dataMap.put("truckInfo", truckInfo);
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}

	public JSONObject d0Analysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[1];
		byte[] temp4 = new byte[1];
		byte[] temp5 = new byte[8];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5);
		
		String code =ByteUtils.toHexString(temp1);//指令
		String obuId = ByteUtils.toHexString(temp2);//车载单元MAC地址
		String frameType = ByteUtils.toHexString(temp3);//保留
		String nMark =ByteUtils.toHexString(temp4);//计算成功标记，0：成功，非0：定位失败
		String data = ByteUtils.toHexString(temp5);//前四个字节表示x方向坐标，后四字节表示y方向坐标，高位在前，低位在后，单位为厘米。
		
		
		dataMap.put("code", code);
		dataMap.put("obuId", obuId);
		dataMap.put("frameType", frameType);
		dataMap.put("nMark", nMark);
		dataMap.put("data", data);
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}

	public JSONObject bdAnalysis(ByteBuf dataBuf) {
		Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[1];
		byte[] temp3 = new byte[10];
		byte[] temp4 = new byte[1];
		byte[] temp5 = new byte[4];
		byte[] temp6 = new byte[8];
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6);
		
		String code =ByteUtils.toHexString(temp1);//指令
		String errorCode = ByteUtils.toHexString(temp2);//执行状态代码，0-执行成功；其他-执行失败
		String psamNo = ByteUtils.toHexString(temp3);//PSAM序列号
		String psamVersion =ByteUtils.toHexString(temp4);//PSAM版本号
		String areaCode = ByteUtils.toHexString(temp5);//PSAM应用区域标识前四个字节
		String randCode = ByteUtils.toHexString(temp6);//8字节PSAM随机数
		
		dataMap.put("code", code);
		dataMap.put("errorCode", errorCode);
		dataMap.put("psamNo", psamNo);
		dataMap.put("psamVersion", psamVersion);
		dataMap.put("areaCode", areaCode);
		dataMap.put("randCode", randCode);
		JSONObject jsonObj=new JSONObject(dataMap);
		return jsonObj;
	}
}
