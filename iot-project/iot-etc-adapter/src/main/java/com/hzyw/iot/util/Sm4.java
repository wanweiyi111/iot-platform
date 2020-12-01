package com.hzyw.iot.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import cn.hutool.core.date.DateUtil;

import java.security.*;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Sm4  {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    public static final String ALGORITHM_NAME = "Sm4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "Sm4/ECB/PKCS5Padding";
    public static final String ALGORITHM_NAME_ECB_NOPADDING = "Sm4/ECB/NoPadding";
    public static void main(String[] args) {
    	String s = "5358474C4554435041524B9529140773";
    	//0123456789abcdeffedcba9876543210
        byte[] key = Hex.decode(s);
       try {
    	   /*Map<String, Object> dataMap = new LinkedHashMap<>();
	        dataMap.put("vehInfo", "87C28F6A5873E29EAC00ED7959E523A13C623C7E53F5230E6E1DFEDC5D85221425DAE1EE5457CEE7F7565688913985B92457963AB8A69E929F29A6FF5364F11D9619EFF0BAF67CA70CD0E2F531E9C4A7C2190808FCD04074");
	        dataMap.put("random", "30478FBAD358FCCD");
	        dataMap.put("tradNo", "86030061201484AE");
	        dataMap.put("divCode", "B9E3B6ABB9E3B6AB");
	        JSONObject dataJson = JSONUtil.parseObj(dataMap);
	        String dataStr = dataJson.toString();
	        System.out.println("data数据:"+dataStr);
	        byte[] srcData = dataStr.getBytes("UTF-8");
          byte[] enc =encrypt_Ecb_Padding(key,srcData);
          String chp = Hex.toHexString(enc);
          System.out.println("SM4数据"+chp);
          Map<String, Object> map = new LinkedHashMap<>();
	        map.put("rsuId", "93a5cabdc2f747ae");
	        map.put("frameId", "1");
	        map.put("data", chp);
	        JSONObject jsonObject = JSONUtil.parseObj(map);
	        System.out.println("发送的数据:"+jsonObject);
	       String test =HttpRequest.post("http://hynas.imdo.co:10087/etckey/card/getVehInfo").body(jsonObject.toString()).execute().body();//json方式
	        System.out.println("返回的数据:"+test);*/
	        
    	   String encStr = "60D4D122C2B55C26DC5A9AE08DEDB6F5B0AB595137EEADEDDB55E291BF740ABB";
    	   //
    	   // 681edf34d206965e86b3e94f536e4246
    	   byte[] keyByte = Hex.decode(encStr);
	        //解密
	        byte[] test1 = decrypt_Ecb_NoPadding(key,keyByte);
	        
	        String test = Hex.toHexString(test1);
	      // String  decryptStr = new String(test1, "UTF-8");
	        
	        System.out.println("sm4解密出来的数据:"+test);
    	   
    	   
    	   /*Map<String, Object> dataMap = new LinkedHashMap<>();
	        dataMap.put("cardId", "1801232204653283");
	        dataMap.put("random", "5E525E0B");
	        dataMap.put("treadNo", "0008");
	        dataMap.put("time", getToday());
	        dataMap.put("money", "00000002");
	        dataMap.put("keyInfo", "0100");
	        dataMap.put("issueId", "B9E3B6ABB9E3B6AB1");
	        //dataMap.put("", "");
	        JSONObject dataJson = JSONUtil.parseObj(dataMap);
	        String dataStr = dataJson.toString();
	        System.out.println("data数据:"+dataStr);
	        byte[] srcData = dataStr.getBytes("UTF-8");
	        System.out.println(srcData.length);
	        if(srcData.length% 16 == 0) {//该方法要求:要求加密/解密的内容需要满足 (pSourceLen % 16 == 0)
	        	System.out.println("方法进来表示进入SM4_ECB_NoPadding方法");
	        }
           byte[] enc =encrypt_Ecb_Padding(key,srcData);
           String chp = Hex.toHexString(enc);
          // chp = chp.substring(0, chp.length());
           System.out.println("SM4数据"+chp);
           Map<String, Object> map = new LinkedHashMap<>();
	        map.put("rsuId", "93a5cabdc2f747ae");
	        map.put("frameId", "1");
	        map.put("data", chp);
	        JSONObject jsonObject = JSONUtil.parseObj(map);
	        System.out.println("发送的数据:"+jsonObject.toString());
	       String test =HttpRequest.post("http://hynas.imdo.co:10087/park/rsu/getMac").body(jsonObject.toString()).execute().body();//json方式
	        System.out.println("返回的数据:"+test);
           System.out.println(chp);*/
       } catch (Exception e) {
           e.printStackTrace();
       }
	}
    /**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * @param strIn 需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception 本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStrToByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes("ISO8859-1");// getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	/**
	 * 将byte数组转化为16进制输出
	 *
	 * @param bytes
	 * @return
	 */
	public static String convertByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			int temp = bytes[i] & 0xff;
			String tempHex = Integer.toHexString(temp);
			if (tempHex.length() < 2) {
				result += "0" + tempHex;
			} else {
				result += tempHex;
			}
		}
		return result;
	}
	
   /* @Test
    public void testSM4Code(){

        String s = "01 23 45 67 89 ab cd ef fe dc ba 98 76 54 32 10 ";
         byte[] key = Hex.decode(s);
        try {
            byte[] enc =encrypt_Ecb_NoPadding(key,key);
            String chp = Hex.toHexString(enc);
            System.out.println(chp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
	 * 当前时间格式化
	 */
	public static String getToday() {
		Date date = DateUtil.parse(DateUtil.now().toString());
		String format = DateUtil.format(date, "yyyyMMddHHmmss");// 日期格式
		return format;
	}
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
            InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static byte[] encrypt_Ecb_NoPadding(byte[] key, byte[] data)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_NOPADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    public static byte[] decrypt_Ecb_NoPadding(byte[] key, byte[] cipherText)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_NOPADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

}
