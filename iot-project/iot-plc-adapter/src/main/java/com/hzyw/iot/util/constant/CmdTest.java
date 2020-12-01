package com.hzyw.iot.util.constant;


import com.alibaba.fastjson.JSONObject;

/**
 *  PLC 协义接口
 * 测试类 main
 */
public class CmdTest {

    public static void main(String[] args){
        String responseParam="";  //模拟的响应参数
        try {
            //========================主机->集中器============请求=========================
            //System.out.println("========集中器继电器开（70H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","70H")); //ok
            //System.out.println("========集中器继电器关（71H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","71H")); //ok
            System.out.println("========查询集中器状态（73H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","73H","")); //oK
            //System.out.println("========下发定时任务（82H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","82H"));   //ok
            //System.out.println("========查询定时任务（83H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","83H"));   //ok

//            System.out.println("========清除定时任务（84H）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","84H"));
//            System.out.println("========设置集中器时间（8CH）请求测试 结果: "+ProtocalAdapter.batchRequestCode("00H","8CH"));

            /*JSONObject jsonObject=RequestVO.getReqVO();
            System.out.println("========节点调光(42H)请求测试 结果: "+ProtocalAdapter.messageRequest(jsonObject));*/

            //System.out.println("========查询节点详细数据(45H) 请求测试 结果: "+ProtocalAdapter.batchRequestCode("01H","45H"));

            /*JSONObject jsonObject=RequestVO.getReqVO();
            System.out.println("========查询节点详细数据(45H) 请求测试 结果: "+ProtocalAdapter.messageRequest(jsonObject));*/

            // 68000000000001680001fdcf16
            // 5.4.26查询集中器远程更新IP和端口(FDH)
            // System.out.println("========集中器继电器开（FDH）请求测试 结果:
            // "+ProtocalAdapter.batchRequestCode("00H","FDH")); //ok

            // 5.4.27 ok
            // 680000000000016800019a6c16
            // System.out.println("========集中器继电器开（9AH）请求测试 结果:
            // "+ProtocalAdapter.batchRequestCode("00H","9AH")); //ok

            // 5.4.28 查询集中器下PLC版本信息（9BH）
            // 680000000000016800019b6d16
            // System.out.println("========集中器继电器开（9BH）请求测试 结果:
            // "+ProtocalAdapter.batchRequestCode("00H","9BH")); //ok


            //=======================集中器->主机==============响应==========================
            //集中器继电器开（70H）  ok  //01H: 成功 02H: 失败, 03H：主机忙
           /* responseParam="680000000000016880027001c416";
            System.out.println("========集中器继电器开（70H）响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/

            //集中器继电器关(71H)  ok  //01H: 成功 02H: 失败, 03H：主机忙
           /* responseParam="680000000000016880027101c516";
            System.out.println("========集中器继电器关（71H）响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/

            //查询集中器状态(73H)   ok  // 成功： 返回VO, 02H:失败
        /*  responseParam="6800000000000168802273089808B608FC27102648251C086C085D0873193C62626362003DB3010EA622BA02E316";
            System.out.println("========查询集中器状态（73H）响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/

            //主动上报节点数据(F7H)   ok  // 成功： 返回VO, 02H:失败
            //1、路灯电源（设备码：00H~6FH）    2、路灯控制器（设备码：70H~7FH）
            //responseParam="6800000000010068044af7040000020004ee0a010933003f001e140000000000100014d10000000000000000000000000000100200b6000000000000000000000000000010020148000000000000000000000000de16";
            //responseParam="68000000000100680414f701000000000000000000000000000000000000e116";
           /* responseParam="6800000000010068044af7040000020004ee71010933003f001e140000000000100014d10000000000000000000000000000100200b60000000000000000000000000000100201480000000000000000000000004516";
            System.out.println("========主动上报节点数据(F7H) 响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/


            //集中器登录(F0H)   ok  // 成功： 返回状态码报文, 01:成功 02:失败
          /*responseParam="68000000000001680401f0c616";
            System.out.println("========集中器登录(F0H)响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/

             //节点调光(42H)     // 成功： 返回VO, 02H:失败
        /*    responseParam="6800000000000168800242019616";
             System.out.println("========节点调光(42H)响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/

            //查询节点详细数据(45H)     // 成功： 返回状态码报文, 01:成功 02:失败
          /*  responseParam="6800000000000168802345000001000156271b08a203b602f8062c065e61c800017016";
            System.out.println("========查询节点详细数据(45H) 响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));*/


            // -------------82H发送 和响应
            // 5.4.4 下发定时任务(82H) 无参数模板
            // responseParam="";
            // System.out.println("========查询集中器状态（82H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	//-----------F6H发送和响应
        	//5.4.20 报警使能查询(F6H) ok
//        	/68000000000001680001f6c816
        	//System.out.println("======== 报警使能查询(F6H)响应测试 结果: "+ProtocalAdapter.batchRequestCode("00H","F6H"));
        	
        	//5.4.20 报警使能查询(F6H) 无模板参数
        	
        	
        	
        	//-----------42H发送和响应
        	//节点调光(42H) ok
//        	System.out.println("========节点调光(42H)请求测试 结果:"+ProtocalAdapter.batchRequestCode("02H","42H"));
        	
        	//节点调光(42H)   ok
//           responseParam="6800000000000168800242019616";
//           System.out.println("========节点调光(42H)响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));
 
        	
        	
        	//-----------F7H集中器->主机
        	//5.4.22 主动上报节点数据(F7H)[无模板参数]
//        	responseParam = "";
//			System.out.println("========主动上报节点数据(F7H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	// -------------45H发送和响应
        	//5.4.23 查询节点详细数据(45H) ok
        	//68000000000001680007450000000F42DC4a16
//        	System.out.println("========设置集中器远程更新IP和端口(FBH)请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","45H"));
        	
        	//5.4.23 查询节点详细数据(45H) 好复杂没做
//        	responseParam = "";
//			System.out.println("========查询节点详细数据(45H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	// -------------FBH发送和响应
        	//5.4.24 查询和上传历史数据(FBH) ok
        	//68000000000001680002fb01cf16
//        	System.out.println("========设置集中器远程更新IP和端口(FBH)请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","FBH"));
        	
        	//5.4.24 查询和上传历史数据(FBH) 好复杂没做
//        	responseParam = "";
//			System.out.println("========查询和上传历史数据(FBH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	
        	// -------------FCH发送和响应
        	
        	//5.4.25 设置集中器远程更新IP和端口(FCH) 没做
//        	System.out.println("========设置集中器远程更新IP和端口(FCH)请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","FCH"));
        	
        	//5.4.25 设置集中器远程更新IP和端口(FCH) ok
//        	responseParam = "68000000000001688002FC015016";
//			System.out.println("========设置集中器远程更新IP和端口(FCH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	
        	// -------------FDH发送和响应
        	
        	//5.4.26 查询集中器远程更新IP和端口(FDH)
        	//68000000000001680001fdcf16
//        	System.out.println("========查询集中器远程更新IP和端口(FDH)请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","FDH"));
        	
        	//5.4.26 查询集中器远程更新IP和端口(FDH) 没做
//			responseParam = "";
//			System.out.println("======== 查询集中器远程更新IP和端口(FDH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	

        	// -------------9AH发送和响应
        	//680000000000016800019a6c16
//        	System.out.println("========读取集中器组网情况(9AH)请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","9AH"));
        	
        	//5.4.27 读取集中器组网情况9AH
//        	responseParam = "";
//			System.out.println("========读取集中器组网情况(9AH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	
        	// -------------9BH发送和响应
        	//680000000000016800019c6e16
//        	System.out.println("========查询集中器下PLC版本信息（9BH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","9CH"));

            //5.4.28 查询集中器下PLC版本信息（9BH） 
//			responseParam = "680000000000016880079B00b723fc00cb9416";
//			System.out.println("========查询集中器下PLC版本信息(9BH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------9CH发送和响应
            // 5.4.29集中器下PLC模块软件复位(9CH) ok
            // 680000000000016800019c6e16
//          System.out.println("========集中器下PLC模块软件复位（9CH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","9CH"));

            //5.4.29集中器下PLC模块软件复位(9CH)  ok
//			responseParam = "680000000000016880019Cee16";
//			System.out.println("========设置集中器继电器必须开启和关闭时间(9CH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

            // -------------60H发送和响应 ok
//             5.4.30 设置集中器继电器必须开启和关闭时间(60H) 无模板参数
//        	/6800000000000168001C6001093703ff093703ff01093703ff093703ff01093703ff093703ffdc16
//             System.out.println("========集中器继电器开（60H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","60H"));

            //5.4.30 设置集中器继电器必须开启和关闭时间(60H) ok
//			responseParam = "680000000000016880026001b416";
//			System.out.println("========设置集中器继电器必须开启和关闭时间(60H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

            // -------------61H发送和响应
            // 5.4.31查询集中器继电器必须开启和关闭时间(61H) ok
            // 68000000000001680001613316
//			 System.out.println("========集中器继电器开（61H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","61H"));

            //5.4.31查询集中器继电器必须开启和关闭时间(61H) 解析模板的时分格式需优化
//			responseParam = "6800000000000168800a610002da072602da07260102da072602da07260302da072602da0726fe16";
//			System.out.println("========查询集中器继电器必须开启和关闭时间(61H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------46H发送和响应
            // 5.4.32 查询节点传感器信息(46H) ok
            //68000000000001680107460000010001567716
//			 System.out.println("========集中器继电器开（46H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","46H"));
        	
            //5.4.32 查询节点传感器信息(46H)  传cs为76就ok，如果按照模板的cs为6A就会报错
//			responseParam = "6800000000000168801A4600000100015682010100E202910201000001640301000000087616";
//			System.out.println("========查询节点传感器信息(46H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------FEH响应    
            //5.4.33 节点传感器自动上报信息(FEH)  运行结果如下不知对不对 
        	//"节点ID":"16777558","设备码":"82H","节点传感器A编码":"257","传感器A数据":"14811793","节点传感器B编码":"513","传感器B数据":"356","节点传感器C编码":"769","传感器C数据":"8"
//			responseParam = "6800000000000168041bfe00000100015682010100e20291020100000164030100000008ee16";
//			System.out.println("========节点传感器自动上报信息(FEH)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	

            // -------------62H发送 和响应
            // 5.5.1 设置PLC-2480开始组网指令(62H) ok
            // 6800000000000168000262033816
//             System.out.println("========集中器继电器开（62H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","62H"));

            //5.5.1 设置PLC-2480开始组网指令(62H) ok
//			responseParam = "680000000000016880026201b616";
//			System.out.println("========设置PLC-2480开始组网指令(62H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------63H发送 和响应
            // 5.5.2 设置PLC-2480停止组网指令(63H) ok
            // 68000000000001680001633516
//             System.out.println("========设置PLC-2480停止组网指令（63H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","63H"));

            //5.5.2 设置PLC-2480停止组网指令(63H) ok
//			 responseParam = "680000000000016880026301b716";
//			 System.out.println("========设置PLC-2480停止组网指令(63H)请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------66H发送 和响应
            // 5.5.3 设置PLC-2480存储节点列表指令(66H) ok
            // 68000000000001680001663816
//             System.out.println("========集中器继电器开（66H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","66H"));

            //5.5.3 设置PLC-2480存储节点列表指令(66H) ok
//			 responseParam = "680000000000016880026601ba16";
//			 System.out.println("========设置PLC-2480存储节点列表指令(66H）请求测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------67H发送 和响应

            // 5.5.4 设置PLC-2480读取FLASH节点列表指令(67H) ok
            // 68000000000001680001673916
//             System.out.println("========设置PLC-2480读取FLASH节点列表指令（67H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","67H"));

            //5.5.4 设置PLC-2480读取FLASH节点列表指令(67H) ok
//			responseParam="680000000000016880026702bc16";
//			System.out.println("======== 设置PLC-2480读取FLASH节点列表指令（67H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------9EH发送 和响应
            // 5.5.5 增加单个节点(9EH)  ok的
//             6800000000000168010F9e 0000000f42dc 5F 82 0000000f42dd bb16
//			 System.out.println("========增加单个节点（9EH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","9EH"));

            //5.5.5 增加单个节点(9EH)  ok
//			responseParam="680000000000016880029e02f316";
//			System.out.println("======== 增加单个节点（9EH）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------9DH发送 和响应
            // 5.5.6 删除单个节点（9DH） ok
        	//680000000000016801079d0000000F42DCa316
//			 System.out.println("========集中器继电器开（9EH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","9DH"));

            //5.5.6 删除单个节点（9DH） ok
//			responseParam="680000000000016880029D02f216";
//			System.out.println("======== 删除单个节点（9DH）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

            // -------------69H发送 和响应
            // 5.5.7 设置PLC-2480删除存储节点列表指令（69H） ok
        	//68000000000001680001693b16
//             System.out.println("========设置PLC-2480删除存储节点列表指令（69H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","69H"));

            // 5.5.7 设置PLC-2480删除存储节点列表指令（69H） ok
//			 responseParam="680000000000016880026902be16";
//			 System.out.println("========设置PLC-2480删除存储节点列表指令（69H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

            // -------------4AH发送 和响应
            // 5.5.8 查询集中器软硬件信息(4AH) ok
        	//680000000000016800014a1c16
            // System.out.println("========查询集中器软硬件信息（4AH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","4AH"));

            // 5.5.8 查询集中器软硬件信息(4AH) ok
//        	responseParam = "6800000000000168800b4A0102000000000104cdb3012e16";
//			System.out.println("======== 查询集中器软硬件信息(4AH)响应测试 结果: " + ProtocalAdapter.batchResponseCode(responseParam));

            // -------------F8H发送 和响应
            // 5.5.9 设置集中器服务器IP和端口(F8H)  //todo 入参IP不知怎么传
            // System.out.println("======== 设置集中器服务器IP和端口（F8H）请求测试 结果:"+batchRequestCode("00H","F8H"));

            // 5.5.9 设置集中器服务器IP和端口(F8H) ok
//			responseParam = "68000000000001688002F8014c16";
//			System.out.println("======== 设置集中器服务器IP和端口（F8H）响应测试 结果: " + ProtocalAdapter.batchResponseCode(responseParam));

            // -------------F9H发送 和响应
            // 5.5.10 查询集中器服务器IP和端口(F9H) ok
        	//68000000000001680001f9cb16
//             System.out.println("======== 查询集中器服务器IP和端口（F9H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("00H","F9H"));

            // 5.5.10 查询集中器服务器IP和端口(F9H) 无参数模板
            // responseParam="";
            // System.out.println("======== 查询集中器服务器IP和端口（F9H）响应测试 结果:"+batchResponseCode(responseParam));

        	
        	// -------------6AH发送 和响应
			// 5.6.0 设定电源最大输出功率(6AH) ok
//        	680000000000016802096a000000000002000d5516
//			System.out.println("========设定电源最大输出功率（6AH）请求测试 结果: "+ProtocalAdapter.batchRequestCode("02H","6AH")); 
			
			// 5.6.0 设定电源最大输出功率(6AH) ok
//			 responseParam="680000000000016880026A02bf16";
//			 System.out.println("========设定电源最大输出功率（6AH）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

        	
        	
        	// -------------6BH发送 和响应
			
			// 5.6.1 查询电源最大功率(6BH) ok
			// 680000000000016802076b0000000000024716
//			 System.out.println("========查询电源最大功率（6BH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("02H","6BH"));
			
			// 5.6.1 查询电源最大功率(6BH) ok
//			 responseParam="680000000000016880096B00000000007000c8fd16";
//			 System.out.println("========查询电源最大功率(6BH)响应测试 结果: "+ProtocalAdapter.batchResponseCode(responseParam));
			
			// -------------6CH发送 和响应
			// 5.6.2 设定电源报警阙值(6CH) ok
        	//6800000000000168020D6c 000000000070 00dc 00d2 00d7 4116
//			 System.out.println("========设定电源报警阙值（6CH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("02H","6CH")); 
        	
			
			// 5.6.2 设定电源报警阙值(6CH) ok
//			 responseParam="680000000000016880026C02c116";
//			 System.out.println("========设定电源报警阙值（6CH）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

        	
            // -------------6DH发送 和响应

            // 5.6.3 查询电源报警阙值(6DH) ok
            // 680000000000016801076d000000000070b616
//             System.out.println("======== 查询电源报警阙值（6DH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","6DH"));

            // 5.6.3 查询电源报警阙值(6DH) ok
//        	responseParam="6800000000000168800d6D00000000007000dc00e600e1de16";
//            System.out.println("========查询电源任务编号(6FH)响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	

            // -------------6FH发送 和响应
            // 5.6.4 查询电源任务编号(6FH)  ok
            // 680000000000016801076f000000000070b816
//             System.out.println("========查询电源任务编号（6FH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","6FH"));

            // 5.6.4 查询电源任务编号(6FH) 解析模板有问题只有一个任务编号了
//        	responseParam="680000000000016880026F0000000000700201039d16";
//            System.out.println("========查询电源任务编号(6FH)响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));

            // -------------47H发送 和响应
            // 5.6.5 删除电源任务编号(47H) ok
            // 68000000000001680209470000000000111AEB3916
//             System.out.println("========删除电源任务编号（47H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("02H","47H"));

            // 5.6.5 删除电源任务编号(47H) ok
//             responseParam="6800000000000168800247039d16";
//             System.out.println("========删除电源任务编号（47H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            // -------------48H发送 和响应
             //5.6.6 查询电源一条定时任务(48H) ok
        	//6800000000000168010848 000000000070 03 9516
//             System.out.println("========查询电源一条定时任务（48H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","48H")); 

            // 5.6.6 查询电源一条定时任务(48H) 无参数模板
//            responseParam="";
//            System.out.println("========设定电源时间（49H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	

            // -------------49H发送 和响应
            // 5.6.7 设定电源时间(49H)  ok
        	 //6800000000000168020E49 000000000070 00000471f4b42c e316
//             System.out.println("========设定电源时间（49H）请求测试 结果:"+ProtocalAdapter.batchRequestCode("02H","49H")); 


            // 5.6.7 设定电源时间(49H) ok
//             responseParam="6800000000000168800249019d16";
//             System.out.println("========设定电源时间（49H）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));
        	
        	// -------------4BH发送 和响应

			// 5.6.8 查询电源时间(4BH) ok
			// 680000000000016801074b0000000000709416
//			 System.out.println("========查询电源时间（4BH）请求测试 结果:"+ProtocalAdapter.batchRequestCode("01H","4BH")); 
			
			// 5.6.8 查询电源时间(4BH)  680000000000016880024b019f16
//			 responseParam="680000000000016880124B0000060552210001bc83964268026f06d6f916";
//			 System.out.println("========查询电源时间（4BH）响应测试 结果:"+ ProtocalAdapter.batchResponseCode(responseParam));
	
			// -------------4CH发送 和响应
			// 5.6.9 设定电源初始化值(4CH) 
        	//6800000000000168020E4c 000000000070 0a 03e8 00c8 0064 be16
//			System.out.println("========设定电源初始化值（4CH）请求测试 结果: " + ProtocalAdapter.batchRequestCode("02H", "4CH")); // 多参数待完善
	
			// 5.6.9 设定电源初始化值(4CH) L 为08？？？
//        	responseParam="680000000000016880024C01a016";
//        	System.out.println("========设定电源初始化值（4CH）响应测试 结果:"+ProtocalAdapter.batchResponseCode(responseParam));


            /*StringBuffer hh=new StringBuffer("680000000001006804A4f7040000020004ee0a010933003f001e140000000000100014d10000000000000000000000000000100200b6000000000000000000000000000010020148000000000000000000000000");
            System.out.println("=====kkkkk=:"+ConverUtil.makeChecksumTest(hh));*/
        } catch (Exception e) {
            System.out.println("=====PLC Exception: "+e.getMessage());

            /* System.out.println(generaMessage("000000000100",null,"70H","03")); //集中器继电器开  T70H
            System.out.println(generaMessage("000000000001",null,"73H",null)); //查询集中器状态(73H)
            System.out.println(generaMessage("0122","01H","46H","000001000156")); //查询节点传感器信息
            System.out.println(generaMessage("000000000100","00H","66H",null));//设置PLC-2480存储节点列表指令(66H)*/
        }
    }


}
