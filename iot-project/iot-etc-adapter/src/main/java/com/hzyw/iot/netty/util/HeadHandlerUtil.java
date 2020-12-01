package com.hzyw.iot.netty.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.client.EtcClient;
import com.hzyw.iot.netty.vo.FilterBufVo;
import com.hzyw.iot.util.Sm4;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class HeadHandlerUtil {
	public final static Map<String,Integer> map = new HashMap<String,Integer>();//帧序列号
	public static List<Integer> listIndex = new ArrayList<Integer>();//算出ffff位置
	public static List<String> listCut = new ArrayList<String>();//切出并合并的数据
	private static final Logger LOGGER = LoggerFactory.getLogger(HeadHandlerUtil.class);
	/**
	 * 下发指令数据模型，返回完整数据(指令类型)
	 *
	 */
	public byte[] requestHandler(String data) {
		byte[] stx = new byte[2];//帧开始标志，2 字节，取值为 FFFFH；
		byte[] ver = new byte[1];//协议版本号，当前版本为 00H
		byte[] seq = new byte[1];//帧序列号
		byte[] len = new byte[4];//DATA 域的长度，4个字节
		byte[] crc = new byte[2];//从 VER 到 DATA 所有字节的 CRC16 校验值，2 字节,初始值为 FFFFH
		byte[] temp = new byte[data.length()];
		byte[] send = new byte[1024];
		try {
			stx = hexStrToByteArr("ffff");
			ver =hexStrToByteArr("00");
			if(map.get("obj")==null||map.get("obj")==9) {
				map.put("obj", 1);
			}else {
				int a = map.get("obj");
				a++;
				map.put("obj", a);
			}
			seq = hexStrToByteArr(map.get("obj")+"0");
			temp = hexStrToByteArr(data);
			String hex  = intToHex(temp.length);
			String pad = padLeft(hex,8);
			len = hexStrToByteArr(pad);
			crc =hexStrToByteArr("ffff");
			send = getPdt(stx,ver,seq,len,temp,crc);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return send;
	}
	
	
	
	/**
	 * 新下发指令
	 *
	 */
	public byte[] newRequestHandler(String com,String data) {
		byte[] stx = new byte[2];//帧开始标志，2 字节，取值为 FFFFH；
		byte[] code = new byte[1];//指令
		byte[] len = new byte[1];//DATA 域的长度，1个字节
		byte[] crc = new byte[1];//异域校验值
		byte[] etx = new byte[1];//结束符
		byte[] temp = new byte[data.length()];
		byte[] send = new byte[1024];
		try {
			stx = hexStrToByteArr("ffff");
			code = hexStrToByteArr(com);
			temp = hexStrToByteArr(data);
			String hex  = intToHex(temp.length);
			String pad = padLeft(hex,2);
			len = hexStrToByteArr(pad);
			String comtest = com+data;//指令+data数据域
			String xor = byteToHex(getXor(comtest));
			//System.out.println("com+data数据:"+comtest);
			crc =hexStrToByteArr(xor);//hexStrToByteArr("ffff");
			String chp = Hex.toHexString(crc);
	    	//System.out.println("校验码:"+chp);
			etx = hexStrToByteArr("ff");
			send = getPdt(stx,code,len,temp,crc,etx);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return send;
	}
	
	/**
	 * 缺失部分用0补充
	 *
	 */
	public static String padLeft(String str,int len){
        String pad="0000000000000000";
        return len>str.length()&&len<=16&&len>=0?pad.substring(0,len-str.length())+str:str;
    }
	 
	
	
	
	/**
	 * 当前时间格式化
	 */
	public String getToday() {
		Date date = DateUtil.parse(DateUtil.now().toString());
		String format = DateUtil.format(date, "yyyyMMddHHmmss");// 日期格式
		return format;
	}
	
	/**
     * 10进制转16进制编码
     */
	public static String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        while(n != 0){
            sb = sb.append(b[n%16]);
            n = n/16;            
        }
        a = sb.reverse().toString();
        return a;
    }
	
	
	
	
	
	/**
	 * 合并byte数据
	 *
	 */
	public static byte[] getPdt(byte[]... ls) throws Exception {
		int len = 0;
		for (byte[] it : ls) {
			len = len + it.length;
		}
		ByteBuf byteBuf = Unpooled.buffer(len);
		for (byte[] it : ls) {
			byteBuf.writeBytes(it);
		}
		byte[] temp = byteBuf.array();
		if (byteBuf != null)
			ReferenceCountUtil.release(byteBuf);
		return temp;
	}
	
	/**
	 * 16进制转2进制
	 *
	 * @param hex
	 * @return
	 */
	public static String hexStringToByte(String hex) {
	    int i = Integer.parseInt(hex, 16);
	    String str2 = Integer.toBinaryString(i);
	    return str2;
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
	
	/**
	 * 算出ffff位置
	 *
	 *//*
	public static void indexStr(String str) {
		String key = "ffff";
		int a = str.indexOf(key);
		while (a!=-1) {
			//System.out.println(a);
			listIndex.add(a);
			a=str.indexOf(key,a+1);
		}
	}*/
	
	/**
	 * 截取byte[]数据
	 * 也可以用这个方法:System.arraycopy(src, srcPos, dest, destPos, length)
	 *
	 */
	 public static byte[] subBytes(byte[] src, int begin, int count) {
	        byte[] bs = new byte[count];
	        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
	        return bs;
	    }
	
	/**
	 * 算出ffff位置
	 *
	 */
	public static void indexStr(String str) {
		String key = "ffff";
		int a = str.indexOf(key);
		int b =0;
		int c =0;
		int i= 0;
		while (a!=-1) {
			//System.out.println(a);
			b=a;
			if(b==c+1) {
				listIndex.remove(i-1);
				listIndex.add(a);
				c++;
				a=str.indexOf(key,a+1);
				continue;
			}
			listIndex.add(a);
			c=b;
			++i;
			a=str.indexOf(key,a+1);
		}
	}
	/**
	 * 过滤解码
	 *
	 */
	public FilterBufVo filter(String bodyStr) {
		int j = 0;
		FilterBufVo vo = new FilterBufVo();
		List<String> listStr = new ArrayList<>();//完整的数据
		//List<String> listBugStr = new ArrayList<>();//不完整的数据
		indexStr(bodyStr);
		for (int i = 0; i < listIndex.size(); i++) {
			//System.out.println("遍历出来:"+listIndex.get(i));
			++j;
			if(i==listIndex.size()-1) {
				String lastSub = bodyStr.substring(listIndex.get(i), bodyStr.length());
				if(lastSub.length()<=10) {
					//System.out.println("数据长度错误:"+lastSub);
					listCut.add(lastSub);
					listIndex.clear();//位置清空数据
					break;
				}
				//System.out.println("最后的数据:"+lastSub);
				//System.out.println("--------------------------------------------最后的数据:"+lastSub);
				String seqDataSub = lastSub.substring(4, lastSub.length()-4);//拿出校验的数据
				String seqSub = lastSub.substring(lastSub.length()-4);//拿出crc
				String crcUtil = CRCUtils.crc(seqDataSub);//校验的数据
				
				if(seqSub.equals(crcUtil)) {//是否正确的数据
					listStr.add(lastSub);
					//System.out.println("正确的数据:"+lastSub);
				}else {
					//listBugStr.add(lastSub);
					listCut.add(lastSub);//不完整的数据存起来
					//System.out.println("不完整的数据:"+lastSub);
				}
				listIndex.clear();//清空数据
				//System.out.println("跳出");
				break;
			}
			if(listIndex.get(0)!=0&&i==0) {//合并数据
				//System.out.println("前半数据"+listCut.get(0));
				String subCut1 = listCut.get(0)+bodyStr.substring(0, listIndex.get(i));
				System.out.println("合并的数据:"+subCut1);
				String seqDataSub1 = subCut1.substring(4, subCut1.length()-4);//拿出校验的数据
				String seqSub1 = subCut1.substring(subCut1.length()-4);//拿出crc
				String crcUtil1 = CRCUtils.crc(seqDataSub1);//校验的数据
				if(seqSub1.equals(crcUtil1)) {//是否正确的数据
					listStr.add(subCut1);
				}
				
				listCut.clear();//listCut清空数据
			}
			String sub = bodyStr.substring(listIndex.get(i), listIndex.get(j));
			/*if(sub.equals("f")&&listStr.size()!=0) {
				String frontStr = listStr.get(i-1);//上一条数据,截取少了一个f
				listStr.remove(i-1);
				listStr.add(frontStr+"f");
				System.out.println("删除了上一条,并重接拼接的数据");
				continue;
			}*/
			listStr.add(sub);
			//System.out.println("截取出数据:"+sub);
			
		}
		vo.setListStr(listStr);
		//vo.setListBugStr(listBugStr);
		return vo;
		
	}


	//转义
	public static String handlebody(String msg) {
		String fe01 = "fe01";
		String fe00 = "fe00";
		int count = getStringCount(msg, fe01); // 返回的数量为
		int count1 = getStringCount(msg, fe00);
		String newMsg = msg.replace(fe01, "ff").replace(fe00, "fe");//转义
		long number = Long.parseLong(newMsg.substring(6, 8),16);
		//替换长度
		String numberlen = String.valueOf(number - count - count1);
		String inToHex = intToHex(Integer.parseInt(numberlen));
		StringBuilder str = new StringBuilder(newMsg);
		str.replace(6, 8, inToHex);
		//System.out.println("转义:" + str);
		return str.toString();
	}
	
	//判断一个字符串source中，有几个字符串src
    private static int getStringCount(String source, String src) {
        int index = 0;
        int count = 0;
        int start = 0;
        while ((index = source.indexOf(src, start)) != -1) {
            count++;
            start = index + 1;
        }
        return count;
    }

  //异域校验算法
  	public static byte getXor(String str){
  		
  		byte[] datas = Hex.decode(str);
  		 
  		byte temp=datas[0];
  			
  		for (int i = 1; i <datas.length; i++) {
  			temp ^=datas[i];
  		}
  	 
  		return temp;
  	}
  	
  /** 
   * 字节转十六进制 
   * @param b 需要进行转换的byte字节 
   * @return  转换后的Hex字符串 
   */  
  	public static String byteToHex(byte b){  
      String hex = Integer.toHexString(b & 0xFF);  
      if(hex.length() < 2){  
          hex = "0" + hex;  
      }  
      return hex;  
  	}
    
  	/** 
     * SM4解密
     * @param key 秘钥
     * @param sm4Data 密文
     */     
	public String sm4Decrypt(String key, String sm4Data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		Sm4 sm4 =new Sm4();
		byte[] keyByte = Hex.decode(key);
		byte[] sm4DataByte = Hex.decode(sm4Data);
		byte[] enc =sm4.decrypt_Ecb_NoPadding(keyByte,sm4DataByte);//解密
		String hex = Hex.toHexString(enc);//转16进制码
		int index = getStringIndex(hex, "ff00", 5);//过滤00
		String sub = hex.substring(0, index+2);//截取正确数据
		return sub;
	}


	/** 
     * SM4加密
     * @param key 秘钥
     * @param merge 明文btye[]
	 * @throws Exception 
     */ 
	public String sm4Encrypt(String key, byte[] msg) throws Exception {
		Sm4 sm4 =new Sm4();
		//System.out.println("byte长度:"+msg.length);
		int mod = msg.length%16;
		//System.out.println("取模数:"+mod);
		String chp =null;
		if(mod!=0) {
			int mod00 = 16-mod;//算出补多少个00
			//System.out.println("补多少组:"+mod00);
			byte[] byte00 = new byte[mod00];//补充字节
			byte[] merge =getPdt(msg,byte00);//合并数据
			byte[] keyByte = Hex.decode(key);
			byte[] enc =sm4.encrypt_Ecb_NoPadding(keyByte,merge);//加密
	        chp = Hex.toHexString(enc);//转16进制码
	       // System.out.println("明文:"+ convertByteToHexString(merge));
		}else {
			byte[] keyByte = Hex.decode(key);
			byte[] enc =sm4.encrypt_Ecb_NoPadding(keyByte,msg);//加密
	        chp = Hex.toHexString(enc);//转16进制码
	     //  System.out.println("明文:"+ convertByteToHexString(msg));
		}
		
		return chp;
	}
	
	 //判断一个字符串source中，从指定的位置开始开始计算，字符串src的游标值
    private static int getStringIndex(String source, String src, int beginIndex) {
        int index = 0;
        int start = 0;
        while ((index = source.indexOf(src, start)) != -1 && index < beginIndex) {
            start = index + 1;
        }
        return index;
    }


    //封装json数据，以及后面补充00
	public byte[] cxJson(String sm4Encrypt) throws Exception {
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("proxyId", "SC_0001");
		jsonObj.put("cmd", "CX");
		jsonObj.put("frameId", 100);
		jsonObj.put("path", "SR_0001");
		jsonObj.put("data", sm4Encrypt);
		//System.out.println("发送过去的JSON："+jsonObj);
        byte[]  byte1 = getPdt(jsonObj.toString().getBytes(),new byte[1]);//后面加一个00
		return byte1;
	}


	//过滤fe--ff
	public static String obuidFilter(String OBUID) {
		String newMsg1 = OBUID.replace("fe", "fe00");
		String newMsg = newMsg1.replace("ff", "fe01");//转义
		return newMsg;
	}
	
}
