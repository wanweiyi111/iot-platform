package com.hzyw.iot.commandManager.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hzyw.iot.commandManager.CommandManager;
import com.hzyw.iot.commandManager.CommandManagerImpl;
import com.hzyw.iot.commandManager.commandbuidler.CommandBuidlerFactory;
import com.hzyw.iot.commandManager.data.CommandTasker;
/**
 * 测试
 * @author admin
 * 
 */
public class Test {
	/**
	 * 命令组装器测试
	 * @throws InterruptedException
	 */
	public static void test1() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl();
		Map<String,String> map = new HashMap<String,String>();
		map.put("appName", "live");
		//map.put("input", "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0");
		map.put("input", "rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream");
		map.put("output", "rtmp://192.168.3.183:1935/rtmp");
		//map.put("codec", "h264");
		//map.put("fmt", "flv");
		//map.put("fps", "25");
		//map.put("rs", "640x360");
			//map.put("-y", "");  //-f flv -r 25 -g 25
		map.put("twoPart", "1");  //逻辑自定义
		// 执行任务，id就是appName，如果执行失败返回为null
		CommandTasker task = manager.start(map);
		System.out.println(task.getId());
		// 通过id查询
		CommandTasker info = manager.query(task.getId());
		System.out.println(info);
		// 查询全部
		Collection<CommandTasker> infoList = manager.queryAll();
		System.out.println(infoList);
		Thread.sleep(1000*60*30);
		// 停止id对应的任务
		 manager.stop(task.getId());
	}
	/**
	 * 默认方式，rtsp->rtmp转流单个命令测试
	 * @throws InterruptedException
	 */
	public static void test2() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl();
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//默认方式发布任务
		manager.start("live1", "ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/1010-d8b38d288d431464-3001-ffff-37cf");
		manager.start("live2", "ffmpeg -i rtsp://admin:Admin123@192.168.3.249:554/h264/ch1/main/av_stream -vcodec copy -f flv -s 640x360 -an rtmp://192.168.3.183:1935/rtmplive/test2");
		Thread.sleep(1000*60);
		new CommandManagerImpl().stopAll();
		System.out.println("---------->>>----all stop?--------");
		Thread.sleep(1000*10);
		// 停止全部任务
		manager.stopAll();
	}
	/**
	 * 完整ffmpeg路径测试
	 * @throws InterruptedException
	 */
	public static void test4() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl();
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//默认方式发布任务
		manager.start("tomcat", "D:/TestWorkspaces/FFmpegCommandHandler/src/cc/eguid/FFmpegCommandManager/ffmpeg/ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat",true);
		
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}
	
	/**
	 * rtsp-rtmp转流多任务测试
	 * @throws InterruptedException
	 */
	public static void test3() throws InterruptedException{
		CommandManager manager = new CommandManagerImpl();
		// -rtsp_transport tcp 
		//测试多个任何同时执行和停止情况
		//false表示使用配置文件中的ffmpeg路径，true表示本条命令已经包含ffmpeg所在的完整路径
		manager.start("tomcat", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat",false);
		manager.start("tomcat1", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat1",false);
		manager.start("tomcat2", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat2",false);
		manager.start("tomcat3", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat3",false);
		manager.start("tomcat4", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat4",false);
		manager.start("tomcat5", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat5",false);
		manager.start("tomcat6", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat6",false);
		manager.start("tomcat7", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat7",false);
		manager.start("tomcat8", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat8",false);
		manager.start("tomcat9", "ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat9",false);
		
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}
	
	/**
	 * 测试流式命令行构建器
	 * @throws InterruptedException
	 */
	public static void testStreamCommandAssmbly() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl();
		manager.start("test1", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}
	/**
	 * 测试任务中断自动重启任务
	 * @throws InterruptedException 
	 */
	public static void testBroken() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl();
		manager.start("test1", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
		manager.destory();
	}
	/**
	 * 批量测试任务中断自动重启任务
	 * @throws InterruptedException 
	 */
	public static void testBrokenMuti() throws InterruptedException {
		CommandManager manager = new CommandManagerImpl();
		manager.start("test1", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test1"));
		manager.start("test2", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test2"));
		manager.start("test3", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test3"));
		manager.start("test4", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test4"));
		manager.start("test5", CommandBuidlerFactory.createBuidler()
				.add("ffmpeg").add("-i","rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov")
//				.add("-rtsp_transport","tcp")
				.add("-vcodec","copy")
				.add("-acodec","copy")
				.add("-f","flv")
				.add("-y").add("rtmp://106.14.182.20:1935/rtmp/test5"));
		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
		manager.destory();
	}
	
	public static void main(String[] args) throws InterruptedException {
		//test1();
 		test2();
//		test3();
//		test4();
//		testStreamCommandAssmbly();
//		testBroken();
		//testBrokenMuti();
 		
        //注册一个 ShutdownHook
        //ShutdownSampleHook thread=new ShutdownSampleHook(Thread.currentThread());
        //Runtime.getRuntime().addShutdownHook(thread);
        //System.exit(0); //会主动触发hook , ctral + c ,kill id
        //System.out.println("------yibu.....");
        
	}
	
}
class ShutdownSampleHook extends Thread {
    private Thread mainThread;
    @Override
    public void run() {
        System.out.println("2: Shut down signal received.");
        mainThread.interrupt();//给主线程发送一个中断信号
        try {
        	System.out.println("等待执行完毕.....");
        	new CommandManagerImpl().stopAll();
        	Thread.currentThread().sleep(1000*15);
            mainThread.join(); //等待 mainThread 正常运行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("5: Shut down complete.");
    }

    public ShutdownSampleHook(Thread mainThread) {
        this.mainThread=mainThread;
        Runtime.getRuntime().addShutdownHook(this);
    }
}
