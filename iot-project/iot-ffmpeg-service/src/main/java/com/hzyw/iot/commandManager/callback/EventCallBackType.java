package com.hzyw.iot.commandManager.callback;

/**
 * 事件回调类型
 * @author admin
 *
 */
public enum EventCallBackType {
	exec,//执行命令后通知
	stop,//停止命令后通知
	interrupt,//进程中断后通知
	heartbeat,//主进程存活心跳
}
