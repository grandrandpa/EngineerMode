package com.cdtsp.engineermode.util;

import android.util.Log;


public class LogUtils {
	private static final String EMPTY_TAG = "EngineerMode_";

	private static boolean isDebug = true;
	
	private static final String LOG = "log";
	private static final String D = "d";
	private static final String I = "i";
	private static final String E = "e";
	private static final String V = "v";
	private static final String W = "w";

	/**
	 * 
	 * @param tag 标签
	 * @param msg 输出消息
	 * @return NONE
	 * @exception throws NONE
	 */
	public static void d( String tag, String msg){
		if(isDebug()){
			tag = EMPTY_TAG + tag;
			Log.d(tag, msg);
		}
	}
	/** 
	 * @param tag 标签
	 * @param msg 输出消息
	 * @return NONE
	 * @exception throws NONE
	 */
	public static void i( String tag, String msg){
		if(isDebug()){
			tag = EMPTY_TAG + tag;
			Log.i(tag, msg);
		}
	}
	/** 
	 * @param tag 标签
	 * @param msg 输出消息
	 * @return NONE
	 * @exception throws NONE
	 */
	public static void v( String tag, String msg){
		if(isDebug()){
			tag = EMPTY_TAG + tag;

			Log.v(tag, msg);
		}
	}
	/** 
	 * @param tag 标签
	 * @param msg 输出消息
	 * @return NONE
	 * @exception throws NONE
	 */
	public static void e( String tag, String msg){
		tag = EMPTY_TAG + tag;
		Log.e(tag, msg);
	}
	/** 
	 * @param tag 标签
	 * @param msg 输出消息
	 * @return NONE
	 * @exception throws NONE
	 */
	public static void w( String tag, String msg){
		if(isDebug()){
			tag = EMPTY_TAG + tag;
			Log.w(tag, msg);
		}
	}
	/** 
	 * 读取是否输出环境变量的值
	 * @return NONE
	 * @exception throws NONE
	 */
	private static boolean isDebug(){
		return isDebug;
	}

}
