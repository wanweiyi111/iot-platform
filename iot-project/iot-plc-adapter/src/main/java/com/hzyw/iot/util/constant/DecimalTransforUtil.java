package com.hzyw.iot.util.constant;

import java.math.BigInteger;

public class DecimalTransforUtil {
	public static byte CARD_NUM_BIT = 8;
	
	/**
     * isBlank 
     * 
     * @param value
     * @return true: blank; false: not blank
     */
    private static boolean isBlank(String value) {
        if (value == null || "".equals(value.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 10进制转16进制 (有符号)
     * @param var
     * @return
     */
    public static String toHexStrSign(int var){
        return Integer.toHexString(var);
    }

    /**
     * 16进制转10进制(有符号)
     * @param hex
     * @return
     */
    public static long hexToLongSign(String hex){
        BigInteger bi = new BigInteger(hex, 16);
        return bi.intValue();
    }

 
    /**
     * 10进制转16进制，并补齐指定位数
     * @param str
     * @param bitNum 字节长度， 16进制一个字节2位
     * @return
     */
    public static String toHexStr(String str,Integer bitNum) {
    	bitNum=bitNum*2;
        String result = "";
        String regex = "^\\d{1,}$";
        if (!isBlank(str)) {
            str = str.trim();
            if (str.matches(regex)) {
                String hexStr = Long.toHexString(Long.parseLong(str.trim())).toUpperCase();
                if (hexStr.length() < bitNum) {
                    hexStr = org.apache.commons.lang3.StringUtils.leftPad(hexStr, bitNum, '0');
                }
                result = hexStr;
            } else if (isHex(str)) {
                if (str.length() < bitNum) {
                    str = org.apache.commons.lang3.StringUtils.leftPad(str, bitNum, '0');
                }
                result = str;
            }
        }
        return result;
    }
    
    
    /**
     * 判断是否是16进制数
     * 
     * @param strHex
     * @return
     */
    public static boolean isHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }
 
/**
     * 计算16进制对应的数值
     * 
     * @param ch
     * @return
     * @throws Exception
     */
    private static int getHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }
 
/**
     * 计算幂
     * 
     * @param nValue
     * @param nCount
     * @return
     * @throws Exception
     */
    private static long getPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        long nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }
 
/**
     * 16进制转10进制，对16进制数的每一位数乘以其对应的16的幂，相加。
     * @param strHex 待转换的字符串
     * @param force 是否强制按16进制转换，纯数字也可能是16进制，true则将纯数字按16进制处理
     * @return
     */
    public static long hexToLong(String strHex, boolean force) {
        long nResult = 0;
        String regex = "^\\d{1,}$";
        if (!isBlank(strHex)) {
            strHex = strHex.trim();
        } else {
            return nResult;
        }
        if (!force && strHex.matches(regex)) {
            return Long.parseLong(strHex);
        }
        if (!isHex(strHex)) {
            return nResult;
        }
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (getHex(ch) * getPower(16, i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nResult;
    }
 
/**
     * 16进制转10进制
     * @param strHex
     * @return
     */
    public static long hexToLong(String strHex) {
        return hexToLong(strHex, false);
    }


    /**
     * 16进制转2进制
     * @param hex
     * @param bitNum
     * @return
     */
    public static String hexStringToByte(String hex,int bitNum) {
        int i = Integer.parseInt(hex, 16);
        String str2 = Integer.toBinaryString(i);
        if(bitNum>0){
            int lengthNum= bitNum - str2.length();
            for (int j = 0; j <lengthNum; j++) {
                str2 = "0".concat(str2);
            }
        }
        return str2;
    }
    /**
     * 16进制转2进制
     *
     * @param hex
     * @return
     */
    public static String hexStringToByte(String hex) {
        return hexStringToByte(hex,-1);
    }

    /**
     * 2进制转10进制
     *
     * @param bytes
     * @return
     */
    public static int ByteToDecimal(String bytes) {
        return Integer.valueOf(bytes, 2);
    }
    
    /**
     * 10进制转2进制
     * @param n
     * @return
     */
    public static String Demical2Byte(int n) {
        String result = Integer.toBinaryString(n);
        return result;
    }

    /**
     * 二进制数据转化为16进制字符串
     * @param src
     * @return
     */

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);

            stringBuilder.append(i + ":");

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + ";");
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param bytes
     * @return 将二进制转换为十六进制字符输出
     */
    public static String BinaryToHexString(byte[] bytes){
        String hexStr =  "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for(int i=0;i<bytes.length;i++){
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
            result +=hex+" ";
        }
        return result;
    }

    /**
     * 16进制字符串转换2进制字符串
     * @param hexString
     * @return
     */
    public static String HexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 2进制字符串转换6进制字符串
     * @param bString
     * @return
     */
    //------------------------------------------------------
    public static String BinaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp=new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    public static void main(String[] args){
       /* String str = "11111000";
        System.out.println("源字符串：\n"+str);

        String hexString = BinaryString2hexString(str);
        System.out.println("转换为十六进制：\n"+hexString);*/

       /* String aa="000002000533";

        String bb=DecimalTransforUtil.toHexStr(String.valueOf(DecimalTransforUtil.hexToLong(String.valueOf(aa), true)), 6);

        String cc=DecimalTransforUtil.toHexStr(aa,6);
        System.out.println(bb);
        System.out.println(bb);*/



        /*String aa="1b"; //7F
        Integer hh=1;
        System.out.println("======length:"+String.valueOf(hh).length());
        System.out.println("======:"+hexToLong(aa,true));
        System.out.println("======:"+hexToLong(aa,false));

        System.out.println("====11111==:"+DecimalTransforUtil.toHexStr(String.valueOf(hexToLong(aa,true)),1));
        System.out.println("===22222===:"+DecimalTransforUtil.toHexStr(String.valueOf(hexToLong(aa,false)),1));
        String bb=DecimalTransforUtil.toHexStr(String.valueOf(hexToLong(aa,true)),1);
        if(StringUtils.isNumeric(bb)){
            System.out.println("===333333===:"+new DecimalFormat("00").format(Integer.parseInt(bb)));
        }else{
            System.out.println("===333333===:"+bb);
        }*/

           /* int var = -23243;
            String hex = toHexStrSign(var);
            System.out.println(hex);
            System.out.println(hexToLongSign(hex));*/

           //hexToLong: 16->10 x
           //toHexStr:10->16
           //true:纯数字按16进制处理

            //String sizeVal=DecimalTransforUtil.toHexStr(String.valueOf(DecimalTransforUtil.hexToLong(String.valueOf(200),false)),1);
             // String sizeVal=DecimalTransforUtil.toHexStr(String.valueOf(DecimalTransforUtil.hexToLong(String.valueOf(20),false)),1);
        //String sizeVal=DecimalTransforUtil.toHexStr(String.valueOf(200),1);
            //String sizeVal = DecimalTransforUtil.toHexStr(String.valueOf(DecimalTransforUtil.hexToLong(String.valueOf(200), true)), 1);

        //long sizeVal =DecimalTransforUtil.hexToLong("200",false);

       String aa6= toHexStr("255",6);
        System.out.println("===:"+aa6);
    }

}
