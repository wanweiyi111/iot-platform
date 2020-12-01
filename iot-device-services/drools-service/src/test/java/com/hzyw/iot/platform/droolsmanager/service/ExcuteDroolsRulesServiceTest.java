package com.hzyw.iot.platform.droolsmanager.service;

import com.hzyw.iot.platform.droolsmanager.domain.BigScreen;
import com.hzyw.iot.platform.droolsmanager.domain.Light;
import com.hzyw.iot.platform.droolsmanager.domain.RuleResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExcuteDroolsRulesServiceTest {



        @Test
        public void testLinkage() {
                //Light light = new Light(1);
                //BigScreen bigScreen = new BigScreen(1,1);
                //light.setDeviceId("1123xsssxsda5124");
               /* KieSession kieSession = null;
                try {
                        kieSession = KieSessionUtils.newKieSession("linkage.drl");
                } catch (Exception e) {
                        e.printStackTrace();
                }*/
                //设置传入参数
                RuleResult  ruleResult = new RuleResult();
                ruleResult.setSourceDeviceId("1010-3f7b3eb6bffe6fb1-2009-ffff-0be7");
                ruleResult.setOnoff(1);
                ruleResult.setLevel(90);
                KieSession kieSession = ReloadDroolsRulesService.kieBase.newKieSession();
                kieSession.insert(ruleResult);
                int num = kieSession.fireAllRules();
                System.out.println("执行了"+num+"个规则");
               // System.err.println("规则执行完毕后目标动作变为了："+ruleResult.getExecuteActionName());
               // System.err.println("规则执行完毕后目标ID变为了："+ruleResult.getTargetDeviceId());
                kieSession.dispose();
        }


}