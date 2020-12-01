/*
 * AlarmJavaDemoApp.java
 */

package com.hzyw.iot.sdk;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.json.JSONObject;
import com.hzyw.iot.utils.IotInfoConstantN;
 
/**
 * 加载海康SDK并自动注册设备自动布防
 */
public class InitSdk{
	
	private static Logger logger = LoggerFactory.getLogger(InitSdk.class);
 
    public static void init() {
    	logger.info("=====hksdk init=====" );
    	IotInfoConstantN.initData();
    	//获取配置
    	//String[] deviceIps = ResourcesConfig.deviceIp;//获取设备数组  连接注册信息    设备属性信息从盒子上报，有必要则可以这里定义设备序号上去作为匹配依据
    	String picPath = ResourcesConfig.picPath;
    	
    	logger.info("=====alldevinfo.size=====" + IotInfoConstantN.allDevInfo.size() );//多设备验证
    	 
    	for(JSONObject devObject : IotInfoConstantN.allDevInfo ){
    		String deviceIp = (String)devObject.get(IotInfoConstantN.deviceIp);
    		String deviceConnUser = (String)devObject.get(IotInfoConstantN.connUser);
    		String deviceConnPwd = (String)devObject.get(IotInfoConstantN.connPasswd);
    		String deviceConnPort = (String)devObject.get(IotInfoConstantN.connPort);
    		logger.info("===deviceIp==========" + deviceIp);
    		logger.info("===deviceConnUser===="+deviceConnUser);
    		logger.info("===deviceConnPwd====="+deviceConnPwd);
    		logger.info("===deviceConnPort===="+deviceConnPort);
    		logger.info("===picPath===="+picPath);
    		IotSdk workdevice1 = new IotSdk(deviceIp,deviceConnUser,deviceConnPwd,deviceConnPort,picPath,devObject);
        	try {
    			workdevice1.sdk_init();
    			workdevice1.sdk_login();
    			//布防
    			workdevice1.sdk_bf();
    			//监听
    			//workdevice1.sdk_loginOut();
    		} catch (Exception e) {
    			logger.info("==="+deviceIp+"=====hksdk init失败=====");
    			logger.error("==="+deviceIp+"=====hksdk init失败=====",e);
     			workdevice1.sdk_loginOut();
    		}
    	}
    	
    	/*for(int p = 0; p < deviceIps.length; p++){    //暂时可以简单支持多个捉拍机
    		IotSdk workdevice1 = new IotSdk(deviceIps[p],ResourcesConfig.user[p],ResourcesConfig.password[p],
    				ResourcesConfig.port[p],picPath);
        	try {
    			workdevice1.sdk_init();
    			workdevice1.sdk_login();
    			//布防
    			workdevice1.sdk_bf();
    			//监听
    			//workdevice1.sdk_loginOut();
    		} catch (Exception e) {
    			logger.info("==="+deviceIps[p]+"=====hksdk init发生异常====="+e.getMessage());
    			logger.error("==="+deviceIps[p]+"=====hksdk init发生异常=====");
     			workdevice1.sdk_loginOut();
    		}
    	}*/
    	while(true){
    		logger.info("=====hksdk 布防线程激活中=====" );
			try {
				Thread.sleep(1000*50);
			} catch (InterruptedException e) {
				logger.error("=====hksdk 布防线程保活发生异常=====",e);
			}
		}
    }
    
    
}
