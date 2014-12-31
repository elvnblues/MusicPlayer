package com.hicsg.musicplayer.tools;

import android.util.Log;

/**
 * @author wangli Log工具类
 * 
 */
public final class LogUtils {

	private static boolean sIsLogEnabled = true;// 是否打开LOG

	private static String sApplicationTag = "MusicPlayerLog";// LOG默认TAG

	private static final String TAG_CONTENT_PRINT = "%s:%s.%s:%d";

	private static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];

	}

	//打印LOG
	public static void trace() {
		if (sIsLogEnabled) {
			android.util.Log.d(sApplicationTag,
					getContent(getCurrentStackTraceElement()));
		}
	}

	//获取LOG
	private static String getContent(StackTraceElement trace) {
		return String.format(TAG_CONTENT_PRINT, sApplicationTag,
				trace.getClassName(), trace.getMethodName(),
				trace.getLineNumber());
	}
	//打印默认TAG的LOG
	public static void traceStack() {
		if (sIsLogEnabled) {
			traceStack(sApplicationTag, android.util.Log.ERROR);
		}
	}

	// 打印Log当前调用栈信息
	public static void traceStack(String tag, int priority) {

		if (sIsLogEnabled) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			android.util.Log.println(priority, tag, stackTrace[4].toString());
			StringBuilder str = new StringBuilder();
			String prevClass = null;
			for (int i = 5; i < stackTrace.length; i++) {
				String className = stackTrace[i].getFileName();
				int idx = className.indexOf(".java");
				if (idx >= 0) {
					className = className.substring(0, idx);
				}
				if (prevClass == null || !prevClass.equals(className)) {

					str.append(className.substring(0, idx));

				}
				prevClass = className;
				str.append(".").append(stackTrace[i].getMethodName())
						.append(":").append(stackTrace[i].getLineNumber())
						.append("->");
			}
			android.util.Log.println(priority, tag, str.toString());
		}
	}
	//指定TAG和指定内容的方法
	public static void d(String tag, String msg) {
		if (sIsLogEnabled) {
			Log.d(tag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	//默认TAG和制定内容的方法
	public static void d(String msg) {
		if (sIsLogEnabled) {
			Log.d(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	//下面的定义和上面方法相同，可以定义不同等级的Debugger
	public static void i(String tag,String msg){
		if (sIsLogEnabled) {
			Log.i(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	public static void i(String msg){
		if (sIsLogEnabled) {
			Log.d(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	public static void w(String tag,String msg){
		if (sIsLogEnabled) {
			Log.w(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	public static void w(String msg){
		if (sIsLogEnabled) {
			Log.w(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	public static void e(String tag,String msg){
		if (sIsLogEnabled) {
			Log.e(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
	public static void e(String msg){
		if (sIsLogEnabled) {
			Log.e(sApplicationTag, getContent(getCurrentStackTraceElement())+">"+msg);
		}
	}
}