package com.hzyw.iot.util.constant;

public class PLCValidateUtil {


    /**
     * 数值区间比较
     * @param current
     * @param min
     * @param max
     * @return
     */
    public static boolean rangeInDefined(int current, int min, int max)
    {
        return Math.max(min, current) == Math.min(current, max);
    }

    /**
     * 检查设备ID的16进制长度是否6个字节，不满足高位补0,超过报异常
     * @param uid
     * @return
     * @throws Exception
     */
    public static String checkDeviceUID(String uid) throws Exception{
        byte[]uidByte=ConverUtil.hexStrToByteArr(uid);
        if(uidByte.length>6) throw new Exception("PLC 设备UUID的16进制长度超过了6个字节!");
        String UUID=ConverUtil.convertUUIDByteToHexString(uidByte);
        System.out.println("===========222222:"+UUID);
        return UUID;
    }
}
