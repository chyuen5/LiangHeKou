package com.cizhu.application.Utils;

public class UrlUtil 
{

	public static int g_count = 0;
	
	// 接口访问地址：59.175.184.124:81
	//218.106.125.136
	//public static String HOST = "http://moa.cjwsjy.com.cn";
	//public static String HOST = "http://10.6.177.205:8080";
	//public static String HOST = "http://w802804.cjwsjy.com.cn:8080";
	public static String HOST = "http://218.106.125.140:8099";

	public static String CHECK_SYS = "/changjiang/checksys.mobo";
	public static String SERVICE_LOGIN_URL = "/changjiang/checkLogin.mobo";

	public static String TODO_MAIN = "/changjiang/todo/main.mobo?iswap=1&cmd=request";
	public static String NOTICE_MAIN = "/changjiang/notice/main.mobo?iswap=1&cmd=request";
	public static String NEWS_MAIN = "/changjiang/news/main.mobo?iswap=1&cmd=request";
	public static String GONGWEN_MAIN = "/changjiang/gongwen/main.mobo?iswap=1&cmd=request";
	public static String HYGL_MAIN = "/changjiang/hygl/main.mobo?iswap=1&cmd=request";
	public static String SEAL_MAIN = "/changjiang/sealManage/main.mobo?iswap=1&cmd=request";
	public static String CHUGUO_LIST = "/changjiang/chuguo/gongwuGenzongList.mobo?iswap=1&cmd=request";
	public static String ZICHAN_LIST = "/changjiang/zichang/zichanggjlcgzList.mobo?iswap=1&cmd=request";
	public static String MUBIAO = "/changjiang/mubiaozaixian.mobo?iswap=1&cmd=request";
	
	public static String SERVICE_EMPLOYEE_URL = "http://moa.cjwsjy.com.cn:81/cjydbg/outinterface!exculteContactList.action";
	public static String SERVICE_DEPARTMENT_URL = "http://moa.cjwsjy.com.cn:81/cjydbg/outinterface!exculteDepartmentList.action";
	public static String software_up_path = "http://moa.cjwsjy.com.cn:81/cjydbg/outinterface!checkVersion.action?type=1";

	//用户头像
	//public static String USER_IMAGE =HOST+"/CEGWAPServer/InterSource/getUserPic/";
	public static String USER_IMAGE =HOST+"/CEGWAPServer/InterSource/getUserPicByte/";
	
	//他人签名
	public static String USER_SIGN = HOST+"/CEGWAPServer/InterSource/getUserSignPicByte/";
	
	//自己的签名
	public static String MY_SIGN = HOST+"/CEGWAPServer/InterSource/getMySignPicByte/";
	
	//附件
	public static String ATTACHMENT = HOST+"/CEGWAPServer/InterSource/getAttFileByte/";
}
