package com.hzyw.iot.platform.droolsmanager.template;

public class LinkageTemplate {
    /*
    String rule = "package com.neo.drools\r\n";
    rule += "import com.neo.drools.model.Message;\r\n";
    rule += "rule \"rule1\"\r\n";
    rule += "\twhen\r\n";
    rule += "Message( status == 1, myMessage : msg )";
    rule += "\tthen\r\n";
    rule += "\t\tSystem.out.println( 1+\":\"+myMessage );\r\n";
    rule += "end\r\n";

    package rules;
    import com.hzyw.iot.platform.droolsmanager.domain.RuleResult;
    rule "singleLinkage2"
	when
		$r:RuleResult( onoff == 1, level == 80 ,sourceDeviceId == "1010-3f7b3eb6bffe6fb1-2009-ffff-0be7");
	then
	    $r.setExecuteActionName("{\"method\": \"set_onoff\",\"in\": [{\"onoff\":1},{\"level\":80}]}");
	    $r.setTargetDeviceId("1030-3c474cf77e1a0376-200d-ffff-74a8");
    end
   /* stRule.add("condition", conStr);
        stRule.add("action", JSONObject.toJSONString(target));
        stRule.add("ruleId", ruleJsonTemplate.getRuleId());
        stFile.add("rules", stRule);
    */
    public static final String linkageST = "wordImport(rules) ::=<<\n" +
            "package rules;\n" +
            "\n" +
            "import\tcom.hzyw.iot.platform.droolsmanager.domain.RuleResult;\n" +
            "import\torg.slf4j.Logger;\n" +
            "import\torg.slf4j.LoggerFactory;\n"+
            "<rules; separator=\"\\n\\n\">\n" +
            ">>\n" +
            "\n" +
            "ruleValue(condition,action,rule) ::=<<\n" +
            "rule \"<rule>\"\n" +
            //"\tno-loop true\n" +
            "final\tLogger\tlogger\t=\tLoggerFactory.getLogger(\"执行规则id\"<rule>);\n"+
            "\t\twhen\n" +
            "\t\t    $r:RuleResult(<if(condition)><condition><endif>);\n" +
            " \t\tthen\n" +
            //"             <if(action)>$r.setExecuteActionName(\"<action>\");<endif>\n" +
            "             <if(action)>$r.getExecuteActionList().add(\"<action>\");<endif>\n" +
            //"              $r.addCount();\n"+
            "              logger.info(\"执行动作<action>\");\n"+
           // "             $r.setTargetDeviceId(<action.targetId>);<endif>\n" +
            //"           modify($r){\n" +
           // "                setPromoteName(drools.getRule().getName())<if(action)>,\n" +
           // "                setFinallyMoney($r.getMoneySum() - <action.money><endif>)\n" +
           // "           }\n" +
            "end\n" +
            ">>\n";

}
