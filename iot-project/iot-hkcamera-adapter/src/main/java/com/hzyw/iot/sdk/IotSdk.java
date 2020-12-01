package com.hzyw.iot.sdk;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
//import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import net.sf.json.JSONObject;

//import alarmjavademo.HCNetSDK;
//import alarmjavademo.AlarmJavaDemoView.FGPSDataCallback;
import com.hzyw.iot.sdk.linux.HCNetSDK;
import com.hzyw.iot.sdk.linux.HCNetSDK.NET_DVR_ALARMER;
import com.hzyw.iot.sdk.linux.HCNetSDK.RECV_ALARM;

public class IotSdk {
	private static Logger logger = LoggerFactory.getLogger(IotSdk.class);
	HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();// 设备登录信息
	HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();// 设备信息
	String m_sDeviceIP;// 已登录设备的IP地址
	String m_sUsername;// 设备用户名
	String m_sPassword;// 设备密码
	String m_port; // 设备端口
	String m_pic_path = "./pic/"; //默认当前目录下的pic文件夹下

	int lUserID;// 用户句柄
	int lAlarmHandle;// 报警布防句柄
	int lListenHandle;// 报警监听句柄

	public static FMSGCallBack fMSFCallBack;// 报警回调函数实现。
	public static FMSGCallBack_V31 fMSFCallBack_V31;// 报警回调函数实现
	
	JSONObject devObject;

	//public static FGPSDataCallback fGpsCallBack;// GPS信息查询回调函数实现
	public IotSdk() {
	}

	public IotSdk(String _ip, String _user, String _pwd, String _port,String path,JSONObject devObject) {
		lUserID = -1;
		lAlarmHandle = -1;
		lListenHandle = -1;
		fMSFCallBack = null;
		fMSFCallBack_V31 = null;
		//fGpsCallBack = null;

		this.m_sDeviceIP = _ip;
		this.m_sUsername = _user;
		this.m_sPassword = _pwd;
		this.m_port = _port;
		this.m_pic_path = path;//图片保存路径
		this.devObject = devObject;
	}
	
	public String getLoginfo(){
		return "===["+this.m_sDeviceIP+"]";
	}

	/**
	 * 初始化SDK
	 */
	public void sdk_init() throws Exception {
		boolean initSuc = hCNetSDK.NET_DVR_Init();
		if (initSuc != true) {
			logger.info(getLoginfo()+"===初始化失败！");
			throw new RuntimeException("===初始化失败！");
		}

		//有可能LINUX版本的SO里没有实现？定义并验证下看
		HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struGeneralCfg = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
		struGeneralCfg.byAlarmJsonPictureSeparate = 1; // 控制JSON透传报警数据和图片是否分离，0-不分离，1-分离（分离后走COMM_ISAPI_ALARM回调返回）
		struGeneralCfg.write();
		
		if (!hCNetSDK.NET_DVR_SetSDKLocalCfg(17, struGeneralCfg.getPointer())) {//win版本的  这里没有，我在hcnetsdk.java里自己定义了，经过验证
		//if (!hCNetSDK.NET_DVR_SetSDKInitCfg(17, struGeneralCfg.getPointer())) { //发生异常了，linux换了个接口？还是说不需要这部分实现？
			logger.info(getLoginfo()+"===NET_DVR_SetSDKLocalCfg失败.");
			throw new RuntimeException("===NET_DVR_SetSDKLocalCfg失败.");
		}
		logger.info(getLoginfo()+"===sdk初始化成功！");
		if(hCNetSDK.NET_DVR_SetExceptionCallBack_V30(-1,-1,new FMSGCallBack_exception_V31(m_sDeviceIP),null)){
			logger.info(getLoginfo()+"===设置连接异常监听成功！");
		}else{
			logger.info(getLoginfo()+"===设置连接异常监听失败！");
		}
		
		//hCNetSDK.NET_DVR_SetLogToFile(boolean bLogEnable , String  strLogDir, boolean bAutoDel );
		hCNetSDK.NET_DVR_SetLogToFile(true, "./logauto", false );
		 
	}

	/**
	 * 根据IP，端口，用户名，密码 登录设备
	 */
	public void sdk_login() throws Exception {
		if (lUserID > -1) {
			// 先注销
			hCNetSDK.NET_DVR_Logout(lUserID);
			lUserID = -1;
			// return false;
		}

		// 注册
		m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
		System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

		m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
		System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

		m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
		System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
		m_strLoginInfo.wPort = (short) Integer.parseInt(this.m_port);
		m_strLoginInfo.bUseAsynLogin = 0; // 是否异步登录：0- 否，1- 是
		m_strLoginInfo.write();
		NativeLong _lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo.getPointer(), m_strDeviceInfo.getPointer()); 
		lUserID = _lUserID.intValue();
		if (lUserID == -1) {
			logger.info(getLoginfo()+"===sdk注册失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
			throw new RuntimeException("===sdk注册失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			logger.info(getLoginfo()+"===注册成功！");
			
		}
	}

	/**
	 * 注销
	 */
	public void sdk_loginOut() {
		// 报警撤防
		if (lAlarmHandle > -1) {
			if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(new NativeLong(lAlarmHandle))) { 
				logger.info(getLoginfo()+"===撤防失败！");
			} else {
				logger.info(getLoginfo()+"===撤防成功！");
				lAlarmHandle = -1;
			}
		}

		// 注销
		if (lUserID > -1) {
			if (hCNetSDK.NET_DVR_Logout(lUserID)) {
				logger.info(getLoginfo()+"===注销成功！");
				lUserID = -1;
			}
		}
	}

	/**
	 * 布防
	 */
	public void sdk_bf() throws Exception {
		if (lUserID == -1) {
			logger.info(getLoginfo()+"===请先注册！");
			throw new RuntimeException("===请先注册！");
		}
		//linux版本不支持如下接口，未发现有例子可参考
		if (lAlarmHandle < 0)// 尚未布防,需要布防
		{
			if (fMSFCallBack_V31 == null) {
				fMSFCallBack_V31 = new FMSGCallBack_V31();
				Pointer pUser = null;
				if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
					logger.info(getLoginfo()+"设置回调函数失败!");
				}else{
					logger.info(getLoginfo()+"设置回调函数成功!");
				}
			}
			HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
			m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
			m_strAlarmInfo.byLevel = 1;// 智能交通布防优先级：0- 一等级（高），1- 二等级（中），2-
										// 三等级（低）
			m_strAlarmInfo.byAlarmInfoType = 1;// 智能交通报警信息上传类型：0-
												// 老报警信息（NET_DVR_PLATE_RESULT），1-
												// 新报警信息(NET_ITS_PLATE_RESULT)
			m_strAlarmInfo.byDeployType = 1; // 布防类型(仅针对门禁主机、人证设备)：0-客户端布防(会断网续传)，1-实时布防(只上传实时数据)
			m_strAlarmInfo.write();
			lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
			if (lAlarmHandle == -1) {
				logger.info(getLoginfo()+"布防失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
				throw new RuntimeException("布防失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
			} else {
				logger.info(getLoginfo()+"布防成功！");
			}
		}
	}

	/**
	 * 撤防
	 */
	public void sdk_bf_cancel() {
		// 报警撤防
		if (lAlarmHandle > -1) {
			if (hCNetSDK.NET_DVR_CloseAlarmChan_V30(new NativeLong(lAlarmHandle))) {
				JOptionPane.showMessageDialog(null, "撤防成功");
				lAlarmHandle = -1;
			}
		}
	}

	public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 { 
		// 报警信息回调函数
		public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen,
				Pointer pUser) {
			AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
			return true;
		}

		@Override
		public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, RECV_ALARM pAlarmInfo, int dwBufLen,
				Pointer pUser) {
			//linux写法
			AlarmDataHandle(lCommand.intValue(), pAlarmer, pAlarmInfo.getPointer(), dwBufLen, pUser);
		}
	}
	public class FMSGCallBack_exception_V31 implements HCNetSDK.FExceptionCallBack {
		String deviceIp;
		public FMSGCallBack_exception_V31(){}
		public FMSGCallBack_exception_V31(String deviceIp){
			this.deviceIp =deviceIp;
		}
		@Override
		public void invoke(int dwType, NativeLong lUserID, NativeLong lHandle, Pointer pUser) {
			logger.info(getLoginfo()+"=======FExceptionCallBack异常！！(设备网络SDK使用手册查看相应dwType)============" );
		} 
	}

	public class FMSGCallBack implements HCNetSDK.FMSGCallBack {
		// 报警信息回调函数
		public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen,
				Pointer pUser) {
			//win上的写法
			AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
		}

		@Override
		public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, RECV_ALARM pAlarmInfo, int dwBufLen,
				Pointer pUser) {
			//linux上的写法
			AlarmDataHandle(lCommand.intValue(), pAlarmer, pAlarmInfo.getPointer(), dwBufLen, pUser);
		}
	}

	public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen,
			Pointer pUser) {
		//跟踪相应IP的设备的状态
		Date today = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String currentDate = dateFormat.format(today);
		StringBuffer loginfoSb = new StringBuffer();
		loginfoSb.append("/deviceIp=").append(m_sDeviceIP);
		try {
			String sAlarmType = new String();
			// DefaultTableModel alarmTableModel = ((DefaultTableModel)
			// jTableAlarm.getModel());//获取表格模型
			String[] newRow = new String[3];
			// 报警时间
			String[] sIP = new String[2];
			sAlarmType = new String("lCommand=0x") + Integer.toHexString(lCommand);
			loginfoSb.append(sAlarmType);
 			//判断断开网络后这里是否有响应监听 如果没有找HK研发确认   --有
			//判断网络断开 然后再连接上是否还需要重注册布防   --接上网线后会不影响之前的登陆和布防
			
			// lCommand是传的报警类型
			switch (lCommand) {
			case HCNetSDK.COMM_ALARM_V40: //报警信息主动上传
				loginfoSb.append("/报警异常1");
				HCNetSDK.NET_DVR_ALARMINFO_V40 struAlarmInfoV40 = new HCNetSDK.NET_DVR_ALARMINFO_V40();
				struAlarmInfoV40.write();
				Pointer pInfoV40 = struAlarmInfoV40.getPointer();
				pInfoV40.write(0, pAlarmInfo.getByteArray(0, struAlarmInfoV40.size()), 0, struAlarmInfoV40.size());
				struAlarmInfoV40.read();

				switch (struAlarmInfoV40.struAlarmFixedHeader.dwAlarmType) {
				case 0:
					struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.setType(HCNetSDK.struIOAlarm.class);
					struAlarmInfoV40.read();
					sAlarmType = sAlarmType + new String("：信号量报警") + "，" + "报警输入口："
							+ struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.struioAlarm.dwAlarmInputNo;
					break;
				case 1:
					sAlarmType = sAlarmType + new String("：硬盘满");
					break;
				case 2:
					sAlarmType = sAlarmType + new String("：信号丢失");
					break;
				case 3:
					struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.setType(HCNetSDK.struAlarmChannel.class);
					struAlarmInfoV40.read();
					int iChanNum = struAlarmInfoV40.struAlarmFixedHeader.ustruAlarm.sstrualarmChannel.dwAlarmChanNum;
					sAlarmType = sAlarmType + new String("：移动侦测") + "，" + "报警通道个数：" + iChanNum + "，" + "报警通道号：";

					for (int i = 0; i < iChanNum; i++) {
						byte[] byChannel = struAlarmInfoV40.pAlarmData.getByteArray(i * 4, 4);

						int iChanneNo = 0;
						for (int j = 0; j < 4; j++) {
							int ioffset = j * 8;
							int iByte = byChannel[j] & 0xff;
							iChanneNo = iChanneNo + (iByte << ioffset);
						}

						sAlarmType = sAlarmType + "+ch[" + iChanneNo + "]";
					}

					break;
				case 4:
					sAlarmType = sAlarmType + new String("：硬盘未格式化");
					break;
				case 5:
					sAlarmType = sAlarmType + new String("：读写硬盘出错");
					break;
				case 6:
					sAlarmType = sAlarmType + new String("：遮挡报警");
					break;
				case 7:
					sAlarmType = sAlarmType + new String("：制式不匹配");
					break;
				case 8:
					sAlarmType = sAlarmType + new String("：非法访问");
					break;
				}

				//newRow[0] = dateFormat.format(today);
				// 报警类型
				newRow[1] = sAlarmType;
				// 报警设备IP地址
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				newRow[2] = sIP[0];

				loginfoSb.append("/"+Arrays.toString(newRow));
				// alarmTableModel.insertRow(0, newRow);
				//给马乐上报一个信号类型的数据  他要提供接口，如果他不提供，这个信息打印到日志里即可以供未来跟踪
				break;
			case HCNetSDK.COMM_ALARM_V30: //报警信息主动上传
				loginfoSb.append("/报警异常2");
				HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
				strAlarmInfoV30.write();
				Pointer pInfoV30 = strAlarmInfoV30.getPointer();
				pInfoV30.write(0, pAlarmInfo.getByteArray(0, strAlarmInfoV30.size()), 0, strAlarmInfoV30.size());
				strAlarmInfoV30.read();
				switch (strAlarmInfoV30.dwAlarmType) {
				case 0:
					sAlarmType = sAlarmType + new String("：信号量报警") + "，" + "报警输入口："
							+ (strAlarmInfoV30.dwAlarmInputNumber + 1);
					break;
				case 1:
					sAlarmType = sAlarmType + new String("：硬盘满");
					break;
				case 2:
					sAlarmType = sAlarmType + new String("：信号丢失");
					break;
				case 3:
					sAlarmType = sAlarmType + new String("：移动侦测") + "，" + "报警通道：";
					for (int i = 0; i < 64; i++) {
						if (strAlarmInfoV30.byChannel[i] == 1) {
							sAlarmType = sAlarmType + "ch" + (i + 1) + " ";
						}
					}
					break;
				case 4:
					sAlarmType = sAlarmType + new String("：硬盘未格式化");
					break;
				case 5:
					sAlarmType = sAlarmType + new String("：读写硬盘出错");
					break;
				case 6:
					sAlarmType = sAlarmType + new String("：遮挡报警");
					break;
				case 7:
					sAlarmType = sAlarmType + new String("：制式不匹配");
					break;
				case 8:
					sAlarmType = sAlarmType + new String("：非法访问");
					break;
				}
				//newRow[0] = dateFormat.format(today);
				// 报警类型
				newRow[1] = sAlarmType;
				// 报警设备IP地址
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				newRow[2] = sIP[0];
				// alarmTableModel.insertRow(0, newRow);
				loginfoSb.append("/"+Arrays.toString(newRow));
				break;
 
			case HCNetSDK.COMM_ALARM_TFS:
				try {
					loginfoSb.append("/停车上报");
					HCNetSDK.NET_DVR_TFS_ALARM strTFSAlarmInfo = new HCNetSDK.NET_DVR_TFS_ALARM();
					strTFSAlarmInfo.write();
					Pointer pTFSInfo = strTFSAlarmInfo.getPointer();
					pTFSInfo.write(0, pAlarmInfo.getByteArray(0, strTFSAlarmInfo.size()), 0, strTFSAlarmInfo.size());
					strTFSAlarmInfo.read();
					loginfoSb.append("/字节流解析完毕");
					String srtPlate = "";
					//srtPlate = new String(strTFSAlarmInfo.struPlateInfo.sLicense, "GBK").trim(); // 车牌号码
					//srtPlate = strTFSAlarmInfo.struPlateInfo.sLicense;   win
					byte[] srtPlate_ = strTFSAlarmInfo.struPlateInfo.sLicense;
					srtPlate = new String(srtPlate_, "GBK").trim();
					srtPlate = srtPlate.replaceAll("蓝", "");
					srtPlate = srtPlate.replaceAll("绿", "");
					sAlarmType = sAlarmType + "：交通取证报警信息，违章类型：" + strTFSAlarmInfo.dwIllegalType + "，车牌号码：" + srtPlate
							+ "，车辆出入状态：" + strTFSAlarmInfo.struAIDInfo.byVehicleEnterState;
					
					// ==================需要打印以下关键信息=====================
					// 泊位号
					byte[] tempNo = strTFSAlarmInfo.byParkingSerialNO;
					String _tempNo = new String(tempNo, "GBK").trim();
					sAlarmType = sAlarmType + ",车位号：" + _tempNo;
	
					// 车道号
					// 车牌号  srtPlate
					// 设备 摄像头编号 可以在控制台定义
					byte[] devCodeByte = strTFSAlarmInfo.byDeviceID;
					String devCode = new String(devCodeByte, "GBK").trim();
					sAlarmType = sAlarmType + ",设备编号：" + devCode;
	
					//设备序号 设备自带不能更改
					int dwSeriaNo = strTFSAlarmInfo.dwSerialNo;
					
					// 抓拍时间
					int a = strTFSAlarmInfo.dwAbsTime;     //假设a是返回的Abstime
			        String strTimeYear = Integer.toString((a>>26)+2000);
			        String strTimeMonth = Integer.toString((a>>22)&15);
			        String strTimeDay = Integer.toString((a>>17)&31);
			        String strTimeHour = Integer.toString((a>>12)&31);
			        String strTimeMinute = Integer.toString((a>>6)&63);
			        String strTimeSecond = Integer.toString((a>>0)&63);
			        String cvtime = strTimeYear +"-"+ strTimeMonth + "-" + strTimeDay + " " +strTimeHour + ":" + strTimeMinute + ":" + strTimeSecond;
			        //logger.info(getLoginfo()+"时间："+strTimeYear +"-"+ strTimeMonth + "-" + strTimeDay + " " +strTimeHour + ":" + strTimeMinute + ":" + strTimeSecond);
			        //logger.info(getLoginfo()+"--1" + a);
			        long cvtimelong = dateFormat.parse(cvtime).getTime()/1000;
			        //logger.info(getLoginfo()+"--2" + cvtimelong);  //验证看是否相等一会
			        
					// 捉拍图片
					newRow[0] = dateFormat.format(today);
					// 报警类型
					newRow[1] = sAlarmType;
					// 报警设备IP地址
					sIP = new String(strTFSAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2);
					newRow[2] = sIP[0];
					loginfoSb.append("/>>>"+Arrays.toString(newRow)+">>>/");
	
					
					//获取车头还是车尾的字段标识，如果是车尾 说明停反了 （验证看停反了是否能上报消息）；上报入场和出场的时候增加违章标记即可
					int b = strTFSAlarmInfo.byVehicleHeadTailStatus;  //dataExceptionFlag  =-1 表示停反了的异常；把此字段推送上去即可
					logger.info(getLoginfo()+"-------COMM_ALARM_TFS/上报消息---(0-保留 1-车头 2-车尾)----" + b);
					String dataExceptionFlag = b!=1?"-1":"";//不等于车头 都算是异常的
					loginfoSb.append("/pic_num="+strTFSAlarmInfo.dwPicNum);
					//获取图片并写入指定文件夹
					String picPaths = "";
					for(int i=0;i<strTFSAlarmInfo.dwPicNum;i++) //图片数量
	                {
	                    if(strTFSAlarmInfo.struPicInfo[i].dwDataLen>0)
	                    {
	                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
	                        String newName = sf.format(new Date());
	                        FileOutputStream fout;
	                        try {
	                        	//存储图片路径
	                        	//linux: /iot/iot-project/pic/
	                        	String saveTopath = m_pic_path==null?"./pic/":m_pic_path;
	                            String filename = saveTopath+ sIP[0] + "_"
	                                    + newName+"_type["+strTFSAlarmInfo.struPicInfo[i].byType+"]_ParkVehicle.jpg";
	                            if(i == 0){
	                            	picPaths = filename;
	                            }else{
	                            	picPaths = picPaths + "," + filename;
	                            }
	                            
	                            logger.info(filename);
	                            fout = new FileOutputStream(filename);
	                            //将字节写入文件
	                            long offset = 0;
	                            ByteBuffer buffers = strTFSAlarmInfo.struPicInfo[i].pBuffer.getByteBuffer(offset, strTFSAlarmInfo.struPicInfo[i].dwDataLen);
	                            byte [] bytes = new byte[strTFSAlarmInfo.struPicInfo[i].dwDataLen];
	                            buffers.rewind();
	                            buffers.get(bytes);
	                            fout.write(bytes);
	                            fout.close();
	                        } catch (Exception e) {
	                        	loginfoSb.append(";图片解析异常:"+e.getMessage());
	                        	logger.error(loginfoSb.toString(),e);
	                        }
	                    }
	                }
					
					// 推送消息到ETC应用服务
					String berthCode = _tempNo;//车位号/泊位号
					String plateNum =  srtPlate; //车牌号
					String parkCode =  _tempNo;//"P15781234764444"; //车位编号  _tempNo 等价于泊位号  在页面上定义
					String time="";
					//deviceCode 设备编号    在页面上定义   问下马了 其他字段如设备相关的字段能否上报上去
					//sIP  ip地址
					//imges filename1,filename2
					String recordId = UUID.randomUUID().toString();//消息标识
					if(strTFSAlarmInfo.struAIDInfo.byVehicleEnterState == 1){//入
						time =  "\"enterTime\":" + cvtimelong ; 
	 			    	String json = "{\"bizContent\": {\"dataExceptionFlag\":  "+dataExceptionFlag+" ,\"enterImages\": \""+picPaths+"\",\"deviceCode\": \""+devCode+"\",\"recordId\": \""+recordId+"\",\"berthCode\": \""+berthCode+"\", \"plateNum\": \""+plateNum+"\","+time+"},"
	 			    			+ "\"parkCode\": \""+parkCode+"\", \"serviceName\": \"enter\"}";
	 					loginfoSb.append("/cartNo="+plateNum+"/"+cvtime+"进场/postjson=" + json);
	 					DoPostUtil.postEnter(json);
					}else if(strTFSAlarmInfo.struAIDInfo.byVehicleEnterState == 2){
						time =  "\"exitTime\":" + cvtimelong;
	 			    	String json = "{\"bizContent\": {\"dataExceptionFlag\":  "+dataExceptionFlag+" ,\"exitImages\": \""+picPaths+"\",\"deviceCode\": \""+devCode+"\",\"recordId\": \""+recordId+"\",\"berthCode\": \""+berthCode+"\", \"plateNum\": \""+plateNum+"\","+time+"},"
	 			    			+ "\"parkCode\": \""+parkCode+"\", \"serviceName\": \"exit\"}";
	 			    	loginfoSb.append("/cartNo="+plateNum+"/"+cvtime+"出场/postjson=" + json);
	 			    	DoPostUtil.postExit(json); 
					}else{
						time =  "\"触发时间\":" + cvtimelong;
						String json = "{\"bizContent\": {\"dataExceptionFlag\":  "+dataExceptionFlag+" ,\"deviceCode\": \""+devCode+"\",\"recordId\": \""+recordId+"\",\"berthCode\": \""+berthCode+"\", \"plateNum\": \""+plateNum+"\","+time+"},"
	 			    			+ "\"parkCode\": \""+parkCode+"\", \"serviceName\": \"???\"}";
						loginfoSb.append("/cartNo="+plateNum+"/"+cvtime+"未知进出标识!/postjson=" + json);
					}
					loginfoSb.append("/postjson success!");
				} catch (Exception e) {
					loginfoSb.append("/解析上报消息发生异常:"+e.getMessage());
                	logger.error(loginfoSb.toString()+"/解析上报消息发生异常:",e);
				}
				logger.info(loginfoSb.toString());
				break;
			default:
				newRow[0] = dateFormat.format(today);
				newRow[1] = sAlarmType;
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				newRow[2] = sIP[0];
 				loginfoSb.append("/"+Arrays.toString(newRow));
				break;
			}
		} catch (Exception ex) {
			loginfoSb.append("/监听回调异常:"+ex.getMessage());
        	logger.error(loginfoSb.toString()+"/监听回调异常:",ex);
		}
	}

}
