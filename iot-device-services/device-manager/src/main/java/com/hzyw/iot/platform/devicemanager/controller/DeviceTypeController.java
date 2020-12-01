package com.hzyw.iot.platform.devicemanager.controller;

import com.hzyw.iot.platform.devicemanager.domain.comm.ResultVO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceMethodDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceTypeDO;
import com.hzyw.iot.platform.devicemanager.domain.vo.DeviceTypeFormVO;
import com.hzyw.iot.platform.devicemanager.domain.vo.DeviceTypeInfoVO;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAttrService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceMethodService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceTypeService;
import com.hzyw.iot.platform.models.transfer.IllegalParameterException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备类型维护接口 （新增、修改、删除）
 */
@Slf4j
@RestController
@Api(value="设备类型维护controller",tags={"设备类型维护接口"})
@RequestMapping("/device/type")
public class DeviceTypeController {
    @Autowired
    DeviceTypeService typeService;
    @Autowired
    DeviceMethodService methodService;
    @Autowired
    DeviceAttrService attrService;

    /**
     * 设备类型新增
     * @param typeForm
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value="添加设备类型", notes="添加设备类型")
//    @Transactional
    public ResultVO<String> addDeviceType(@RequestBody DeviceTypeFormVO typeForm) {
        if (typeForm != null && typeForm.getTypeCode() != null &&
                typeForm.getManufacturerCode() != null && typeForm.getTypeDomain() != null) {
            DeviceTypeDO typeDO = new DeviceTypeDO();
            typeDO.setTypeCode(typeForm.getTypeCode());
            typeDO.setTypeName(typeForm.getTypeName());
            typeDO.setDeviceDomain(typeForm.getTypeDomain());
            typeDO.setManufacturerCode(typeForm.getManufacturerCode());
            typeService.insertDeviceType(typeDO); //插入设备类型
           if(typeForm.getAttrIds()!=null){
               DeviceMethodDO methodDO = new DeviceMethodDO();
               typeForm.getAttrIds().stream().forEach(e->{
                   attrService.saveAttrTypeRelation(typeForm.getTypeId(),e);//插入属性
               });
           }
           if(typeForm.getMethods()!=null){
               typeForm.getMethods().stream().forEach(e->{
                   DeviceMethodDO methodDO = new DeviceMethodDO();
                  methodDO.setMethodName(e.getMethodName());
                  methodDO.setMethodIn(e.getMethodIn());
                  methodDO.setMethodOut(e.getMethodOut());
                  methodDO.setMethodDescription(e.getDescription());
                   try {
                       methodService.saveMethod(methodDO);
                   } catch (IllegalParameterException ex) {
                        log.error(ex.getErrorMsg());
                   }
               });
           }
            return ResultVO.success("success");
        }else {
            return ResultVO.failed("Illegal Parameters! something required is null!");
        }
    }

    /**
     * 设备类型修改
     *
     * @param type
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value="更新设备类型", notes="更新设备类型")
    public Object updateDeviceType(@RequestBody DeviceTypeDO type) {
        try {
            //typeService.updateDeviceTypeByID(type);
            return ResultVO.success("更新成功!");
        } catch (Exception e) {
            return ResultVO.failed("更新失败! Exception: " + e.getMessage());
        }
    }

    /**
     * 设备类型删除
     *
     * @param type
     * @return
     */
    @DeleteMapping("/delete")
    @ApiOperation(value="删除设备类型", notes="删除设备类型")
    public Object deleteDeviceType(@RequestBody DeviceTypeDO type) {
        try {
            //typeService.deleteByID(type);
            return ResultVO.success("删除成功!");
        } catch (Exception e) {
            return ResultVO.failed("删除失败! Exception: " + e.getMessage());
        }
    }

    /**
     * 设备查询
     * @param type
     * @return
     */
    @GetMapping("/select")
    @ApiOperation(value="查询设备类型", notes="查询设备类型")
    public ResultVO<DeviceTypeInfoVO> findDeviceTypeInfo(@RequestBody DeviceTypeDO type){
        DeviceTypeInfoVO deviceTypeInfoVO = new DeviceTypeInfoVO();
        try{
            if(type!=null&&type.getDeviceDomain()!=null&&type.getTypeCode()!=null&&type.getManufacturerCode()!=null){
                DeviceTypeDO  deviceTypeDO =  typeService.selectDeviceType(type.getTypeCode(),type.getDeviceDomain(),type.getManufacturerCode());
                deviceTypeInfoVO.setTypeCode(deviceTypeDO.getTypeCode());
                deviceTypeInfoVO.setTypeDomain(deviceTypeDO.getDeviceDomain());
                deviceTypeInfoVO.setManufacturerCode(deviceTypeDO.getManufacturerCode());
                List<DeviceAttributeDO> deviceAttributeDOList = new ArrayList<DeviceAttributeDO>();
                //deviceAttributeDOList = typeService.selectDeviceAttrByDeviceType(deviceTypeDO);
                deviceAttributeDOList = attrService.searchDeviceAttrByType(deviceTypeDO.getTypeCode()+'-'+deviceTypeDO.getDeviceDomain()+'-'+deviceTypeDO.getManufacturerCode());
                List<DeviceMethodDO> deviceMethodDOList = new ArrayList<DeviceMethodDO>();
                deviceMethodDOList = methodService.getMethodListByType(deviceTypeDO.getTypeCode()+'-'+deviceTypeDO.getDeviceDomain()+'-'+deviceTypeDO.getManufacturerCode());
                deviceTypeInfoVO.setAttrs(deviceAttributeDOList);
                deviceTypeInfoVO.setMethods(deviceMethodDOList);
            }
        }catch (Exception e) {
            return ResultVO.failed("failed! Exception: " + e.getMessage());
        }
        return ResultVO.success(deviceTypeInfoVO);

    }
}
