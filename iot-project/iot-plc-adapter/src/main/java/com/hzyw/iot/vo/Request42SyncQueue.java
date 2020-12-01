package com.hzyw.iot.vo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.test.TestBlockingQueue;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.vo.dc.ModbusInfo;

import io.netty.channel.Channel;

public class Request42SyncQueue {
	private static final Logger logger = LoggerFactory.getLogger(Request42SyncQueue.class);
	public Request42SyncQueue(){
	}
	
	public Request42SyncQueue(String msgId,String cacheKey){
		this.msgId = msgId;
		this.cacheKey = cacheKey;
	}
	private String msgId;
	private String cacheKey;
	private long timesOut;//超时任务自动关闭
	private Integer syncSleepTime;//任务执行间隔事件 毫秒
	 
	public Integer getSyncSleepTime() {
		return syncSleepTime;
	}

	public void setSyncSleepTime(Integer syncSleepTime) {
		this.syncSleepTime = syncSleepTime;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public long getTimesOut() {
		return timesOut;
	}

	public void setTimesOut(long timesOut) {
		this.timesOut = timesOut;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	 
	private LinkedBlockingQueue<Request42SyncQueueVO> concurrentLinkedQueue = new LinkedBlockingQueue<Request42SyncQueueVO>(255); 
	 
	public LinkedBlockingQueue<Request42SyncQueueVO> getConcurrentLinkedQueue() {
		return concurrentLinkedQueue;
	}
	public void setConcurrentLinkedQueue(LinkedBlockingQueue<Request42SyncQueueVO> concurrentLinkedQueue) {
		this.concurrentLinkedQueue = concurrentLinkedQueue;
	}
	
    
	public void startConsumer(){
		Consumer consumer = new Consumer(this.getCacheKey(),2*1000,3*1000,this.cacheKey,this.msgId,this.getSyncSleepTime());//5秒没数据入队列,则超时退出线程
		Thread task = new Thread(consumer,"==request42=>[队列处理/cacheKey="+this.getCacheKey() + "/msgId="+this.getMsgId()+"]");
		task.start();
	}
	
	static int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;//任务处理间隔
	class Consumer implements Runnable {  
	     private String name;  
	     
	     private long timeout;  
	     
	     private long waittime;
	     
	     private String cacheId;
	     private String msgId;
	     private long syncSleepTime;
	     
	     public Consumer() {}  
	     
	     /**
	      * 等待多少时间线程会自动退出呢 time=timeout + waittime;建议值   timeout=2000,waittime=3000
	     * @param name =cahekey
	     * @param timeout 线程失效时间,毫秒
	     * @param waittime 允许等待观察(是否继续有数据入栈)多少时间 ,毫秒
	     * 
	     * cacheId  --支持打印日志
	     * msgId   --支持打印日志
	     */
	    public Consumer(String name,long timeout,long waittime,String cacheId,String msgId,long syncSleepTime) {  
	         this.name = name; 
	         this.timeout = timeout;
	         this.waittime = waittime;
	         this.cacheId = cacheId;
	         this.msgId = msgId;
	         this.syncSleepTime = syncSleepTime;
	     }  
	    
	     public void run() {
	    	 long _timeout = timeout;
	    	 while(_timeout >=0){
	         //for (int i = 1; i < 10; ++i) {  
				try {
					// 必须要使用take()方法在获取的时候阻塞
					//System.out.println(" ----+++Queue.take：/下发到设备  -----------------------" + concurrentLinkedQueue.poll());
					// 使用poll()方法 将产生非阻塞效果
					// concurrentLinkedQueue.poll());
					Request42SyncQueueVO item = concurrentLinkedQueue.poll();
					if(item != null){
						PlcProtocolsBusiness.ctxWriteAndFlush_byResponse(item.getChannel(), item.getModbusInfo(),
								item.getMsgId(), item.getPlcId(), item.getNodeId(), item.getMethod());
					}
 					
					_timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS; // 默认是100毫秒 ,
					/*
					 * 延迟100 毫秒, 这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
					 * 只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,
					 * 这将可能导致前面来的锁得不到满足. 使用随机的等待时间可以一定程度上保证公平性
					 */
					Thread.sleep(this.syncSleepTime);//处理间隔100毫秒
					//logger.info(" ----+++ concurrentLinkedQueue.size() = "+ concurrentLinkedQueue.size());
					
					if(!concurrentLinkedQueue.isEmpty()){
						_timeout = this.timeout; //一直都有数据,持续处理
					}
					
					if (_timeout < 0) {
						Thread.sleep(waittime);//再等待5秒如果还没有数据则退出
						// 超时时间到了，查看队列里面是否还有数据，如果还有，重新设置超时时间
						if (!concurrentLinkedQueue.isEmpty()) {
							logger.info(" --request_45_request--+++ concurrentLinkedQueue.size() > 0 重新设置超时继续消费../cacheKey="+this.cacheId+ "/msgId="+this.msgId );
							_timeout = this.timeout;
						}else{
							logger.info(" --request_45_request--+++ 已超时,,,没有数据了直接退出!../cacheKey="+this.cacheId+ "/msgId="+this.msgId );
							_timeout = -1;
						}
					}
				} catch (Exception e) {
	                    e.printStackTrace();
	                    _timeout = -1;
	            }  

	         }
	    	 //清除内存
	    	 PlcProtocolsBusiness.request42SyncQueue.remove(this.name);
	    	 logger.info(" ---request_45_request-+++ 清理缓存并退出,Queue exit ! +++-../cacheKey="+this.cacheId+ "/msgId="+this.msgId);
	         
	     }  
	 }   
	
}
