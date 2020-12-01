package com.hzyw.iot.utils.md5;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/10.
 */
public class DeviceIdGenerator {
    private static final String BLANK_RESERVE = "ffff";
    private static final String HYPHEN = "-";

    public static String generatorId(String serialNumber, int flagId, int manufacturer) {//SN   4112  12289
        if (StringUtils.isEmpty(serialNumber)||"NULL".equalsIgnoreCase(serialNumber)) {
            return null;
        }
        String md5id = MD5.md5_16(serialNumber);
        //十进制要转成十六进制
        String flag = Integer.toHexString(flagId).toLowerCase();
        String man = Integer.toHexString(manufacturer).toLowerCase();
        String crc = CRC16.crc16_modBus((flag + md5id + man + BLANK_RESERVE).getBytes());
        String str = flag + HYPHEN + md5id + HYPHEN + man + HYPHEN + BLANK_RESERVE + HYPHEN + crc;
        return str;
    }

    public static boolean validateDeviceId(String deviceId, String serialNumber) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(serialNumber)) {
            return false;
        }
        String[] str = deviceId.split(HYPHEN, 5);
        if (str.length != 5) {
            return false;
        }
        String tag = str[0] + str[1] + str[2] + str[3];
        if (!CRC16.crc16_modBus(tag.getBytes()).equals(str[4])) {
            //check crcCode
            return false;
        }
        if ("NULL".equalsIgnoreCase(serialNumber)) {
            // ignore validate
            return true;
        } else {
            //validate
            if (MD5.md5_16(serialNumber).equals(str[1])) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
    	System.out.println(generatorId("27.242.21.8",8194,12293));//ID,设备类型,厂家代码
	}
   
}
