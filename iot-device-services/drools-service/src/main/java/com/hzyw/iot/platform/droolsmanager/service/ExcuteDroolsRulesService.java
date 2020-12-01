package com.hzyw.iot.platform.droolsmanager.service;

import com.alibaba.fastjson.JSONObject;

import com.hzyw.iot.platform.droolsmanager.domain.RuleResult;
import com.hzyw.iot.platform.droolsmanager.util.KieSessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcuteDroolsRulesService {


    //@Autowired
    //KieSession kieSession;
  /*  @PostConstruct
    public void setUp() {
        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("group", "kjar", "0.0.1-SNAPSHOT");

        kContainer = ks.newKieContainer(releaseId);
        KieScanner kScanner = ks.newKieScanner(kContainer);

        // Start the KieScanner polling the Maven repository every 10 seconds
        kScanner.start(10000L);
    }*/



    public RuleResult executeRule(RuleResult ruleResult) {
        try {
            /*List cmdCondition = new ArrayList<>();
            cmdCondition.add(CommandFactory.newInsert(ruleResult));
            StatelessKieSession statelessKieSession = ReloadDroolsRulesService.kieBase.newStatelessKieSession();
            statelessKieSession.execute(CommandFactory.newBatchExecution(cmdCondition));*/
            //获取会话
            //KieSession kieSession = KieSessionUtils.newKieSession("linkage.drl");
            KieSession kieSession = ReloadDroolsRulesService.kieBase.newKieSession();
            //添加监听器，这里加的是对规则文件运行debug监听器，测试时最好加上，用于排查问题，生产上可视情况去掉
            kieSession.addEventListener(new DebugRuleRuntimeEventListener());
            //设置传入参数
            kieSession.insert(ruleResult);
            FactHandle handle = kieSession.insert(ruleResult);
            //设置全局参数
            //kieSession.setGlobal("addObject",addObject);
            //执行规则
            int ruleFiredCount = kieSession.fireAllRules();
            log.info("成功匹配执行了{}条规则",ruleFiredCount);
            kieSession.delete(handle);
            //释放会话资源
            kieSession.dispose();

        } catch (Exception e) {
            log.error("规则执行异常,SourceDeviceId={}",ruleResult.getSourceDeviceId(),e.getMessage());
            e.printStackTrace();
            ruleResult.setMsg("规则执行异常,Fact:SourceDeviceId="+ruleResult.getSourceDeviceId());
            return ruleResult;
        }
        return ruleResult;
    }

    /**
     * 组合业务规则Json方法
     *
     * @return 结果
     */
    public String ruleWorkMap(String name) {
        Map<String, Object> map = new HashMap<>();
        //组合Rule部分
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        map.put("rule", rule);
        //组合 规则When部分
        Map<String, Object> when = new HashMap<>();
        map.put("condition", when);
        //组合 规则Then部分
        Map<String, Object> then = new HashMap<>();
        map.put("action", then);
        //组合规则When And Then 部分
        return JSONObject.toJSONString(map);
    }

}
