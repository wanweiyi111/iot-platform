package com.hzyw.iot.utils;

import com.hzyw.iot.vo.dataaccess.DevSignlResponseDataVO;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.netty.channelhandler.ChannelManagerHandler;
import com.hzyw.iot.util.ByteUtils;
import com.hzyw.iot.util.constant.ConverUtil;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.ModbusInfo;

import cn.hutool.core.convert.Convert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * 初始化PLC配置： 
 * 1 集中器配置：向集中器写入经纬度信息 
 * 2 设置集中器时间：手动同步集中器时间 
 * 3 清除节点 
 * 4 删除flash节点 
 * 5 开始组网（等待节点数*20秒） 
 * 6 查询组网(当全部节点都组网成功) (略) 
 * 7 停止组网
 *  8 存储节点 
 *  9 下发节点 
 *  10 配置节点（等待节点数*30秒） 
 *  11 查询节点  (略)
 *  
 * @author Administrator
 *
 */
public class PlcProtocolsUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(PlcProtocolsUtilsTest.class);
	public static Map<String, String> gloable = new HashMap<String, String>();

	 

	/**
	 * @param modbusInfo
	 * @param address
	 * @param cCode
	 * @param cmdCode
	 * @param _PDT
	 * @throws Exception
	 */
	public static void setProtocalsAuto(ModbusInfo modbusInfo,byte[] address,byte[] cCode,byte[] cmdCode,byte[] _PDT) throws Exception{
		// 1,devId,cCode,cmdCode
		modbusInfo.setAddress(address);//设备ID
		modbusInfo.setcCode(cCode);// 广播
		modbusInfo.setCmdCode(cmdCode);
		// 2,Pdt部分
		modbusInfo.setPdt(_PDT);
	}
	public static void main(String[] a) {
		//testOpenLight();
		//testGetBW();  //PDT为空
		testSendDown(new MessageVO()); //构造带PDT内容的报文
		/*testGetBW(); //手工构造一条没有PDT的报文
		init1_config_jzq(null,"000000000100");
		init8_sendNode();//下发节点
		ini9_configNode(null,"000000000100"); //配置节点
		getSplSTR();*/
		
		
		
		//protocols_API_F7_Response(null,null);  //数据上报报文解析调试
	}

	 

	/**
	 *  以下TEST  已通过验证
	 *  下发VO ==>报文
	 *  
	 * @return
	 */
	public static <T> boolean testSendDown(MessageVO<T> messageVO) {
		boolean excuSeccess = true;
		String response = "";
		ByteBuf byteBuf = null;
		try {
			System.out.println("=====>>> 构造 开灯 报文...===============");
			 
			//680000000000016803094200000000000001002016
			ModbusInfo modbusInfo_42h = new ModbusInfo();
			// 1,devId,cCode,cmdCode
			modbusInfo_42h.setAddress(ConverUtil.hexStrToByteArr("000000000100"));//设备ID
			modbusInfo_42h.setcCode(ConverUtil.hexStrToByteArr("02"));// 广播  01单点      02 组      03广播
			modbusInfo_42h.setCmdCode(ConverUtil.hexStrToByteArr("42"));
			
			// 2,Pdt部分
			String nodeID = "000000000001"; //无节点？   0000020004ee     000002000001   0000020004E8
			String operator_AB = "03"; 		// 01H-a灯           02H -B灯                   03H -A,B灯
			int light_value = 0;       		// 范围00H~C8H(十进制：0~200)，对应亮度为0~100%
			byteBuf = Unpooled.buffer(8); 	// 必须等于要拼接的内容的长度，否则byteBuf.array()得到的长度就不准确
			byteBuf.writeBytes(ConverUtil.hexStrToByteArr(nodeID));
			byteBuf.writeBytes(ConverUtil.hexStrToByteArr(operator_AB));
			byteBuf.writeByte(light_value); 
			if (byteBuf.hasArray()) {
				byte[] aa = byteBuf.array();
				System.out.println("pdt.lenght = " + aa.length);
				System.out.println("pdt = " + ConverUtil.convertByteToHexString(aa));
				modbusInfo_42h.setPdt(aa); 		// 内容：计算nodeId + operator_AB(A/B/所有) + 调光值
			}
			if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
			
			// 3,长度
			modbusInfo_42h.resetLength();  
			// 4,CRC计算
			modbusInfo_42h.resetCrc();
			 
			logger.info("     >>>您要的报文=" + ConverUtil.convertByteToHexString(modbusInfo_42h.getNewFullData()));
			System.out.println("====>>>init ModbusInfo :====");
			System.out.println(modbusInfo_42h.toString());
			System.out.println("====<<<===");
			
			String temp = ConverUtil.convertByteToHexString(modbusInfo_42h.getNewFullData());
			StringBuffer sb = new StringBuffer();
			int p=0;
			for(int i=0;i<temp.length();i++){
				if(i%2 == 0){
					sb.append(","+temp.charAt(i));
				}else{
					sb.append(temp.charAt(i));
				}
				p++;
			}
			System.out.println("===>"+sb.toString());
			//ctxWriteAndFlush(ctx,modbusInfo,"删除FLASH节点");  
		} catch (Exception e) {
			e.printStackTrace();
			if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
		}
		return excuSeccess;

	
	}
	
	public static <T> boolean testGetBW() {  //无PDT的报文
		boolean excuSeccess = true;
		String response = "";
		ByteBuf byteBuf = null;
		try {
			System.out.println("=====>>> 构造 没有PDT的报文 报文...===============");
  			ModbusInfo modbusInfo_42h = new ModbusInfo();
			// 1,devId,cCode,cmdCode
			modbusInfo_42h.setAddress(ConverUtil.hexStrToByteArr("000000000100"));//设备ID
			modbusInfo_42h.setcCode(ConverUtil.hexStrToByteArr("03"));// 广播
			modbusInfo_42h.setCmdCode(ConverUtil.hexStrToByteArr("97"));
			
			// 2,Pdt部分
			 
			byte[] aa = new byte[0];
			modbusInfo_42h.setPdt(aa); 	
			modbusInfo_42h.resetLength();  
			modbusInfo_42h.resetCrc();
			
			//logger.info("     >>>---------=68 00 00 00 00 00 01 68 00 01 63 35 16");
			logger.info("     >>>您要的报文=" + ConverUtil.convertByteToHexString(modbusInfo_42h.getNewFullData()));
			System.out.println("====>>>init ModbusInfo :====");
			System.out.println(modbusInfo_42h.toString());
			System.out.println("====<<<===");
			  
			
			String temp = ConverUtil.convertByteToHexString(modbusInfo_42h.getNewFullData());
			StringBuffer sb = new StringBuffer();
			int p=0;
			for(int i=0;i<temp.length();i++){
				if(i%2 == 0){
					sb.append(","+temp.charAt(i));
				}else{
					sb.append(temp.charAt(i));
				}
				p++;
			}
			System.out.println("===>"+sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return excuSeccess;

	
	}
	public static void getSplSTR(){
		String tst = "680000000001006800508e236cf618f600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080a08328e16";
		StringBuffer sp = new StringBuffer();
		for(int i=0 ;i<tst.length();i++){
			if(i%2 == 0){
				sp.append(","+tst.charAt(i));
			}else{
				sp.append( tst.charAt(i));
			}
		}
		System.out.println("========" +sp.toString());
	}

	public static <T> boolean init8_sendNode() { //下发节点
		boolean excuSeccess = true;
		String response = "000000000100";
		ByteBuf byteBuf = null;
		try {
			ModbusInfo modbusInfo = new ModbusInfo();
			String plc_SN = "000000000100";
			logger.info("=====>>>initstep(" + plc_SN+ ") 下发节点...==96,00=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("96")); 
			
			// 获取设备信息   通过chennelID  ==> devinfo
		 
			
			// PDT  节点列表
			int pdtLen = 8 ;//一个节点占8个字节
			//if(nodelist != null && nodelist.size() > 0){
				byte[] temp1 = new byte[pdtLen];
				if(true){
					 byteBuf = Unpooled.buffer(8);
					//for(int i = 0; i < nodelist.size(); i++){
 						int len = 8;
						String nodeid = "0000020004ee";  //6
						int groupid = 1;                 //1
						String devCode = "12"; //1
				        byteBuf.writeBytes(ConverUtil.hexStrToByteArr(nodeid))  //节点ID
				        		.writeBytes(ByteUtils.varIntToByteArray(groupid)) //组号
				        		.writeBytes(ConverUtil.hexStrToByteArr(devCode));  //设备码
					//}
					  temp1 = byteBuf.array();
			        if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
				}
				modbusInfo.setPdt(temp1);
				 
			//}else{
				//
				 
			//}
			  
			logger.info("     >>>您要的报文=" + ConverUtil.convertByteToHexString(modbusInfo.getNewFullData())); 
			  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return excuSeccess;

	
	}

	public static boolean init1_config_jzq(ChannelHandlerContext ctx, String plc_SN ) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo(); //8e表示命令码 00表示请求  80表示响应
		try {
			//	获取设备信息   通过chennelID  ==> devinfo
			//Map<String, String> def_attributers = IotInfoConstant.allDevInfo.get(getPort(ctx.channel())).get(plc_SN + "_defAttribute");//从自定义的字段里面获取值
			logger.info("=====>>>initstep1(" + plc_SN + ")集中器配置参数...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));  
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("8e")); 
			// 获取设备信息  构造PDT  
			/*
				 名称	说明                                                                                       	长度
				经度	取小数后2位(-180.00 - +180.00) 再*100 整型	2 Bs     如：+9068
				纬度	取小数后2位 (-90.00 - +90.00) 再*100整型	    2 Bs     如：-XXXX
				时区	-11 - +12整型	                            1 B
				短信中心号	如: +8613010888500 字符串型	14 Bs    --不考虑
				管理员1	如:13812345678 字符串型	11 Bs        --不考虑
				管理员2	如:13812345678 字符串型	11 Bs    	 --不考虑
				操作员1	如:13812345678 字符串型	11 Bs     	 --不考虑
				操作员2	如:13812345678 字符串型	11 Bs        --不考虑
				操作员3	如:13812345678 字符串型	11 Bs        --不考虑
				短信操作回复	0 否  1是			1 B     	 --不考虑
				光控时段	前2Byte：开始 时分                    2Bs                     
				                     后2Byte：结束 时分 	    2 Bs
			*/
	    	String plc_cfg_step1_longitude = "9096";  //经度
	    	String plc_cfg_step1_latitude = "4521";  //纬度
	    	String plc_cfg_step1_sq= "+8"; //"-8"; //时区        转化成整形表示
	    	String plc_cfg_step1_gksd_start= "8:10"; //"8:10"; //光控时段-开始时分
	    	int gksd_start_h = Integer.parseInt(plc_cfg_step1_gksd_start.split(":")[0]);
	    	int gksd_start_s = Integer.parseInt(plc_cfg_step1_gksd_start.split(":")[1]);
	    	String plc_cfg_step1_gksd_end=  "8:50"; //"8:10"; //光控时段  结束时分
	    	int gksd_end_h = Integer.parseInt(plc_cfg_step1_gksd_end.split(":")[0]);
	    	int gksd_end_s = Integer.parseInt(plc_cfg_step1_gksd_end.split(":")[1]);
 	    	
			byte[] temp1 = new byte[2]; 
			temp1 = ByteUtils.varIntToByteArray(9068); //经度
			byte[] temp2 = new byte[2]; 
			temp2 = ByteUtils.varIntToByteArray(-2536); //纬度
			byte[] temp3 = new byte[1];
			temp3 = ByteUtils.varIntToByteArray(-10);  //时区
			byte[] temp4_10 = new byte[14+11*5+1];
			for(int p=0;p<temp4_10.length;p++){ //不需要给值，报文的相应位置默认给0
				temp4_10[p]= 0;
			}
			byte[] temp11_0 = new byte[1];
			byte[] temp11_1 = new byte[1];
			temp11_0 = ByteUtils.varIntToByteArray(gksd_start_h); //时
			temp11_1 = ByteUtils.varIntToByteArray(gksd_start_s);   //分
			byte[] temp12_0 = new byte[1];   //时
			byte[] temp12_1 = new byte[1];   //时
			temp12_0 = ByteUtils.varIntToByteArray(gksd_end_h); //时
			temp12_1 = ByteUtils.varIntToByteArray(gksd_end_s);   //分
			 
			int len = temp1.length + temp2.length + temp3.length + temp4_10.length + 2 + 2;   	
			logger.info(">>>请求设备,cmdCode=8e_00  , 应该是len=" + len);  
	        ByteBuf byteBuf = Unpooled.buffer(len);
	        byteBuf.writeBytes(temp1).writeBytes(temp2).writeBytes(temp3).writeBytes(temp4_10).writeBytes(temp11_0).writeBytes(temp11_1)
	         			.writeBytes(temp12_0).writeBytes(temp12_1); //CRC计算，根据CRC所在报文位置--》headStart所有的数据
	        byte[] temp = byteBuf.array();
	        if(byteBuf != null)ReferenceCountUtil.release(byteBuf);
	        modbusInfo.setPdt(temp);
 			//	响应
			logger.info(">>>请求设备,cmdCode=8e_00  ,byteMsg=" + ConverUtil.convertByteToHexString(modbusInfo.getNewFullData()) );
			logger.info(">>>请求设备,cmdCode=8e_00  ,byteMsg.len=" + modbusInfo.getNewFullData().length );
			 
			 
			ctxWriteAndFlush(ctx,modbusInfo,"删除FLASH节点");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")响应设备,cmdCode=8e_00,exception ！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	
	}
	
	/**
	 * 配置过程,指令响应后进入此处理
	 * 
	 * @param ctx
	 * @param modbusInfo
	 * @return
	 */
	 

	public static boolean init2_config_setTime(ChannelHandlerContext ctx,String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo_8c_00 = new ModbusInfo();
		try {
			//	获取设备信息   通过chennelID  ==> devinfo

			logger.info("=====>>>initstep(" + plc_SN+ ")集中器设置时钟...===============");
			modbusInfo_8c_00.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo_8c_00.setcCode(ConverUtil.hexStrToByteArr("00"));  
			modbusInfo_8c_00.setCmdCode(ConverUtil.hexStrToByteArr("8c")); 
			//   构造PDT 7个字节 
			/*
				 年，月，日，周，时，分，秒
			*/
			long timestamp = System.currentTimeMillis();//输入定为时间戳
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(timestamp);
			int hour = ca.get(Calendar.DATE);  //时
			int min = ca.get(Calendar.MINUTE); //分
			int sec = ca.get(Calendar.SECOND);//秒
			int week = ca.get(Calendar.DAY_OF_WEEK);//周 默认从1开始  是否要-1？
			int day = ca.get(Calendar.DAY_OF_MONTH);//日
			int month = ca.get(Calendar.MONTH);//月   默认0开始  是否要 +1？
			int year = ca.get(Calendar.YEAR);//年
 	    	
			byte[] temp1 = new byte[7]; 
			temp1[0] = (byte)year; 
			temp1[1] = (byte)month; 
			temp1[2] = (byte)day; 
			temp1[3] = (byte)week; 
			temp1[4] = (byte)hour; 
			temp1[5] = (byte)min; 
			temp1[6] = (byte)year; 
			modbusInfo_8c_00.setPdt(temp1);

			ctxWriteAndFlush(ctx,modbusInfo_8c_00,"集中器设置时钟");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo_8c_00.getAddress_str() + ")请求设备,,cmdCode="+modbusInfo_8c_00.getCmdCode_str()
        				+"ccode="+modbusInfo_8c_00.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	}

  
	public static boolean init3_config_cleanNode(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			//	删除所有节点，只需要指定集中器ID即可
			logger.info("=====>>>initstep(" + plc_SN+ ")清除节点...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03"));  //01删某个ID，02删除一组  03删除所有
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("99")); 
			//   构造PDT 6个字节 
			byte[] temp1 = new byte[6]; 
			modbusInfo.setPdt(temp1);

			ctxWriteAndFlush(ctx,modbusInfo,"清除节点");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	
	}
	
	public static void ctxWriteAndFlush(ChannelHandlerContext ctx,  ModbusInfo modbusInfo,String logmsg) throws Exception{
		logger.info(" byteMsg=" + ConverUtil.convertByteToHexString(modbusInfo.getNewFullData()));
		 
	}

	public static boolean init4_config_delFlash(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			//	删除所有节点，只需要指定集中器ID即可
			logger.info("=====>>>initstep(" + plc_SN+ ")删除FLASH节点...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("69")); 
			byte[] temp1 = new byte[0]; 
			modbusInfo.setPdt(temp1);//注意这里，没有PDT的时候 是否需要给值0

			ctxWriteAndFlush(ctx,modbusInfo,"删除FLASH节点");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	
	}
	 

	/**
	 * 
	 * 68 00 00 00 00 00 01 68 00 02 62 02 37 16
	 * 
	 * @param ctx
	 * @param modbusInfo
	 * @return
	 */
	public static boolean init5_CfgNetwork(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			//	获取设备信息
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
			int port = insocket.getPort();
			Map<String, Object> attributers = IotInfoConstant.allDevInfo.get(port).get(modbusInfo.getAddress_str() + "_defAttribute");
					
			logger.info("=====>>>initstep(" + plc_SN+ ") 开始组网...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("62")); 
			// PDT 组网个数
			byte[] groupNum = new byte[1];
			int x = Integer.parseInt((String)attributers.get(IotInfoConstant.dev_plc_cfg_step5_groupAtuo));
			logger.info("     >>>组网个数 = " + x);
			groupNum[0] = (byte) x;  
			modbusInfo.setPdt(groupNum);
			  
			ctxWriteAndFlush(ctx,modbusInfo,"开始组网");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
		
	}
	
	/**
	 * 停止组网
	 * 
	 * @param ctx
	 * @param plc_SN
	 * @return
	 */
	public static boolean init6_stopCfgNetwork(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN+ ") 停止组网...==63,00=============");
			Thread.currentThread().sleep(1000*5);//等待5秒钟后在做停止组网操作 ，不管组网是否达到要求的数量，这里暂时做成强制停止组网
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("63")); 
			// PDT 
			byte[] temp1 = new byte[0]; 
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx,modbusInfo,"停止组网");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	}
	public static boolean init7_saveNode(ChannelHandlerContext ctx, String plc_SN) {  //未完成。。。。。
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN+ ") 存储节点...==66,00=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("66")); 
			// PDT 
			byte[] temp1 = new byte[0]; 
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx,modbusInfo,"存储节点");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
		
	}
	 
	public static boolean ini9_configNode(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN+ ") 配置节点...==98,03=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03")); 
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("98")); 
			// PDT 
			byte[] temp1 = new byte[0]; 
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx,modbusInfo,"配置节点");
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode="+modbusInfo.getCmdCode_str()
        				+"ccode="+modbusInfo.getcCode_str()+",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
		
	}
	
	
	public static boolean protocols_API_F7_Response(ChannelHandlerContext ctx, ModbusInfo modbusInfo) {
		boolean excuSeccess = true;
		//final ModbusInfo modbusInfo = new ModbusInfo(); 
		try {
			//68,00,00,00,00,01,00,68,04,4a,f7,04,00,00,02,00,04,ee,0a,01,09,33,00,3f,00,1e,14,00,00,00,00,00,10,00,14,d1,00,00,00,00,00,00,00,00,00,00,00,00,00,00,10,02,00,b6,00,00,00,00,00,00,00,00,00,00,00,00,00,00,10,02,01,48,00,00,00,00,00,00,00,00,00,00,00,00,de,16
			//新设备
			//68,00,00,00,00,01,00,68,04,4a,f7,    04, 00,00,02,00,04,ee,0a,01,09,33,00,3f,00,1e,14,00,00,00(18),00,00,10,00,14,d1,00,00,00,00,00,00,00,00,00,00,00,00(18),00,00,10,02,00,b6,00,00,00,00,00,00,00,00,00,00,00,00(18),00,00,10,02,01,48,00,00,00,00,00,00,00,00,00,00,00,00(18),de,16
			//test 
			String hexbw = "6800000000010068044af7040000020004ee0a010933003f001e140000000000100014d10000000000000000000000000000100200b6000000000000000000000000000010020148000000000000000000000000de16";
			byte[] tb = ConverUtil.hexStrToByteArr(hexbw);
			ByteBuf testbytebuf = Unpooled.wrappedBuffer(tb,0,tb.length); //wrappedBuffer  支持0拷贝（软拷贝）  buffer()同样支持；注意 byteBuf.readBytes引发了内存拷贝操作
			modbusInfo = new ModbusInfo(testbytebuf);
			
			logger.info("=====>>>protocols_F7  状态数据上报   ...===============");
			byte[] allpdt = modbusInfo.getPdt();
			byte[] temp0_bytes = {allpdt[0]}; 
		    long node_len = ByteUtils.byteArrayToLong(temp0_bytes);     //节点总数
		    
		    System.out.println("-------节点总数--------" + node_len);
			ByteBuf allPdtBuf = Unpooled.wrappedBuffer(allpdt,0,allpdt.length);//所有节点
		    System.out.println("-------aa-------" + allPdtBuf.readerIndex());

		    allPdtBuf.readByte();//移动一个字节
		 
		    System.out.println("-------bb-------" + allPdtBuf.readerIndex());
			System.out.println("------- 切分到的节点--------" + ConverUtil.convertByteToHexString(allPdtBuf.array())   );
			ByteBuf byteBuf = allPdtBuf.copy();  //拷贝readindex开始到后面的字节 ，深度拷贝，即硬拷贝
			System.out.println("------- 切分到的节点--------" + ConverUtil.convertByteToHexString(byteBuf.array())   );
			
			//判断是新设备还是老设备 
			boolean isOld = true;
			if((allpdt.length-1)%18 == 0){
				//说明是新设备
				isOld = false;
				System.out.println("-------isOld---新-----" + isOld);
			}
			if((allpdt.length-1)%27 == 0){
				//说明是老设备
				isOld = true;
				System.out.println("-------isOld----老----" + isOld);
			}
			
			 System.out.println("-------3--------" ); 
			//需要定义14个临时变量  27个字节
			byte[] temp1 = new byte[6]; //节点ID
			byte[] temp2 = new byte[1]; //设备码  列如:19是100W
			byte[] temp3 = new byte[1]; //在线状态   00H：不在线;    01H：在线
			byte[] temp4 = new byte[2]; //输入电压V   单位0.1V
			byte[] temp5 = new byte[2]; //输入电流A   单位mA
			byte[] temp6 = new byte[2]; //输入功率
			byte[] temp7 = new byte[1];  //功率因素
			byte[] temp8 = new byte[2]; //状态                  二进制位来表示
			byte[] temp9 = new byte[1];  //异常状态
			
			byte[] temp10 = new byte[1];  //灯具温度   新程序才有temp10~14的数据
			byte[] temp11 = new byte[2];  //输出功率
			byte[] temp12 = new byte[2];  //灯具运行时长
			byte[] temp13 = new byte[2];  //电能
			byte[] temp14 = new byte[2];  //故障时长
			for(int i=0;i < node_len; i++){ //一个节点
				if(isOld){
					byteBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4).readBytes(temp5)
					.readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9).readBytes(temp10)
					.readBytes(temp11).readBytes(temp12).readBytes(temp13).readBytes(temp14);
				}else{
					byteBuf.readBytes(temp1).readBytes(temp2).readBytes(temp3).readBytes(temp4).readBytes(temp5)
					.readBytes(temp6).readBytes(temp7).readBytes(temp8).readBytes(temp9);
				}
				
				String _temp1 = ConverUtil.convertByteToHexString(temp1);//节点ID
				String _temp2 = ConverUtil.convertByteToHexString(temp2);//设备码  列如:19是100W
				String _temp3 = ConverUtil.convertByteToHexString(temp3);//在线状态   00H：不在线;    01H：在线
				double _temp4 = ByteUtils.byteArrayToLong(temp4) * 0.1;// 输入电压  ,单位0.1V ,   
				long _temp5 = ByteUtils.byteArrayToLong(temp5);  //整形            输入电流
				double _temp6 = ByteUtils.byteArrayToLong(temp6) * 0.1;  //整形            输入功率  单位0.1W
				long _temp7 = ByteUtils.byteArrayToLong(temp7);  //整形            功率因素
				String _temp8 = ConverUtil.byteArrToBinStr(temp8); //状态   转化位二进制串？
				parseSingl(_temp8,temp2);//解析二进制数据     信号数据，在里面构造一条信号数据上报
				String _temp9 = ConverUtil.convertByteToHexString(temp9);//异常状态
				if(isOld){
					long _temp10 = ByteUtils.byteArrayToLong(temp10);//灯具温度 ,   温度范围从-127℃~+127℃，单位为1℃
					double _temp11 = ByteUtils.byteArrayToLong(temp11);//输出功率      ,   单位0.1W
					long _temp12 = ByteUtils.byteArrayToLong(temp12);  //灯具运行时长
					double _temp13 = ByteUtils.byteArrayToLong(temp13);  //电能          0.1KW
					long _temp14 = ByteUtils.byteArrayToLong(temp14); //故障时长   1小时
				}
				//构造一条状态数据上报
				
	    	}
			if (byteBuf != null)ReferenceCountUtil.release(byteBuf);//释放内存
			
			 
			//解析PDT
			//state(_temp8);//解析二进制数据
			
 		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")响应设备,cmdCode=F7,exception ！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}
	
	public static long _6f = 110;
	public static long _70 = 112;
	public static long _7f = 127;
	public static void parseSingl(String _temp8,byte[] plc_node_devCode){ //dev_plc_node_devCode ="plc_node_devCode" 设备码
		//2个字节，通过二进制 0/1来表示
		/*1、路灯电源（设备码：00H~6FH）
		bit15~bit9：bit15~bit9：0x00,为老程序，没有以下红色数据，0x88，则为新程序，将上报以下红色数据
		bit8：温度过高报警位  bit7：温度过低报警位
		bit6：无法启动报警位  bit5：输出短路报警位
		bit4：输出开路报警位  bit3：功率过高报警位
		bit2：输入电压过高报警位
		bit1：输入电压过低报警位
		以上bit1~bit8：0表示无报警，1表示有报警。
		bit0：电源状态位；0：电源关，1：电源开。

		2、路灯控制器（设备码：70H~7FH）
		bit15~bit8表示B灯的状态：
		bit15：继电器失效报警位 bit14：过载报警位
		bit13：欠载报警位       bit12：过压警位
		bit11：欠压报警位       bit10：过流报警位
		bit9： 欠流报警位 
		以上报警位： 0表示无报警，1表示有报警      
		bit8：继电器状态位；0表示关，1表示开
		------------------------------------
		bit7~bit0表示A灯的状态：
		bit7：继电器失效报警位  bit6：过载报警位
		bit5：欠载报警位        bit4：过压警位
		bit3：欠压报警位        bit2：过流报警位
		bit1：欠流报警位 
		以上报警位： 0表示无报警，1表示有报警      
		bit0：继电器状态位；0表示关，1表示开
        */
		long long_plc_node_devCode = ByteUtils.byteArrayToLong(plc_node_devCode);
		if(long_plc_node_devCode >= 0 && long_plc_node_devCode <=_6f){ //表示路灯电源信息
			//bit15~bit9：0x00,为老程序，没有以下红色数据，0x88   前面已经根据长度计算出新老程序了，这里不需要
			 
		}else if(long_plc_node_devCode >= _70 && long_plc_node_devCode <=_7f){ //表示路灯控制器信息
			
		}
 
		System.out.println("temp =" + _temp8);
	}
	

}
