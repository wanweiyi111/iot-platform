package com.hzyw.iot.netty.util;

public class Test {
	
	public static void main(String[] args) throws Exception {
	//	System.out.println(hexStringToByte("c1"));
		/*byte[] hexByte = hexStrToByteArr("88bfa14a");
		String  decryptStr = new String(hexByte, "UTF-8");
		System.out.println(decryptStr);*/
		/*byte[] hexByte = hexStrToByteArr("647d27df71fcb227262b8fc3");
		String  vehInfoStr = new String(hexByte, "GBK");
	    System.out.println("vehInfo解析:"+vehInfoStr);*/
		
		
		
	        //判断一个字符串中是否包含另个字符串
	        String msg = "ffffB0150001020304fe0506fe000101020301020102030405fe01e7ff";
	        String src = "fe01";
	        String src1 = "fe00";
	        int count = getStringCount(msg, src); //返回的数量为
	        int count1 = getStringCount(msg, src1); //返回的数量为
	        System.out.println("count:" + count);
	        System.out.println("count1:"+count1);
	        int index = getStringIndex(msg, src, 2);
	        System.out.println("index:" + index);   //返回的游标值为
	        System.out.println("原文:"+msg);
	        String newMsg = msg.replace("fe01", "ff").replace("fe00", "fe");
	        long number = Long.parseLong(newMsg.substring(6, 8),16);
	        String hex_num = String.valueOf(number);  
	        long dec_num = Long.parseLong(hex_num, 16);  
	        System.out.println(dec_num);
	        //System.out.println(number);
	        String numberlen= String.valueOf(dec_num-count-count1);
	        String inToHex = intToHex(Integer.parseInt(numberlen));
	        
	        StringBuilder str = new StringBuilder(newMsg);
	        str.replace(6, 8, inToHex);
	        System.out.println("转义:"+str);		
	        		
	        byte[] bytes = {73, 32, 87, 97, 110, 32, 89, 111, 117, 32, 87, 97, 110, 32, 77, 101};//"I Wan You Wan Me"
	        byte[] bytes1={ 87, 97, 110};     //Wan
	        int count2=getByteCountOf(bytes,bytes1);
	        System.out.println("count2:" + count2); //返回的数量为：2

	        int index2=getByteIndexOf(bytes,bytes1,0);
	        System.out.println("index2:" + index2);   //返回的游标值为：2
	    }

	
	public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;            
        }
        a = s.reverse().toString();
        return a;
    }
	public static String str2Hex(String str) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < str.length(); i++) {
	        char c = str.charAt(i);
	        // 这里的第二个参数16表示十六进制
	        sb.append(Integer.toString(c, 16));
	        // 或用toHexString方法直接转成16进制
	        // sb.append(Integer.toHexString(c));
	    }
	    return sb.toString();
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

    //判断一个字符串source中，从指定的位置开始开始计算，字符串src的游标值
    private static int getStringIndex(String source, String src, int beginIndex) {
        int index = 0;
        int start = 0;
        while ((index = source.indexOf(src, start)) != -1 && index < beginIndex) {
            start = index + 1;
        }
        return index;
    }

    //判断一个byte数值在另外一个byte数组中对应的游标值
    public static int getByteIndexOf(byte[] sources, byte[] src, int startIndex) {
        return getByteIndexOf(sources, src, startIndex, sources.length);
    }


    //判断一个byte数值在另外一个byte数组中对应的游标值，指定开始的游标和结束的游标位置
    public static int getByteIndexOf(byte[] sources, byte[] src, int startIndex, int endIndex) {

        if (sources == null || src == null || sources.length == 0 || src.length == 0) {
            return -1;
        }

        if (endIndex > sources.length) {
            endIndex = sources.length;
        }

        int i, j;
        for (i = startIndex; i < endIndex; i++) {
            if (sources[i] == src[0] && i + src.length < endIndex) {
                for (j = 1; j < src.length; j++) {
                    if (sources[i + j] != src[j]) {
                        break;
                    }
                }

                if (j == src.length) {
                    return i;
                }
            }
        }
        return -1;
    }


    //判断一个byte数组src，在另一个byte数组source中存在的个数
    public static int getByteCountOf(byte[] sources, byte[] src) {
        if (sources == null || src == null || sources.length == 0 || src.length == 0) {
            return 0;
        }
        int count = 0;
        int start = 0;
        int index = 0;
        while ((index = getByteIndexOf(sources, src, start)) != -1) {
            start = index + 1;
            count++;
        }
        return count;
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
}
