package com.hzyw.iot.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.vo.Request42SyncQueue;
import com.hzyw.iot.vo.Request42SyncQueueVO;
import com.hzyw.iot.vo.ResponseCache;

public class TestBlockingQueue {
	// 阻塞队列，FIFO
	private static LinkedBlockingQueue<Integer> concurrentLinkedQueue = new LinkedBlockingQueue<Integer>(3);

	public static void main(String[] args) {
		testEX();

	}

	static ExecutorService executorService = Executors.newFixedThreadPool(2);
	//public static Map<String, concurrentLinkedQueue> aa = new HashMap<String, concurrentLinkedQueue>();

	private static void testEX() {

		executorService.submit(new Producer("producer1"));
		// executorService.submit(new Producer("producer2"));
		// executorService.submit(new Producer("producer3"));
		// executorService.submit(new Consumer("consumer1"));
		// executorService.submit(new Consumer("consumer2"));
		// executorService.submit(new Consumer("consumer3"));
		Consumer cs = new Consumer("测试",2*1000,5*1000);
		new Thread(cs).start();
	}

	static String cachekey = "000000100_40_3";

	static class Producer implements Runnable { // 测试下发指令 同步阻塞
		private String name;
		public Producer(String name) {
			this.name = name;
		}

		public void run() {
			int i = 1;

			while (i > 0) {
				// for (int i = 1; i < 10; ++i) {
				boolean queuefull = concurrentLinkedQueue.offer(i);
				if(!queuefull){
					System.out.println(name + " 生产：//////消息已满   --" + concurrentLinkedQueue.size());
				}
				// concurrentLinkedQueue.offer(i); //不阻塞
				// concurrentLinkedQueue.add(i);
				// concurrentLinkedQueue.put(i);
				i++;
				if (i % 5 == 0) {
					try {
						Thread.sleep(1000*20); // 暂停15秒再生成消息
						System.out.println(name + " 生产：//////暂停8秒再生成5条消息   --" + concurrentLinkedQueue.size());
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
	static int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
	static class Consumer implements Runnable {  
	     private String name;  
	     private long timeout;  
	     private long waittime;
	     public Consumer() {
	     }  
	     
	     /**
	      * 等待多少时间线程会自动退出呢 time=timeout + waittime
	     * @param name =cahekey
	     * @param timeout 线程失效时间,毫秒
	     * @param waittime 允许等待观察(是否继续有数据入栈)多少时间 ,毫秒
	     */
	    public Consumer(String name,long timeout,long waittime) {  
	         this.name = name; 
	         this.timeout = timeout;
	         this.waittime = waittime;
	     }  
	     public void run() {
	    	 long _timeout = timeout;
	    	 while(_timeout >=0){
	         //for (int i = 1; i < 10; ++i) {  
				try {
					// 必须要使用take()方法在获取的时候阻塞
					System.out.println(" ----+++Queue.take：/下发到设备  -----------------------" + concurrentLinkedQueue.poll());
					// 使用poll()方法 将产生非阻塞效果
					// concurrentLinkedQueue.poll());

					// 还有一个超时的用法，队列空时，指定阻塞时间后返回，不会一直阻塞
					// 但有一个疑问，既然可以不阻塞，为啥还叫阻塞队列？
					// System.out.println(name+" Consumer " + concurrentLinkedQueue.poll(300, TimeUnit.MILLISECONDS));
					//int  p = concurrentLinkedQueue.poll();
					/*PlcProtocolsBusiness.ctxWriteAndFlush_byResponse(item.getChannel(), item.getModbusInfo(),
							item.getMsgId(), item.getPlcId(), item.getNodeId(), item.getMethod());*/
					
					_timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS; // 默认是100毫秒 ,
					/*
					 * 延迟100 毫秒, 这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
					 * 只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进行,也以同样的频率申请锁,
					 * 这将可能导致前面来的锁得不到满足. 使用随机的等待时间可以一定程度上保证公平性
					 */
					Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);//处理间隔100毫秒
					System.out.println(" ----+++ concurrentLinkedQueue。size() = "+ concurrentLinkedQueue.size());
					if(!concurrentLinkedQueue.isEmpty()){
						_timeout = this.timeout; //持续处理
					}
					if (_timeout < 0) {
						Thread.sleep(waittime);//再等待5秒如果还没有数据退出
						// 超时时间到了，查看队列里面是否还有数据，如果还有，重新设置超时时间
						if (!concurrentLinkedQueue.isEmpty()) {
							System.out.println(" ----+++ concurrentLinkedQueue.size() > 0 重新设置超时继续消费.." );
							_timeout = this.timeout;
						}else{
							System.out.println(" ----+++ 已超时,,,没有数据了直接退出!.." );
							_timeout = -1;
						}
					}
					//if (concurrentLinkedQueue.isEmpty()) {
					//	// 没有数据了直接退出
					//	System.out.println(" ----+++ 没有数据了直接退出.." );
					//	_timeout = -1;
					//}
				} catch (Exception e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	            }  

	         }
	    	 //清除内存
	         System.out.println(" ----+++ 清理缓存并退出,Queue exit ! +++---");
	         
	     }  
	 }   

}
