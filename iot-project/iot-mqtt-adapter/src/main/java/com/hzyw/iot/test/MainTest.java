package com.hzyw.iot.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hzyw.iot.vo.dataaccess.RequestDataVO;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class MainTest {
	public static void main(String[] args) {
		
        System.out.println(JSONUtil.parseObj(getDataVO()));
		
	}
	
	private static RequestDataVO getDataVO() {
	     RequestDataVO dataVO=new RequestDataVO();
	     List<Map>methods=new ArrayList<Map>();
	     List<Map>ins=new ArrayList<Map>();
	     Map<String,Object>dataMap=new HashMap<String, Object>();
	     Map<String,Object>inMap=new HashMap<String, Object>();
	     
	     inMap.put("code", "00H");
	     inMap.put("pdt",getPdtParams());
	     ins.add(inMap);
	     
	     dataMap.put("method", "82H");
	     dataMap.put("in", ins);
	     
	     methods.add(dataMap);
	     
	     dataVO.setId("");
	     dataVO.setMethods(methods);
	     return dataVO;
	    }
	    
	    private static List<String[]> getPdtParams(){
	     List<String[]> pdtList=new ArrayList<String[]>();
	  pdtList.add(new String[]{"200","040919","051119","02H","0","00H","5922","80","02H","12","42H","C8H","01H"});
	  return pdtList;
	    }
}
