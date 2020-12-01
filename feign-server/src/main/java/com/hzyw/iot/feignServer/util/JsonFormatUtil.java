package com.hzyw.iot.feignServer.util;
import com.alibaba.fastjson.JSON;


/**
 * create by zouxiongbin
 * 2019-08-07
 */

public class JsonFormatUtil {

    public static TransmitDataVO format(String string){
        MessageVO messageVO=new MessageVO();
        TransmitDataVO transmitDataVO =new TransmitDataVO();
        if (string!=null){
            transmitDataVO= JSON.toJavaObject(JSON.parseObject(string),TransmitDataVO.class);

            if (DataModelConstant.DEVINFORESPONSE.equals(transmitDataVO.getType())){
                transmitDataVO.getData().getAttributer().addAll(transmitDataVO.getData().getDefinedAttributer());
                transmitDataVO.getData().getMethods().addAll(transmitDataVO.getData().getDefinedMethod());
                transmitDataVO.getData().setDefinedAttributer(null);
                transmitDataVO.getData().setDefinedMethod(null);
            }
        }
         return transmitDataVO;
    }

}
