package com.hzyw.iot.util.constant;

/**
 * 设备码列表
 * @author jian
 *
 */
public enum D_CODE_VAL {
	//0~75W LED电源
	T00H("00","40W LED电源","LES-040","10"),
	T01H("01","40W LED电源","LES-040","10"),
	T02H("02","40W LED电源","LES-040","10"),
	T03H("03","40W LED电源","LES-040","10"),
	T04H("04","40W LED电源","LES-040","10"),
	T05H("05","40W LED电源","LES-040","10"),
	T06H("06","60W LED电源","LES-060","10"),
	T07H("07","60W LED电源","LES-060","10"),
	T08H("08","60W LED电源","LES-060","10"),
	T09H("09","60W LED电源","LES-060","10"),
	T0AH("0a","75W LED电源","LES-075","10"),
	T0BH("0b","75W LED电源","LES-075","10"),
	T0CH("0c","75W LED电源","LES-075","10"),
	T0DH("0d","75W LED电源","LES-075","10"),
	T0EH("0e","75W LED电源","LES-075","10"),
	T0FH("0f","75W LED电源","LES-075","10"),
	//75W~120W LED电源
	T10H("10","80W LED电源","LES-040","10"),
	T11H("11","80W LED电源","LES-040","10"),
	T12H("12","80W LED电源","LES-040","10"),
	T13H("13","80W LED电源","LES-040","10"),
	T14H("14","80W LED电源","LES-040","10"),
	T15H("15","90W LED电源","LES-040","10"),
	T16H("16","80W LED电源","LES-060","10"),
	T17H("17","80W LED电源","LES-060","10"),
	T18H("18","80W LED电源","LES-060","10"),
	T19H("19","100W LED电源","LES-060","10"),
	T1AH("1a","100W LED电源","LES-075","10"),
	T1BH("1b","100W LED电源","LES-075","10"),
	T1CH("1c","100W LED电源","LES-075","10"),
	T1DH("1d","120W LED电源","LES-075","30"),
	T1EH("1e","120W LED电源","LES-075","30"),
	T1FH("1f","120W LED电源","LES-075","30"),
	
	//120W~150W LED电源
	T20H("20","150W LED电源","LES-150","30"),
	T21H("21","150W LED电源","LES-150","30"),
	T22H("22","150W LED电源","LES-150","30"),
	T23H("23","150W LED电源","LES-150","30"),
	T24H("24","150W LED电源","LES-150","30"),
	T25H("25","150W LED电源","LES-150","30"),
	T26H("26","150W LED电源","LES-150","30"),
	T27H("27","150W LED电源","LES-150","30"),
	T28H("28","150W LED电源","LES-150","30"),
	T29H("29","150W LED电源","LES-150","30"),
	T2AH("2a","150W LED电源","LES-150","30"),
	T2BH("2b","150W LED电源","LES-150","30"),
	T2CH("2c","150W LED电源","LES-150","30"),
	T2DH("2d","150W LED电源","LES-150","30"),
	T2EH("2e","150W LED电源","LES-150","30"),
	T2FH("2f","150W LED电源","LES-150","30"),

	//150W~200W LED电源
	T30H("30","185W LED电源","LES-185","30"),
	T31H("31","185W LED电源","LES-185","30"),
	T32H("32","185W LED电源","LES-185","30"),
	T33H("33","185W LED电源","LES-185","30"),
	T34H("34","185W LED电源","LES-185","30"),
	T35H("35","185W LED电源","LES-185","30"),
	T36H("36","185W LED电源","LES-185","30"),
	T37H("37","185W LED电源","LES-185","30"),
	T38H("38","185W LED电源","LES-185","30"),
	T39H("39","185W LED电源","LES-185","30"),
	T3AH("3a","185W LED电源","LES-185","30"),
	T3BH("3b","185W LED电源","LES-185","30"),
	T3CH("3c","185W LED电源","LES-185","30"),
	T3DH("3d","185W LED电源","LES-185","30"),
	T3EH("3e","185W LED电源","LES-185","30"),
	T3FH("3f","185W LED电源","LES-185","30"),
	
	//200W~400W LED电源
	T40H("40","240W LED电源","LHG-240-P",""),
	T41H("41","240W LED电源","LHG-240-P",""),
	T42H("42","240W LED电源","LHG-240-P",""),
	T43H("43","240W LED电源","LHG-240-P",""),
	T44H("44","240W LED电源","LHG-240-P",""),
	T45H("45","240W LED电源","LHG-240-P",""),
	T46H("46","240W LED电源","LHG-240-P",""),
	T47H("47","240W LED电源","LHG-240-P",""),
	T48H("48","260W LED电源","LHG-260-P",""),
	T49H("49","260W LED电源","LHG-260-P",""),
	T4AH("4a","260W LED电源","LHG-260-P",""),
	T4BH("4b","260W LED电源","LHG-260-P",""),
	T4CH("4c","260W LED电源","LHG-260-P",""),
	T4DH("4d","260W LED电源","LHG-260-P",""),
	T4EH("4e","260W LED电源","LHG-260-P",""),
	T4FH("4f","260W LED电源","LHG-260-P",""),
	
	//HID 镇流器
	T50H("50","70W HID镇流器","HID-070","45"),
	T51H("51","70W HID镇流器","HID-070","45"),
	T52H("52","70W HID镇流器","HID-070","45"),
	T53H("53","70W HID镇流器","HID-070","45"),
	T54H("54","70W HID镇流器","HID-070","45"),
	T55H("55","70W HID镇流器","HID-070","45"),
	T56H("56","70W HID镇流器","HID-070","45"),
	T57H("57","70W HID镇流器","HID-070","45"),
	T58H("58","100W HID镇流器","HID-100","45"),
	T59H("59","100W HID镇流器","HID-100","45"),
	T5AH("5a","100W HID镇流器","HID-100","45"),
	T5BH("5b","100W HID镇流器","HID-100","45"),
	T5CH("5c","100W HID镇流器","HID-100","45"),
	T5DH("5d","100W HID镇流器","HID-100","45"),
	T5EH("5e","100W HID镇流器","HID-100","45"),
	T5FH("5f","100W HID镇流器","HID-100","45"),	
	T60H("60","150W HID镇流器","HID-150","45"),
	T61H("61","150W HID镇流器","HID-150","45"),
	T62H("62","150W HID镇流器","HID-150","45"),
	T63H("63","150W HID镇流器","HID-150","45"),
	T64H("64","150W HID镇流器","HID-150","45"),
	T65H("65","150W HID镇流器","HID-150","45"),
	T66H("66","150W HID镇流器","HID-150","45"),
	T67H("67","250W HID镇流器","250W HID镇流器","45"),
	T68H("68","250W HID镇流器","250W HID镇流器","45"),
	T69H("69","250W HID镇流器","250W HID镇流器","45"),
	T6AH("6a","250W HID镇流器","250W HID镇流器","45"),
	T6BH("6b","400W HID镇流器","400W HID镇流器","45"),
	T6CH("6c","400W HID镇流器","400W HID镇流器","45"),
	T6DH("6d","400W HID镇流器","400W HID镇流器","45"),
	T6EH("6e","400W HID镇流器","400W HID镇流器","45"),
	T6FH("6f","400W HID镇流器","400W HID镇流器","45"),
	
	//路灯控制器
	T70H("70","500W 单灯控制器","ODC-500-HP1",""),
	T71H("71","500W 单灯控制器","ODC-500-HP1",""),
	T72H("72","500W 单灯控制器","ODC-500-HP1",""),
	T73H("73","500W 单灯控制器","ODC-500-HP1",""),
	T74H("74","500W 单灯控制器","ODC-500-HP1",""),
	T75H("75","500W 单灯控制器","ODC-500-HP1",""),
	T76H("76","500W 单灯控制器","ODC-500-HP1",""),
	T77H("77","500W 单灯控制器","ODC-500-HP1",""),
	T78H("78","500W 单灯控制器","ODC-500-HP1",""),
	T79H("79","500W 单灯控制器","ODC-500-HP1",""),
	T7AH("7a","500W 双灯控制器","ODC-500-HP2",""),
	T7BH("7b","500W 双灯控制器","ODC-500-HP2",""),
	T7CH("7c","500W 双灯控制器","ODC-500-HP2",""),
	T7DH("7d","500W 双灯控制器","ODC-500-HP2",""),
	T7EH("7e","500W 双灯控制器","ODC-500-HP2",""),
	T7FH("7f","500W 双灯控制器","ODC-500-HP2",""),
	
	//传感采集器
	T80H("80","PM2.5、温湿度、光照采集模块","",""),
	T81H("81","PM2.5、温湿度、光照采集模块","",""),
	T82H("82","PM2.5、温湿度、光照采集模块","",""),
	T83H("83","PM2.5、温湿度、光照采集模块","",""),
	T84H("84","PM2.5、温湿度、光照采集模块","",""),
	T85H("85","PM2.5、温湿度、光照采集模块","",""),
	T86H("86","PM2.5、温湿度、光照采集模块","",""),
	T87H("87","PM2.5、温湿度、光照采集模块","",""),
	T88H("88","PM2.5、温湿度、光照采集模块","",""),
	T89H("89","PM2.5、温湿度、光照采集模块","",""),
	T8AH("8a","PM2.5、温湿度、光照采集模块","",""),
	T8BH("8b","PM2.5、温湿度、光照采集模块","",""),
	T8CH("8c","PM2.5、温湿度、光照采集模块","",""),
	T8DH("8d","PM2.5、温湿度、光照采集模块","",""),
	T8EH("8e","PM2.5、温湿度、光照采集模块","",""),
	T8FH("8f","PM2.5、温湿度、光照采集模块","","");
	
	 /**
	 * 设备码编号
	 */
	private String value;
	 /**
	 * 产品名称
	 */
	private String proName;
	 /**
	 * 产品型号
	 */
	private String proModel;
	 /**
	 * 调光下限值
	 */
	private String level;
	D_CODE_VAL(String value,String proName,String proModel,String level) {
	        this.value = value;
	        this.proName=proName;
	        this.proModel=proModel;
	        this.level=level;
	    }
	    
	    public String getValue() {
	        return value;
	    }


	    public String getProName() {
	        return proName;
	    }
	    
	    
	    /**
	     * 查询指定设备码编码值
	     * @param code
	     * @return
	     */
	    public static String DValueMethod(String code){
	        code=code==null?"NONE":code;
	        try {
	            code=code.toUpperCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String resCode="";
	        for (D_CODE_VAL cf : D_CODE_VAL.values()) {
	            if (cf.name().endsWith(code)) {
	                resCode=cf.value;
	                break;
	            }
	        }
	        return resCode;
	    }
	    
	    /**
	     * 查询指定设备码产品名称
	     * @param code
	     * @return
	     */
	    public static String DNameMethod(String code){
	        code=code==null?"NONE":code;
	        try {
	            code=code.toUpperCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String resCode="";
	        for (D_CODE_VAL cf : D_CODE_VAL.values()) {
	            if (cf.name().endsWith(code)) {
	                resCode=cf.proName;
	                break;
	            }
	        }
	        return resCode;
	    }
	    
	    /**
	     * 查询指定设备码产品型号
	     * @param code
	     * @return
	     */
	    public static String DModelMethod(String code){
	        code=code==null?"NONE":code;
	        try {
	            code=code.toUpperCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String resCode="";
	        for (D_CODE_VAL cf : D_CODE_VAL.values()) {
	            if (cf.name().endsWith(code)) {
	                resCode=cf.proModel;
	                break;
	            }
	        }
	        return resCode;
	    }
	    
	    /**
	     * 查询指定设备码 调光下限值
	     * @param code
	     * @return
	     */
	    public static String DLevelMethod(String code){
	        code=code==null?"NONE":code;
	        try {
	            code=code.toUpperCase();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String resCode="";
	        for (D_CODE_VAL cf : D_CODE_VAL.values()) {
	            if (cf.name().endsWith(code)) {
	                resCode=cf.level;
	                break;
	            }
	        }
	        return resCode;
	    }
}
