package com.hzyw.iot.platform.devicemanager.service.opt;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.devicemanager.caches.TransactionSessionCacheService;
import com.hzyw.iot.platform.devicemanager.kafka.DeviceAccessProducer;
import com.hzyw.iot.platform.devicemanager.service.related.DeviceRelationService;
import com.hzyw.iot.platform.droolsmanager.domain.RuleResult;
import com.hzyw.iot.platform.droolsmanager.service.ExcuteDroolsRulesService;
import com.hzyw.iot.platform.models.rule.Target;
import com.hzyw.iot.platform.models.transfer.*;
import com.hzyw.iot.platform.util.json.MessageJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/16.
 */
@Slf4j
@Service
public class DeviceOptService {

    private final DeviceAccessProducer deviceAccessProducer;
    private final DeviceRelationService deviceRelationService;
    private final TransactionSessionCacheService transactionSessionCache;
    private final ExcuteDroolsRulesService excuteDroolsRulesService;

    public DeviceOptService(DeviceAccessProducer deviceAccessProducer, DeviceRelationService deviceRelationService, TransactionSessionCacheService transactionSessionCache, ExcuteDroolsRulesService excuteDroolsRulesService) {
        this.deviceAccessProducer = deviceAccessProducer;
        this.deviceRelationService = deviceRelationService;
        this.transactionSessionCache = transactionSessionCache;
        this.excuteDroolsRulesService = excuteDroolsRulesService;
    }

    //验证权限（放在网关）
    //发送审计日志（异步，放在网关）
    //验证方法和参数
    //处理逻辑（暂无）
    //组织报文
    //发送kafka
    public DeviceOptResponse doSyncOpt(DeviceOptRequest request) {
        return null;
    }

    public DeviceOptResponse doAsyncOpt(DeviceOptRequest request) {
        log.info("DeviceOptRequest.doAsyncOpt...");
        try {
            //安全审计切面 （今后要做成AOP注解）
            doAudit(request.getAudit());
            //验证方法和参数切面（今后要做成AOP注解）
            validateRequest(request);
        } catch (IllegalPermissionException | IllegalParameterException e) {
            DeviceOptResponse response = new DeviceOptResponse().setSuccess(false).setDown(false).setError(e);
            return response;
        }
        log.info("DeviceOptRequest.buildContext...");
//      构建事务上下文
        DeviceServiceContext requestContext = new DeviceServiceContext();
        requestContext.setTransactionId(UUID.randomUUID().toString());
        if (StringUtils.isEmpty(request.getCustomMsgId())) {
//            如果客户端不传自己的事务标识，则用系统的标识填充用户级ID；
            request.setCustomMsgId(requestContext.getTransactionId());
        }else{
            //TODO： 这里仅作为临时做法，平台是独立的中台，需要有自己的完整体系，两层ID的做法必不可少，但是要兼顾调用链的查错体系，从前到后的Trace也必不可少。
//            requestContext.setTransactionId(request.getCustomMsgId());
        }
        requestContext.setDeviceRequest(request);
        boolean b = transactionSessionCache.tryLock(requestContext.getTransactionId(), requestContext, 10, TimeUnit.MINUTES);
        if (!b) {
            log.error("Operation is repeat,tryLock: transId={} failed!", request.getCustomMsgId());
            IotError e = new DefaultDeviceException("BUSY", "Operation is running, Do not Repeat!");
            DeviceOptResponse response = new DeviceOptResponse().setSuccess(false).setDown(false).setError(e);
            return response;
        }
//      拆分设备
        List<String> deviceIds = request.getDeviceIds();
        for (String deviceId : deviceIds) {
            //查询从属关系
            String gwId = deviceRelationService.getEdgeGateWay(deviceId);
            if (StringUtils.isEmpty(gwId)) {
                continue;
            }
            //处理PLC的额外参数，看这方法名把，不想说话
            plcAdditionalHardCode(gwId,request.getMethod());
            //组织报文
            String jsonData = MessageJsonUtil.buildOptMsg(deviceId, requestContext.getTransactionId(), gwId, request.getMethod());
            //发送kafka
            log.error("OptJson is : {}", jsonData);
            deviceAccessProducer.sendDeviceOptMsg(jsonData);
            //联动规则
            executeRuler(deviceId, request, requestContext.getTransactionId());
        }
        log.error("DeviceOptRequest.endOpt,will return...");
        return new DeviceOptResponse().setSuccess(true).setDown(false).setRespObj(request.getCustomMsgId());
    }

    private void plcAdditionalHardCode(String gwId, Map<String, Map<String, Object>> method) {
        //TODO:最烦这种不按标准来的，大家都各自放飞自我，那北向的平台还玩个球，单播和组播都区分不了，还做个毛线网关？
        if (gwId.startsWith("2000-")) {//PLC集控器
            if (method.get("set_onoff") != null) {
                if (method.get("set_onoff").get("ab") == null) {
                    method.get("set_onoff").put("ab", "03");
                }
                if (method.get("set_onoff").get("code") == null) {
                    method.get("set_onoff").put("code", "01");
                }
                if(method.get("set_onoff").get("code") == null){

                }
            } else if (method.get("set_brightness") != null) {
                if (method.get("set_brightness").get("ab") == null) {
                    method.get("set_brightness").put("ab", "03");
                }
                if (method.get("set_brightness").get("code") == null) {
                    method.get("set_brightness").put("code", "01");
                }
            }
        }
    }

    @Async
    void executeRuler(String deviceId, DeviceOptRequest request, String transactionId) {
        log.error("Rule->executeRuler...");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RuleResult rule = new RuleResult();
        rule.setSourceDeviceId(deviceId);
        if (!StringUtils.isEmpty(request.getMethod().get("set_brightness"))) {
            rule.setLevel(new Integer(request.getMethod().get("set_brightness").get("level").toString()));
            rule.setOnoff(-1);
        } else if (!StringUtils.isEmpty(request.getMethod().get("set_onoff"))) {
            rule.setOnoff(new Integer(request.getMethod().get("set_onoff").get("onoff").toString()));
            if (StringUtils.isEmpty(request.getMethod().get("set_onoff").get("level"))) {
                rule.setLevel(-1);
            } else {
                rule.setLevel(new Integer(request.getMethod().get("set_onoff").get("level").toString()));
            }
        }

        RuleResult res = excuteDroolsRulesService.executeRule(rule);
        log.info("Rule->executeRuler RuleResult={}", res.getExecuteActionList().size());
        for(String action:res.getExecuteActionList()){
            if (!StringUtils.isEmpty(action)) {
                Target target = JSONObject.parseObject(action, Target.class);
                Map<String, Map<String, Object>> targetMethod = new HashMap();
                targetMethod.put(target.getMethodName(), target.getIn());
                String gwId = deviceRelationService.getEdgeGateWay(target.getTargetId());
                //处理PLC的额外参数，看这方法名把，不想说话
                plcAdditionalHardCode(gwId,targetMethod);
                if (!StringUtils.isEmpty(gwId)) {
                    String relTransId = UUID.randomUUID().toString();
                    String rel = MessageJsonUtil.buildOptMsg(target.getTargetId(), relTransId, gwId, targetMethod);
                    log.error("Rule->OptJson is : {}", rel);
                    deviceAccessProducer.sendDeviceOptMsg(rel);
                } else {
                    log.warn("not found gwId for this device: {}", deviceId);
                }
            }
        }
    }

    private void doAudit(Map<String, String> audit) throws IllegalPermissionException {

    }

    private void validateRequest(DeviceOptRequest request) throws IllegalParameterException {
        if (StringUtils.isEmpty(request.getCallbackUrl()) && StringUtils.isEmpty(request.getCallbackName())) {
            throw new IllegalParameterException("No CallbackUrI");
        }
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            throw new IllegalParameterException("No Target deviceIds");
        }
        if (request.getMethod() == null || request.getCallbackUrl().isEmpty()) {
            throw new IllegalParameterException("No Target methods");
        }
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            throw new IllegalParameterException("No target DeviceId!");
        }
    }
}
