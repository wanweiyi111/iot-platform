package com.hzyw.iot.netty.util;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;

public class NewReadHandlerUtil {

	public JSONObject b0Analysis(ByteBuf dataBuf) {
		/*byte[] temp = dataBuf.array();
		System.out.println(ByteUtils.toHexString(temp));*/
		byte[] temp1 = new byte[1];
		byte[] temp2 = new byte[6];
		byte[] temp3 = new byte[1];
		byte[] temp4 = new byte[1];
		byte[] temp5 = new byte[3];
		byte[] temp6 = new byte[2];
		byte[] temp7 = new byte[5];
		
		dataBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7);
		
		String RSUStatus =ByteUtils.toHexString(temp1);//RSU 主状态参数 0 表示正常，否则表示异常
		String RSUTerminalId =ByteUtils.toHexString(temp2);//PSAM 卡终端机编号
		String RSUAlgId = ByteUtils.toHexString(temp3);//算法标识
		String RSUManuID = ByteUtils.toHexString(temp4);//RSU 厂商代码
		String RSUIndividualID = ByteUtils.toHexString(temp5);//RSU 编号
		String RSUVersion = ByteUtils.toHexString(temp6);//RSU 软件版本号
		String Reserved = ByteUtils.toHexString(temp7);//保留字节。
		
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("RSUStatus", RSUStatus);
		jsonObj.put("RSUTerminalId", RSUTerminalId);
		jsonObj.put("RSUAlgId", RSUAlgId);
		jsonObj.put("RSUManuID", RSUManuID);
		jsonObj.put("RSUIndividualID", RSUIndividualID);
		jsonObj.put("RSUVersion", RSUVersion);
		jsonObj.put("Reserved", Reserved);
		
		return jsonObj;
	}

	public JSONObject b2Analysis(ByteBuf dataBuf) {
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2);
		String OBUID =ByteUtils.toHexString(temp1);//OBU 号
		String ErrorCode =ByteUtils.toHexString(temp2);//执行状态代码，0 表示正常
		if(!ErrorCode.equals("00")) {
			return null;
		}
		byte[] temp3 = new byte[8];
		byte[] temp4 = new byte[1];
		byte[] temp5 = new byte[1];
		byte[] temp6 = new byte[8];
		byte[] temp7 = new byte[4];
		byte[] temp8 = new byte[4];
		byte[] temp9 = new byte[1];
		byte[] temp10 = new byte[2];
		
		
		dataBuf.readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9).readBytes(temp10);
		
		String ContractProvider = ByteUtils.toHexString(temp3);//发行商代码
		String ContractType = ByteUtils.toHexString(temp4);//协约类型
		String ContractVersion = ByteUtils.toHexString(temp5);//协约版本
		String ContractSerialNumbe = ByteUtils.toHexString(temp6);//应用序列号，8 个字节，BCD 编码
		String ContractSignedDate = ByteUtils.toHexString(temp7);//协议签署日期，yyyymmdd
		String ContractExpiredDate = ByteUtils.toHexString(temp8);//协议过期日期，yyyymmdd
		String Equitmentstatus = ByteUtils.toHexString(temp9);//OBU 硬件版本
		String OBUStatus = ByteUtils.toHexString(temp10);//OBU 状态
		
		
		JSONObject jsonObj=new JSONObject();//data数据解析出的数据
		jsonObj.put("OBUID", OBUID);
		jsonObj.put("ErrorCode", ErrorCode);
		jsonObj.put("ContractProvider", ContractProvider);
		jsonObj.put("ContractType", ContractType);
		jsonObj.put("ContractVersion", ContractVersion);
		jsonObj.put("ContractSerialNumbe", ContractSerialNumbe);
		jsonObj.put("ContractSignedDate", ContractSignedDate);
		jsonObj.put("ContractExpiredDate", ContractExpiredDate);
		jsonObj.put("Equitmentstatus", Equitmentstatus);
		jsonObj.put("OBUStatus", OBUStatus);
		
		
		return jsonObj;
	}

	public JSONObject b3Analysis(ByteBuf dataBuf) {
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2);
		String OBUID =ByteUtils.toHexString(temp1);//OBU 号
		String ErrorCode =ByteUtils.toHexString(temp2);//执行状态代码，0 表示正常
		if(!ErrorCode.equals("00")) {
			return null;
		}
		byte[] temp3 = new byte[12];
		byte[] temp4 = new byte[2];
		byte[] temp5 = new byte[1];
		byte[] temp6 = new byte[1];
		byte[] temp7 = new byte[4];
		byte[] temp8 = new byte[1];
		byte[] temp9 = new byte[1];
		byte[] temp10 = new byte[2];
		byte[] temp11 = new byte[3];
		byte[] temp12 = new byte[16];
		byte[] temp13 = new byte[16];
		
		
		dataBuf.readBytes(temp3).readBytes(temp4)
		.readBytes(temp5).readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9).readBytes(temp10)
		.readBytes(temp11).readBytes(temp12).readBytes(temp13);
		
		String VehicleLicencePlateNumber = ByteUtils.toHexString(temp3);//OBU 记载的车牌号
		String VehicleLicencePlateColor = ByteUtils.toHexString(temp4);//车牌颜色
		String VehicleClass = ByteUtils.toHexString(temp5);//车辆类型
		String VehicleUserType = ByteUtils.toHexString(temp6);//车辆用户类型 
		String VehicleDimensions = ByteUtils.toHexString(temp7);//车辆尺寸
		String VehicleWheels = ByteUtils.toHexString(temp8);//车轮数
		String VehicleAxles = ByteUtils.toHexString(temp9);//车轴数
		String VehicleWheelBases = ByteUtils.toHexString(temp10);//轴距
		String VehicleWeightLimits = ByteUtils.toHexString(temp11);//车辆载重
		String VehicleSpecificInformation = ByteUtils.toHexString(temp12);//车辆特征描述，字符用 ASCII 编码表示
		String VehicleEngineNumber = ByteUtils.toHexString(temp13);//车辆发动机号 
		
		
		JSONObject jsonObj=new JSONObject();//data数据解析出的数据
		jsonObj.put("OBUID", OBUID);
		jsonObj.put("ErrorCode", ErrorCode);
		jsonObj.put("VehicleLicencePlateNumber", VehicleLicencePlateNumber);
		jsonObj.put("VehicleLicencePlateColor", VehicleLicencePlateColor);
		jsonObj.put("VehicleClass", VehicleClass);
		jsonObj.put("VehicleUserType", VehicleUserType);
		jsonObj.put("VehicleDimensions", VehicleDimensions);
		jsonObj.put("VehicleWheels", VehicleWheels);
		jsonObj.put("VehicleAxles", VehicleAxles);
		jsonObj.put("VehicleWheelBases", VehicleWheelBases);
		jsonObj.put("VehicleWeightLimits", VehicleWeightLimits);
		jsonObj.put("VehicleSpecificInformation", VehicleSpecificInformation);
		jsonObj.put("VehicleEngineNumber", VehicleEngineNumber);
		return jsonObj;
	}
	
	public JSONObject b4Analysis(ByteBuf dataBuf) {
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2);
		String OBUID =ByteUtils.toHexString(temp1);//OBU 号
		String ErrorCode =ByteUtils.toHexString(temp2);//执行状态代码，0 表示正常
		if(!ErrorCode.equals("00")) {
			return null;
		}
		byte[] temp3 = new byte[4];
		byte[] temp4 = new byte[28];
		byte[] temp5 = new byte[43];
		dataBuf.readBytes(temp3).readBytes(temp4).readBytes(temp5);
		
		String CardRestMoney = ByteUtils.toHexString(temp3);//卡余额，高字节在前
		String OBU0015 = ByteUtils.toHexString(temp4);//0015 文件
		String OBU0019 = ByteUtils.toHexString(temp5);//0019 文件
		
		JSONObject json0015=new JSONObject();
		json0015.put("cardID", OBU0015.substring(0, 16));
		json0015.put("cardType", OBU0015.substring(16, 18));
		json0015.put("cardVol", OBU0015.substring(18, 20));
		json0015.put("cardNetNo", OBU0015.substring(20, 24));
		json0015.put("cardNum", OBU0015.substring(24, 40));
		json0015.put("cardSignedDate", OBU0015.substring(40, 48));
		json0015.put("cardExpiredDate", OBU0015.substring(48, 56));
		System.out.println("0015文件解析:"+json0015);
		
		JSONObject json0019=new JSONObject();
		json0019.put("复合应用类型标识", OBU0019.substring(0, 2));
		json0019.put("记录长度", OBU0019.substring(2, 4));
		json0019.put("应用锁定标志", OBU0019.substring(4, 6));
		json0019.put("入口收费路网号", OBU0019.substring(6, 10));
		json0019.put("入口收费站号", OBU0019.substring(10, 14));
		json0019.put("入口收费车道号", OBU0019.substring(14, 16));
		json0019.put("入口时间", OBU0019.substring(16, 24));
		json0019.put("车型", OBU0019.substring(24, 26));
		json0019.put("入出口状态", OBU0019.substring(26, 28));
		json0019.put("标识站", OBU0019.substring(28, 46));
		json0019.put("收费员工号", OBU0019.substring(46, 52));
		json0019.put("入口班次", OBU0019.substring(52, 54));
		json0019.put("车牌号码", OBU0019.substring(54, 78));
		json0019.put("预留", OBU0019.substring(78, 86));
		System.out.println("0019文件解析:"+json0019);
		
		
		JSONObject jsonObj=new JSONObject();//data数据解析出的数据
		jsonObj.put("OBUID", OBUID);
		jsonObj.put("ErrorCode", ErrorCode);
		jsonObj.put("CardRestMoney", CardRestMoney);
		jsonObj.put("OBU0015", OBU0015);
		jsonObj.put("OBU0019", OBU0019);
		return jsonObj;
	}

	
	
	public JSONObject b5Analysis(ByteBuf dataBuf) {
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[1];
		dataBuf.readBytes(temp1).readBytes(temp2);
		String OBUID =ByteUtils.toHexString(temp1);//OBU 号
		String ErrorCode =ByteUtils.toHexString(temp2);//执行状态代码，0 表示正常
		if(!ErrorCode.equals("00")) {
			return null;
		}
		byte[] temp3 = new byte[7];
		byte[] temp4 = new byte[4];
		byte[] temp5 = new byte[2];
		byte[] temp6 = new byte[1];
		byte[] temp7 = new byte[4];
		byte[] temp8 = new byte[4];
		byte[] temp9 = new byte[4];
		byte[] temp10 = new byte[4];
		dataBuf.readBytes(temp3).readBytes(temp4).readBytes(temp5).readBytes(temp6)
		.readBytes(temp7).readBytes(temp8).readBytes(temp9).readBytes(temp10);
		
		
		String TransTime = ByteUtils.toHexString(temp3);//交易时间
		String PSAMTransSerial = ByteUtils.toHexString(temp4);//PSAM卡交易序号
		String ETCTradNo = ByteUtils.toHexString(temp5);//联机交易序号
		String TransType = ByteUtils.toHexString(temp6);//交易类型，CPU卡为9
		String CardRandom = ByteUtils.toHexString(temp7);//卡随机号，复合消费初始化产生的
		String CardRestMoney = ByteUtils.toHexString(temp8);//交易后卡余额
		String TAC = ByteUtils.toHexString(temp9);//TAC码
		String WrFileTime = ByteUtils.toHexString(temp10);//写文件时间，UNIX 格式
		
		JSONObject jsonObj=new JSONObject();//data数据解析出的数据
		jsonObj.put("OBUID", OBUID);
		jsonObj.put("ErrorCode", ErrorCode);
		jsonObj.put("TransTime", TransTime);
		jsonObj.put("PSAMTransSerial", PSAMTransSerial);
		jsonObj.put("ETCTradNo", ETCTradNo);
		jsonObj.put("TransType", TransType);
		jsonObj.put("CardRandom", CardRandom);
		jsonObj.put("CardRestMoney", CardRestMoney);
		jsonObj.put("TAC", TAC);
		jsonObj.put("WrFileTime", WrFileTime);
		return jsonObj;
	}

	

}
