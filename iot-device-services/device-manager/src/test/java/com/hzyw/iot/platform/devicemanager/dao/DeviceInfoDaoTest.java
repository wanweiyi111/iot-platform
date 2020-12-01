package com.hzyw.iot.platform.devicemanager.dao;

import com.hzyw.iot.platform.devicemanager.caches.EquipmentCacheService;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceInfoDO;
import com.hzyw.iot.platform.devicemanager.mapper.device.DeviceInfoDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/31.
 */
@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ComponentScan(basePackages = {"com.hzyw.iot.platform"})
public class DeviceInfoDaoTest {
    @Resource
    private DeviceInfoDao deviceInfoDao;
    private DeviceInfoDO vo = new DeviceInfoDO();
    private DeviceInfoDO vv = new DeviceInfoDO();

    @Before
    public void setUp() throws Exception {

        vo.setDeviceId("sssdf-1023-sdf2-33");
        vo.setSerialNumber("sssfssss");
        vo.setMacAddr("3434:s22:323:232:2");
        vo.setBarCode("111");
        vo.setBatchNumber("23232s");
        vo.setDeviceName("速度");  //TODO: 中文乱码
        vo.setDeviceType("ssf");
        vo.setDeviceDomain(1123);
        vo.setExpireDate(new Date());
        vo.setManufacturerCode(8205);
        vo.setProductDate(new Date());

        vv.setDeviceId("23232-232-232");
        vv.setSerialNumber("sssfssss");
        vv.setDeviceName("were");
        vv.setDeviceDomain(2);
        vv.setDeviceType("rrr");
    }

    @Test
//    @Rollback(false)
    public void deviceInfoDaoCRUD() {
        deviceInfoDao.insertDeviceInfo(vo);
        deviceInfoDao.insertDeviceInfo(vv);

        DeviceInfoDO vo1 = deviceInfoDao.getDeviceInfoByID(vo.getDeviceId());
        Assert.assertEquals(vo.getBatchNumber(), vo1.getBatchNumber());
        Assert.assertEquals(vo.getDeviceName(), vo1.getDeviceName());
//        try {
//            String str = vo1.getDeviceId();
//            Assert.assertEquals(vo.getDeviceName(), new String(str.getBytes("utf8"),"gbk"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        vo.setMacAddr("111-111");
        deviceInfoDao.updateDeviceInfo(vo);
        Assert.assertEquals("111-111", deviceInfoDao.getDeviceInfoByID(vo.getDeviceId()).getMacAddr());

        List<DeviceInfoDO> list = deviceInfoDao.getDeviceInfoBySN(vo.getSerialNumber());
        Assert.assertEquals(2, list.size());
        System.out.println(list.get(0).getDeviceId());
        List tt = deviceInfoDao.getDeviceInfoByType("ssf", null);
        Assert.assertEquals(1, tt.size());
        List tt2 = deviceInfoDao.getDeviceInfoByType(null, null);
        Assert.assertEquals(1, tt.size());//vv's type wa null
        List tt3 = deviceInfoDao.getDeviceInfoByType("ssf", "sssfssss");
        Assert.assertEquals(1, tt.size());

        deviceInfoDao.updateDeviceSN(vv.getDeviceId(),"ABCDE");
        List<DeviceInfoDO> listl = deviceInfoDao.getDeviceInfoBySN("ABCDE");
        Assert.assertFalse(list.isEmpty());

        deviceInfoDao.deleteDeviceInfo(vo.getDeviceId());
        deviceInfoDao.deleteDeviceInfo(vv.getDeviceId());

    }


}
