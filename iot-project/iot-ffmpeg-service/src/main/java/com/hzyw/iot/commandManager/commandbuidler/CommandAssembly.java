package com.hzyw.iot.commandManager.commandbuidler;

import java.util.Map;
/**
 * 命令组装器接口
 * @author admin
 * 
 * 
 */
public interface CommandAssembly {
	/**
	 * 将参数转为ffmpeg命令
	 * @param paramMap
	 * @return
	 */
	public String assembly(Map<String, String> paramMap);
	
	public String assembly();
}
