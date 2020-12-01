package com.hzyw.iot.listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hzyw.iot.netty.client.EtcClient;
import com.hzyw.iot.netty.udpClient.GlobalInfo;
import com.hzyw.iot.netty.udpClient.NettyUdpClient;
import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.netty.util.NewReplyHandlerUtil;
import com.hzyw.iot.test.kafka.ProducerDemo;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

 
/**
 * 
 * 监听服务
 * 
 * @author Administrator
 *
 */
@Component
@Order(value=1)
public class ListenerService implements CommandLineRunner {
	@Autowired
    NettyUdpClient nettyTcpClient;
    @Value("${host}")
    private String HOST;
    @Value("${port}")
    private int PORT;
    NewReplyHandlerUtil replyUtil = new NewReplyHandlerUtil();//回复指令
	HeadHandlerUtil util =new HeadHandlerUtil();
    public static final TimedCache<String, String> timedCache1 = CacheUtil.newTimedCache(15000);//创建缓存，默认10秒过期(保证数据不完整后关闭连接)
    public final static TimedCache<String, String> timedCache2 = CacheUtil.newTimedCache(10000);
    
    int i=0;
   /* public void thread() {
    Thread thread = new Thread(){
    	
    	   public void run(){
    	
    	     System.out.println("Thread Running");
    	
    	   }
    	
    	};
    	
    	thread.start();
    }*/

    
    /**
     * 启动消费线程获取下发的指令
     */
    @Override
    public void run(String... args) throws Exception {
    	System.out.println("测试开始");
    	
    	/*//PLC下发关灯
    	  String json = "{\"data\":{\"methods\":[{\"method\": \"set_onoff\",\"in\": [{\"ab\": \"03\",\"code\": \"01\",\"level\": 100,\"onoff\": 1}]}],\"id\": \"1010-71d8537795b8f25e-2011-ffff-4130\",\"tags\": {\"agreement\": \"plc\"}},\"msgId\": \"1db179ce-c81e-4499-bff2-29e8a954af97\",     \"gwId\": \"2000-d8b38d288d431464-2011-ffff-08ed\",\"type\": \"request\",\"timestamp\": 1566205651 }";
    	JSONObject jsonObj=new JSONObject(json);
    	ProducerDemo.sendKafka("iot_topic_plcOrder", jsonObj.toString());
    	System.out.println("发送成功"); //日志查看是否有,请求执行成功!/调灯光    	    	
*/    	
    	/*//RSU旧版
    	 * EtcClient client = new EtcClient();
        try {
			//client.connect("112.74.55.86", 9527);
			client.connect("127.0.0.1", 9527);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	
    	//RSU新版
    	try {
			//启动客户端，发送数据
            nettyTcpClient.connect();
            //monitor();//实时监测OBU状态
            //schedule方法：定时任务三个参数：执行任务，起始时间，时间间隔
            Timer timer = new Timer("任务调度器");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("执行调度。。。1");
                    try {
						monitor();//监测
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//实时监测OBU状态
                }
            }, new Date(System.currentTimeMillis()), 12000);
            
            Thread.sleep(3000);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("执行调度。。。2");
                    i++;
                    try {
						heartbeat();//心跳
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }, new Date(System.currentTimeMillis()), 2000);
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    
    
    public void monitor() throws Exception {
    		System.out.println("监测OBU超时处理");
    		Iterator<CacheObj<String, String>> entries = ListenerService.timedCache1.cacheObjIterator();
    		List<String> listKey = new ArrayList<>();
    		List<String> overdueKey = new ArrayList<>();
    		//启动定时任务，每5秒检查一次过期
    		timedCache1.schedulePrune(5000);
    		timedCache2.schedulePrune(5000);
    		while(entries.hasNext()){
    		    CacheObj<String, String> entry = entries.next();
    		    String key1 = entry.getKey();
    		    timedCache1.get(key1);
    		    //System.out.println("ke1有效值:"+key1);
    		   /* String value = entry.getValue();
    		    System.out.println(key+":"+value);*/
    		    listKey.add(key1);
    		}
    		System.out.println("size:"+listKey.size());
    		System.out.println("timedCache2:"+timedCache2.size());
    		if(listKey.size()==1||listKey.size()>=1&&timedCache2.size()==0) {
    			int online = 0;
    		for(String attribute : listKey) {
		    	if(!attribute.equals(timedCache2.get(attribute))) {
		    		overdueKey.add(attribute);
		    		System.out.println("超时OBU1:"+attribute);
		    		offObu(attribute);//关闭流程
		    		//listKey.remove(attribute);
		    		timedCache1.remove(attribute);//清除缓存
		    		timedCache2.remove(attribute);//清除缓存
		    		
		    	}else {
		    		System.out.println("在线obuId1:"+attribute);
		    		online=1;
		    	}
		   }
    		if(online==0) {
    		Thread.sleep(2000);
    		offantenna();//关闭天线
    		}
    		return;
    	}
    		
    		Iterator<CacheObj<String, String>> entries2 = ListenerService.timedCache2.cacheObjIterator();
    		while(entries2.hasNext()){
    		    CacheObj<String, String> entry2 = entries2.next();
    		    String key2= entry2.getKey();
    		    timedCache2.get(key2);//有效时间还存在
    		    /*String value2 = entry2.getValue();
    		    System.out.println(key2+":"+value2);*/
    		    
    		    for(String attribute : listKey) {
    		    	//System.out.println("拿到所有OBUID:"+attribute);
    		    	if(!attribute.equals(timedCache2.get(attribute))) {
    		    		overdueKey.add(attribute);
    		    		System.out.println("超时OBU2:"+attribute);
    		    		offObu(attribute);//关闭流程
    		    		timedCache1.remove(attribute);//清除缓存
    		    		timedCache2.remove(attribute);//清除缓存
    		    	}else {
    		    		System.out.println("在线obuId2:"+attribute);
    		    	}
    		   }
    		}
    	
    }
    
    
    public void heartbeat() throws Exception {
    	ChannelHandlerContext ctx =  GlobalInfo.CHANNEL_INFO_MAP.get("etc");
    		if(i==101) {
    			i=1;
    		}
            JSONObject jsonObj=new JSONObject();
            jsonObj.put("proxyId", "SC_0001");	
            jsonObj.put("cmd", "heartbeat");
            jsonObj.put("frameId", i);
            System.out.println("发送心跳："+jsonObj);
            HeadHandlerUtil util = new HeadHandlerUtil();
            byte[] byte00 = {0};
            byte[]  byte1 = util.getPdt(jsonObj.toString().getBytes(),byte00);
           // System.out.println("发送心跳："+util.convertByteToHexString(byte1));
            sendBuf(ctx,byte1);
            //Thread.sleep(3000);
            
    }
    
    //关闭流程
    public void offObu(String obuId) throws Exception {
    	System.out.println("关闭流程"+obuId);
    	ChannelHandlerContext ctx =  GlobalInfo.CHANNEL_INFO_MAP.get("etc");
    	//c2终止
		byte[] c2Replyb5 =replyUtil.c2Reply(obuId,"00");//c2指令     1-重新OBU流程，0-终止OBU流程。
		String sm4EncryptB5 = util.sm4Encrypt("5358474C4554435041524B9529140773",c2Replyb5);//sm4加密
		byte[] cxJsonByteB5= util.cxJson(sm4EncryptB5);//json数据，以及补充00
		sendBuf(ctx,cxJsonByteB5);
    }
    
    //关闭天线
    public void offantenna() throws Exception {
    	System.out.println("关闭天线");
    	ChannelHandlerContext ctx =  GlobalInfo.CHANNEL_INFO_MAP.get("etc");
		byte[] c2Data = replyUtil.c2Reply("ffffffff","00");//获取c2数据(开关天线)  01开，00关
		String sm4EncryptC2 = util.sm4Encrypt("5358474C4554435041524B9529140773",c2Data);//sm4加密
        byte[] cxJsonByteC2= util.cxJson(sm4EncryptC2);//json数据，以及补充00
        //System.out.println(new String(cxJsonByteC2));
		sendBuf(ctx,cxJsonByteC2);
    }
    
    
    /**
	 * 发送数据
	 */
	public void sendBuf(ChannelHandlerContext ctx,byte[] dataByte) {
		ByteBuf byteBuf= ctx.alloc().buffer();
		byteBuf.writeBytes(dataByte);
		ctx.writeAndFlush(byteBuf);
	}
}