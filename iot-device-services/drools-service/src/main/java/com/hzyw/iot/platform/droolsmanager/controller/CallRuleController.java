package com.hzyw.iot.platform.droolsmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.droolsmanager.domain.Rule;
import com.hzyw.iot.platform.droolsmanager.service.ReloadDroolsRulesService;
import com.hzyw.iot.platform.droolsmanager.util.RuleUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
//@ResponseBody
@Api(value="规则controller",tags={"联动规则接口"})
@RequestMapping("/rule")
public class CallRuleController {
    @Resource
    private ReloadDroolsRulesService reloadDroolsRulesService;


    /*@RequestMapping("/linkage")
    public void ruleHandler(int num){

        BigScreen bigScreen = new BigScreen(1,1);
        Light light = new Light(1);
        KieSession kieSession = ReloadDroolsRulesService.kieBase.newKieSession();
        kieSession.insert(bigScreen);
        kieSession.insert(light);
        int ruleFiredCount = kieSession.fireAllRules();
        kieSession.dispose();
        log.info("触发了{}条规则",ruleFiredCount);

    }*/

    @ApiOperation(value="添加规则", notes="添加规则")
    @Transactional
    @RequestMapping(value = "/addrule",method = RequestMethod.POST)
    public String  addRule(@RequestBody String json)  {
        String msg = "fail";
        if(!StringUtils.isEmpty(json)){
            JSONObject jsonObject = JSONObject.parseObject(json);
            String ruleName = jsonObject.getString("ruleName");
            String ruleStr = RuleUtil.rule(jsonObject);
            String ruleKey = jsonObject.getString("ruleId");
            Rule rule = new Rule();
            rule.setContent(ruleStr);
            rule.setRuleName(ruleName);
            rule.setRuleKey(ruleKey);
            rule.setCreateTime(new Timestamp(System.currentTimeMillis()));
            //保存并重新加载规则
            try{
                rule = reloadDroolsRulesService.insertRule(rule);
                reload();
                msg = "success";
                log.info("添加规则成功,ruleId为",rule.getId());
            }catch (Exception e) {
            log.error("添加规则失败", e.getMessage());
            return msg;
            }
        }
        return msg;
    }

    /**
     * 删除规则
     *
     */
    @ApiOperation(value="删除规则", notes="删除规则")
    @ApiParam(value = "ruleId",name = "规则Id")
    @Transactional
    @RequestMapping(value = "/deleterule",method = RequestMethod.DELETE)
    public  void  deleteRule(@RequestParam(value = "ruleId",required = false) String ruleId){
        if(!StringUtils.isEmpty(ruleId)){
            try{
                reloadDroolsRulesService.deleteRule(ruleId);
                reload();
                log.info("删除规则成功,ruleId={}",ruleId);
            }catch (Exception e){
                log.error("删除规则失败",e.getMessage());
            }
        }

    }
    /**
     * 从数据加载最新规则
     * @return
     * @throws IOException
     */
    @ApiOperation(value="加载最新规则", notes="加载最新规则")
    @RequestMapping(value = "/reload", method= RequestMethod.GET)
    public String reload() {
        reloadDroolsRulesService.reload();
        return "ok";
    }


    /**
     * 生成随机数
     * @param num
     * @return
     */
    public String generateRandom(int num) {
        String chars = "0123456789";
        StringBuffer number=new StringBuffer();
        for (int i = 0; i < num; i++) {
            int rand = (int) (Math.random() * 10);
            number=number.append(chars.charAt(rand));
        }
        return number.toString();
    }


    /**
     * 组合业务规则Json方法
     *
     * @return 结果
     */
    private String ruleWorkMap(String name) {
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
