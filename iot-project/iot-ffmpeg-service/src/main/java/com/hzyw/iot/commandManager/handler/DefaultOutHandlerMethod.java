package com.hzyw.iot.commandManager.handler;

import com.hzyw.iot.utils.IotInfoConstant;

/**
 * 默认任务消息输出处理
 * @author admin
 * 
 */
public class DefaultOutHandlerMethod implements OutHandlerMethod{

	/**
	 * 任务是否异常中断，如果
	 */
	public boolean isBroken=false;
	
	@Override
	public void parse(String id,String msg) {
		//过滤消息
		if (msg.indexOf("fail") != -1) {
			System.err.println(id + "任务可能发生故障：" + msg);
			System.err.println("失败，设置中断状态");
			isBroken= true;
			//如果发生异常或失败，设置全局变量里面的 IotInfoConstant.gloable_CommandManager.CommandManager.
			String[] idAndOperator = id.split(",");
			if(IotInfoConstant.gloable_CommandManager.get(idAndOperator[0]) != null){
				System.err.println(id + "，失败 ！ 清除缓存中的任务信息 ！");
				IotInfoConstant.gloable_CommandManager.get(idAndOperator[0]).remove(id);
			}
		}else if(msg.indexOf("miss")!= -1) {
			System.err.println(id + "任务可能发生丢包：" + msg);
			System.err.println("失败，设置中断状态");
			//isBroken=true;
			isBroken=false; //byzhu  丢包不需要执行中断，因为中断后会立即进入复活，ffmpeg会有问题 因为上次ffmpeg已经执行推流命令成功了撒，会提示连接被占用的
			               //丢包后如果不broken ,继续推流 ，会容易导致VLC上面的展示的视频卡住,所以这里要broken ,然后进入重连
		}else {
			isBroken=false;
			System.err.println(id + "消息：" + msg);
		}
	}

	@Override
	public boolean isbroken() {
		return isBroken;
	}
	
}
