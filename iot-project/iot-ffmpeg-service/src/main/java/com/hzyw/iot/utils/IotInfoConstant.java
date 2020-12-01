package com.hzyw.iot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.commandManager.CommandManager;
import com.hzyw.iot.commandManager.CommandManagerImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
 
public class IotInfoConstant {
 
    /**
     * 
     * 缓存CommandManager对象，提供根据设备ID来停止此设备下所运行的线程
     * Map<设备ID, Map<设备ID+","+操作code, CommandManager>>
     * -- CommandManager中默认会把CommandTasker.id =设备ID+","+操作code,因为所有的CommandManager.start操作输入的ID需要控制为此ID
     * --这里应该放到REDIS，防止服务关掉后丢失缓存数据
     * 
     */
    public static final Map<String, Map<String, CommandManagerImpl>> gloable_CommandManager = new HashMap<String, Map<String, CommandManagerImpl>>();
    
}
