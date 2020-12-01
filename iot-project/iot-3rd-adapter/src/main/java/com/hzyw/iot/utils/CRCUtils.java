package com.hzyw.iot.utils;

import com.hzyw.iot.util.ByteUtils;
import com.hzyw.iot.util.constant.ConverUtil;

/**
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-23 11:35
 */
public class CRCUtils {
    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            buf[i] = bytes[i];
        }
        int len = buf.length;
        int crc = 0xFFFF;
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256;
            } else {
                crc ^= (int) buf[pos];
            }
            for (int i = 8; i != 0; i--) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        String c = Integer.toHexString(crc).toUpperCase();
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }
        return c;
    }

   /* public static boolean checkCRC(byte[] data, byte[] crc) {
        return ByteUtils.bytesToHexString(crc).equals(getCRC(data));
    }*/
    
    public static boolean checkCRC(byte[] data, byte[] crc) {
    	String datastr = ConverUtil.convertByteToHexString(data);
    	String crcstr = ConverUtil.convertByteToHexString(crc);
        return crcstr.equals(ConverUtil.makeChecksum(datastr));
    }
    
     
    
    public static void main(String[] s) {
    	String cshex = "68 00 00 00 00 01 00 68 01 07 45 00 00 02 00 04 E8";
    	String[] fulldata = cshex.split(" ");
    	byte[] b = new byte[fulldata.length];
    	int p = 0;
    	for(String ithex : fulldata){
    		b[p] = ByteUtils.hexToByte(ithex);
    		p++;
    	}
    	
    	int sum = 0;
    	int x1 = 0x68;
    	sum = sum + x1;
    	System.out.println( sum);
    	sum = sum + 0x00;
    	System.out.println( sum);
    	sum = sum + 0x01;
    	System.out.println( sum);
    	sum = sum + 0x68;
    	System.out.println( sum);
    	sum = sum + 0x02;
    	System.out.println( sum);
    	sum = sum + 0x70;
    	System.out.println( sum);
    	sum = sum + 0x03;
    	System.out.println(sum );
    	
    	//
    	System.out.println("--CRC="+makeChecksum(cshex));   //取低字节位 ，经过验证是准确的
    	 
    	
    }
    
    
    /**
     * 
     * 获取16进制累加和
     * 	如：hexstr = "68 00 00 00 00 00 01 68 00 01 73"
     * @param hexdata
     * @return
     */
    public static String makeChecksum(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return "00";
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return "00";
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        return hexInt(total);
    }
     
    private static String hexInt(int total) {
        int a = total / 256;
        int b = total % 256;
        if (a > 255) {
            return hexInt(a) + format(b);
        }
        return format(a) + format(b);
    }
     
    private static String format(int hex) {
        String hexa = Integer.toHexString(hex);
        int len = hexa.length();
        if (len < 2) {
            hexa = "0" + hexa;
        }
        return hexa;
    } 
    
      
}
