package com.hzyw.iot.util;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.util.constant.ConverUtil;
 
/**
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-09 09:58
 */
public class ByteUtils {
  
    public static String bytesToHexString(byte[] src) {
        StringBuffer sb = new StringBuffer("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString();
    }

    public static byte[] hex2byte(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }
    
    
    
    /**
     * 字节数组转int,适合转高位在前低位在后的byte[]
     * 
     * @param bytes
     * @return
     */
    public static long byteArrayToLong(byte[] bytes) {  
        long result = 0;
        int len = bytes.length;
        if (len == 1) {
            long ch = (long) (bytes[0] & 0xff);
            result = ch;
        } else if (len == 2) {
            int ch1 = bytes[0] & 0xff;
            int ch2 = bytes[1] & 0xff;
            result = (short) ((ch1 << 8) | (ch2 << 0));
        } else if (len == 4) {
            int ch1 = bytes[0] & 0xff;
            int ch2 = bytes[1] & 0xff;
            int ch3 = bytes[2] & 0xff;
            int ch4 = bytes[3] & 0xff;
            result = (int) ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0));
        } else if (len == 8) {
            long ch1 = bytes[0] & 0xff;
            long ch2 = bytes[1] & 0xff;
            long ch3 = bytes[2] & 0xff;
            long ch4 = bytes[3] & 0xff;
            long ch5 = bytes[4] & 0xff;
            long ch6 = bytes[5] & 0xff;
            long ch7 = bytes[6] & 0xff;
            long ch8 = bytes[7] & 0xff;
            result = (ch1 << 56) | (ch2 << 48) | (ch3 << 40) | (ch4 << 32) | (ch5 << 24) | (ch6 << 16) | (ch7 << 8) | (ch8 << 0);
        }
        return result;
    }

    /**
     * int转byte[]，高位在前低位在后
     * 有符号的单个字节 127
     * 无符号的单个字节  数值范围 255
     * @param value
     * @return
     */
    public static byte[] varIntToByteArray(long value) {
        Long l = new Long(value); 
        byte[] valueBytes = null;
        if (l == l.byteValue()) {  //这里有点问题，如果是小于 255的int 会返回两个字节的数值哦 ，这里应该是当有符号的数值来处理了
            valueBytes = toBytes(value, 1);
        } else if (l == l.shortValue()) {
            valueBytes = toBytes(value, 2);
        } else if (l == l.intValue()) {
            valueBytes = toBytes(value, 4);
        } else if (l == l.longValue()) {
            valueBytes = toBytes(value, 8);
        }
        return valueBytes;
    }
    
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    } 
    /*public static void main(String[] args) throws Exception {
    	String hex = "c8";
    	 
    	byte[] temp = ConverUtil.hexStrToByteArr(hex); 
		long _temp14 = ByteUtils.byteArrayToLong(temp);  
		System.out.println("-------1--------------="+_temp14);
		 
    	
    	int plc_node_a_brightness = 118593;
    	System.out.println("-1-" + plc_node_a_brightness);
    	//System.out.println("-2-" + ConverUtil.convertByteToHexStr(x) );
    	System.out.println("-2-" + ConverUtil.convertByteToHexString(ByteUtils.intToByteArray(plc_node_a_brightness)) );
    	
    	System.out.println("-1-" + Integer.toHexString(plc_node_a_brightness) );
    	byte x =  (byte)(((plc_node_a_brightness)) & 0xFF); //强制转化为byte 取最低位字节
    	System.out.println("-2-" + ConverUtil.convertByteToHexStr(x) );
    	
    	x =  (byte)(((plc_node_a_brightness >> 8))); //强制转化为byte 取最低位字节 ,因为右移了8位 所以下面打印的肯定是右边数过来的第二个字节
    	System.out.println("-2-" + ConverUtil.convertByteToHexStr(x) );
    	 
    	
        // byte
        byte bMax = Byte.MAX_VALUE;
        byte bMin = Byte.MIN_VALUE;
        long b_max_v = byteArrayToLong(varIntToByteArray(bMax));
        check(bMax, b_max_v, "byte");
        long b_min_v = byteArrayToLong(varIntToByteArray(bMin));
        check(bMin, b_min_v, "byte");

        // short
        short sMax = Short.MAX_VALUE;
        short sMin = Short.MIN_VALUE;
        long s_max_v = byteArrayToLong(varIntToByteArray(sMax));
        check(sMax, s_max_v, "short");
        long s_min_v = byteArrayToLong(varIntToByteArray(sMin));
        check(sMin, s_min_v, "short");

       
		
		 
		
        //int
        int iMax = 36 ;
        int iMin = Integer.MIN_VALUE;
        long i_max_v = byteArrayToLong(varIntToByteArray(iMax));
        System.out.println("--------"+ConverUtil.convertByteToHexString(varIntToByteArray(iMax)));
        check(iMax, i_max_v, "int");
        long i_min_v = byteArrayToLong(varIntToByteArray(iMin));
        check(iMin, i_min_v, "int");

        // long
        long lMax = Long.MAX_VALUE;
        long lMin = Long.MIN_VALUE;
        long l_max_v = byteArrayToLong(varIntToByteArray(lMax));
        check(lMax, l_max_v, "long");
        long l_min_v = byteArrayToLong(varIntToByteArray(lMin));
        check(lMin, l_min_v, "long");
         
        
    } */
    private static void check(long s, long r, String tag) {
        if (s == r) {
            System.out.println(tag + "[result:" + r + "]");
        } else {
            System.err.println(tag + "[source:" + s + ",result:" + r + "]");
        }
    }
    
    private static byte[] toBytes(long value, int len) {
        byte[] valueBytes = new byte[len];
        for (int i = 0;i < len;i++) {
            valueBytes[i] = (byte) (value >>> 8 * (len - i - 1));
        }
        return valueBytes;
    } 
    
    //Double类型和进制制互转
    public static byte[] double2Bytes(double d) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[8];
		for (int i = 0; i < 8; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return byteRet;
	} 
    public static double bytes2Double(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
	} 
    /**
     * float转换byte
     *
     * @param bb
     * @param x
     * @param index
     */
    public static void putFloat(byte[] bb, float x, int index) {
        // byte[] b = new byte[4];
        int l = Float.floatToIntBits(x);
        for (int i = 0; i < 4; i++) {
            bb[index + i] = new Integer(l).byteValue();
            l = l >> 8;
        }
    }
     
    /**
     * 通过byte数组取得float
     *
     * @param bb
     * @param index
     * @return
     */
    public static float getFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    } 
    
    /*public static void main(String[] args) throws Exception {
    	byte[] temp = ConverUtil.hexStrToByteArr("0930"); 
    	long _temp11 = byteArrayToLong(temp) * 100;
    	System.out.println("----" + _temp11 );
    }*/
    
    /*private static void check(long s, long r, String tag) {
        if (s == r) {
            System.out.println(tag + "[result:" + r + "]");
        } else {
            System.err.println(tag + "[source:" + s + ",result:" + r + "]");
        }
    } 
    public static void main(String[] args) throws Exception {
        // byte
        byte bMax = Byte.MAX_VALUE;
        byte bMin = Byte.MIN_VALUE;
        long b_max_v = byteArrayToLong(varIntToByteArray(bMax));
        check(bMax, b_max_v, "byte");
        long b_min_v = byteArrayToLong(varIntToByteArray(bMin));
        check(bMin, b_min_v, "byte");

        // short
        short sMax = Short.MAX_VALUE;
        short sMin = Short.MIN_VALUE;
        long s_max_v = byteArrayToLong(varIntToByteArray(sMax));
        check(sMax, s_max_v, "short");
        long s_min_v = byteArrayToLong(varIntToByteArray(sMin));
        check(sMin, s_min_v, "short");

        //int
        int iMax = +8 ;
        int iMin = Integer.MIN_VALUE;
        long i_max_v = byteArrayToLong(varIntToByteArray(iMax));
        System.out.println(convertByteToHexString(varIntToByteArray(iMax)));
        check(iMax, i_max_v, "int");
        long i_min_v = byteArrayToLong(varIntToByteArray(iMin));
        check(iMin, i_min_v, "int");

        // long
        long lMax = Long.MAX_VALUE;
        long lMin = Long.MIN_VALUE;
        long l_max_v = byteArrayToLong(varIntToByteArray(lMax));
        check(lMax, l_max_v, "long");
        long l_min_v = byteArrayToLong(varIntToByteArray(lMin));
        check(lMin, l_min_v, "long");
         
        //double aa = Double.MAX_VALUE;
        //double bb = Double.MIN_VALUE;
        double aa = 3.1418;
        double bb = -20.3669;
        double r1 = bytes2Double(double2Bytes(aa));
         System.out.println("aa="+aa  + " /r1=" +r1 );
        double r2 = bytes2Double(double2Bytes(bb));
        System.out.println("bb="+bb  + " /r2=" +r2 );
    } */
 
    
    
}
