package com.hzyw.iot.test;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
public class TestController {
	@RequestMapping(value = "/devInfoResponse", method = RequestMethod.POST)
	public String devInfoResponse(@RequestBody String json) throws MqttException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "test");
		jsonObject.put("gwId", "test");
		jsonObject.put("type", "test");
		jsonObject.put("code", "0");
		System.out.println("属性上报:"+json);
		return jsonObject.toString();
	}
	
	
	@RequestMapping(value = "/response", method = RequestMethod.POST)
	public String response(@RequestBody String json) throws MqttException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "test");
		jsonObject.put("gwId", "test");
		jsonObject.put("type", "test");
		jsonObject.put("code", "0");
		System.out.println("请求返回:"+json);
		return jsonObject.toString();
	}
	
	
	@RequestMapping(value = "/metricInfoResponse", method = RequestMethod.POST)
	public String metricInfoResponse(@RequestBody String json) throws MqttException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "test");
		jsonObject.put("gwId", "test");
		jsonObject.put("type", "test");
		jsonObject.put("code", "0");
		System.out.println("设备状态数据上报:"+json);
		return jsonObject.toString();
	}
	
	
	
	
	@RequestMapping(value = "/devSignalResponse", method = RequestMethod.POST)
	public String devSignalResponse(@RequestBody String json) throws MqttException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "test");
		jsonObject.put("gwId", "test");
		jsonObject.put("type", "test");
		jsonObject.put("code", "0");
		System.out.println("设备信号上报:"+json);
		return jsonObject.toString();
	}
	
	@RequestMapping(value = "/devOnline", method = RequestMethod.POST)
	public String devOnline(@RequestBody String json) throws MqttException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", "test");
		jsonObject.put("gwId", "test");
		jsonObject.put("type", "test");
		jsonObject.put("code", "0");
		System.out.println("设备上下线:"+json);
		return jsonObject.toString();
	}
	public static void main(String[] args) throws Exception {
		/*HeadHandlerUtil util = new HeadHandlerUtil();
		byte[] privateKeyStr =util.hexStrToByteArr("8707D50382E9E52F21BE83D260098EB101033956CBFB465CA757464ABE2CD3BE");
		byte[] publicKeyStr =util.hexStrToByteArr("C848306D64058309AE5C83B0870BAE04D1B0C7BC4C9DCD995D77CEF0ECDD3C86AF995D2FC105E1630FAFA63AAF47F5C38170E42D1C8C2C9EFAA2D3CD9AEA478B");
		//私钥解密
		SM2 sm2 = SmUtil.sm2(privateKeyStr,publicKeyStr);
		//密文:C9B8F825264DDE32ED3008A2975C39A833908414F6AC8CBD46CA41F624B1179694CCBC76B58A12783ACD769F648A484E513F9D9F75E086B80F62FBD2E4D7326547039A0DDAB9E76C48789AA7CF3935ED5F78238CBD0A6A531E2F81ADCCBED0F914E85E1F6298878B256195E9E2BF92FE6EA898ABD528D90A620F99B7D2C49EAC9D
		String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd("801E405DFB492CEA464297ED54014AD4826CBD4BFEAB01A64A69E7D76D2175A938E7B88289CA8707F411D978DF4D0EFD8CBB22B008A90C40C20641B30C77A8D4DAC472B12C89105B64BDCF1470557510F096D74C5D2490FEFB3A2BE50F7C574CF35C1BB853B479D258B073380D056FCDDD5B0C330891EAC47A0F45EAA71361AB9C", KeyType.PrivateKey));
		System.out.println("解密:"+decryptStr);
		*/
		System.out.println(hexStringToString("7B22636F6465223A3730322C226D7367223A22BDE2C3DCCAFDBEDDCAA7B0DC227D"));
	
	}
	
	/**
	 * 16进制转换成为string类型字符串
	 * @param s
	 * @return
	 */
	public static String hexStringToString(String s) {
	    if (s == null || s.equals("")) {
	        return null;
	    }
	    s = s.replace(" ", "");
	    byte[] baKeyword = new byte[s.length() / 2];
	    for (int i = 0; i < baKeyword.length; i++) {
	        try {
	            baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    try {
	        s = new String(baKeyword, "GBK");
	        new String();
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    return s;
	}
}
