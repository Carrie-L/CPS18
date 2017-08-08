package com.adsale.ChinaPlas.utils;

import android.util.Log;
/**
 * 封装log: 修改LEVEL的值。LEVEL的值小于Log级别，打印Log日志；LEVEL的值大于Log级别，不打印
 * @author Carrie
 *
 */

public class LogUtil {
	/**true : 打印Log ; false :不打印*/
	public static final boolean LOG=ReleaseHelper.LOG_OPEN;
	public static final String PREFIX="CPS:";

	public static void v(String tag,String msg){
		if(LOG)
			Log.v(PREFIX+tag,msg);
	}
	public static void d(String tag,String msg){
		if(LOG)			//LEVEL<=DEBUG
			Log.d(PREFIX+tag,msg);
	}
	public static void i(String tag,String msg){
		if(LOG){			
			Log.i(PREFIX+tag,msg);
		}
	}
	public static void w(String tag,String msg){
		if(LOG){
			Log.w(PREFIX+tag,msg);
		}
	}
	public static void e(String tag,String msg){
		if(LOG){
			Log.e(PREFIX+tag,msg);
		}
	}

}
