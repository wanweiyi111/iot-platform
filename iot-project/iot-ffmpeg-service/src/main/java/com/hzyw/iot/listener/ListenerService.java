package com.hzyw.iot.listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
//import com.hzyw.iot.config.NettyConfig;

 
/**
 * 启动服务，定义一个钩子，当服务正常退出的时候，关闭所有打开的ffmpeg连接，否则下次再连接的时候无法建立连接的哦
 * @author zhu
 *
 */
@Component
public class ListenerService implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerService.class);
    private static ExecutorService singleThreadExecutor = Executors.newFixedThreadPool(2);
    
    //@Autowired
    //private NettyConfig nettyConfig;
    
    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Camera Service Start..");
        new ShutdownSampleHook(Thread.currentThread());
    }
}

class ShutdownSampleHook extends Thread {
    private Thread mainThread;
    @Override
    public void run() {
        System.out.println("监听到服务停止信号.....");
        mainThread.interrupt();//给主线程发送一个中断信号
        try {
        	System.out.println("等待:关闭所有正在跑的任务进程.....");
        	//调用公共方法来关闭 ，应该在启动任务的某个地方记录下启动的任务或进程且关闭的时候做清除
        	// --
        	Thread.currentThread().sleep(1000*5);
            mainThread.join(); //等待 mainThread 正常运行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主程序将正常退出.....");
    }

    public ShutdownSampleHook(Thread mainThread) {
        this.mainThread=mainThread;
        Runtime.getRuntime().addShutdownHook(this);
    }
}