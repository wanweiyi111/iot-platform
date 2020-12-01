package com.hzyw.iot.platform.util;

import com.hzyw.iot.platform.util.json.MessageJsonUtil;
import com.hzyw.iot.platform.util.json.SignalJsonData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/23.
 */
public class MessageJsonUtilTest {

    @Test
    public void contextLoads() {
        Assert.assertFalse(MessageJsonUtil.validMsg(null));

        Map m = new HashMap();
        Map m1 = new HashMap();
        Map m2 = new HashMap();
        m2.put("on",1);
        m2.put("level",33);
        m.put("mth1",m1);
        m.put("mth2",m2);
        System.out.println(MessageJsonUtil.buildOptMsg("2111","msg_Id","gw_Id",m));

        SignalJsonData signalData = new SignalJsonData();
        signalData.setSignalCode(101);
        signalData.setSignalName("ha-ha");
        signalData.setSignalMsg("you are wrong!");
        System.out.println(MessageJsonUtil.signalData2Json("2311",4114,signalData,21133311));


    }
}
