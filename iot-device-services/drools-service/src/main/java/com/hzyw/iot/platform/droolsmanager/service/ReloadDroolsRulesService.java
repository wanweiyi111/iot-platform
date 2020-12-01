package com.hzyw.iot.platform.droolsmanager.service;

import com.hzyw.iot.platform.droolsmanager.api.ReloadDroolsRules;
import com.hzyw.iot.platform.droolsmanager.domain.Rule;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
@Slf4j
@Service
public class ReloadDroolsRulesService {
    public static KieBase kieBase;

    @Autowired
    private ReloadDroolsRules ruleRepository;

    public  void reload(){
        loadContainerFromString(loadRules());
        //ReloadDroolsRulesService.kieBase = kieBase;
    }

    private List<Rule> loadRules(){
        List<Rule> rules = ruleRepository.findAll();
        return rules;
    }

    private KieBase loadContainerFromString(List<Rule> rules) {
       /* long startTime = System.currentTimeMillis();
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();*/
        KieHelper helper = new KieHelper();
        for (Rule rule:rules) {
            String  drl = rule.getContent();
            //springboot项目打包成Jar，实时更新规则，规则地址需要写成外部地址
            //kfs.write("src/main/resources/" + drl.hashCode() + ".drl", drl);
            try {
                helper.addContent(drl, ResourceType.DRL);
            }catch (Exception e){
                log.error("从数据库读取规则添加至规则库失败|{}|异常信息{}",drl,e.getMessage());
            }
        }
        try{
            kieBase = helper.build();
        }catch (Exception e) {
            log.error("规则初始化失败",e.getMessage());
            throw new RuntimeException("规则初始化失败");
        }
        log.info("{}条规则加入规则库",rules.size());
        /*KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time to build rules : " + (endTime - startTime)  + " ms" );
        startTime = System.currentTimeMillis();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        endTime = System.currentTimeMillis();
        System.out.println("Time to load container: " + (endTime - startTime)  + " ms" );
        return kContainer;*/
        return kieBase;
    }

    public void removeRule(String ruleKey){
        kieBase.removeRule("rules",ruleKey);
    }

    public Rule insertRule(Rule rule) {
        try{
            rule = ruleRepository.save(rule);
        }catch (RuntimeException e){
            log.error("添加规则异常{}",e.getMessage());
            throw new RuntimeException("添加规则异常");
        }
       return rule;
    }

    public void deleteRule(String ruleId){
        ruleRepository.deleteByRuleKey(ruleId);
    }

    public void selectRule(){
        Collection<org.kie.api.definition.rule.Rule> rules= kieBase.getKiePackage("rules").getRules();
    }

}
