package com.hzyw.iot.platform.devicemanager.caches;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAttrService;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.transfer.IllegalParameterException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    AttributeCacheService attributeCacheService;

    @Autowired
    DeviceAttrService attrService;


    @Before
    public void setUp() throws Exception {
//        DeviceAttributeDO vo = new DeviceAttributeDO();
//        DeviceAttributeDO vo2 = new DeviceAttributeDO();
//        vo.setAttrKey("VV");
//        vo.setAttrName("XXX");
//        vo.setUnit("V");
//        vo.setValueType("java.lang.String");
//        vo2.setAttrKey("V2");
//        vo2.setAttrName("XXX2");
//        vo2.setUnit("V2");
//        vo2.setValueType("java.lang.Integer");
//
//        try {
//            attrService.insertDeviceAttr(vo);
//            attrService.insertDeviceAttr(vo2);
//        } catch (IllegalParameterException e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void deviceAccessDaoCRUD() {
        DeviceAttribute a = attributeCacheService.get("agreement");
        DeviceAttribute rr = attributeCacheService.get("agreement");
//        attributeCacheService.evict("VV");
        attributeCacheService.get("agreement");

    }

    @After
    public void tearDown() throws Exception {
//        attrService.deleteDeviceAttr("VV");
//        attrService.deleteDeviceAttr("V2");
    }
}
