package com.hzyw.iot.commandManager.commandbuidler;

/**
 * 默认流式命令构建器工厂类
 * @author admin
 *
 */
public class CommandBuidlerFactory {

	public static CommandBuidler createBuidler() {
		return new DefaultCommandBuidler();
	};
	
	public static  CommandBuidler createBuidler(String rootpath) {
		return new DefaultCommandBuidler(rootpath);
	};
}
