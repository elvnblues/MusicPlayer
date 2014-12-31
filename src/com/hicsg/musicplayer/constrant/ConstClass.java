package com.hicsg.musicplayer.constrant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.hicsg.musicplayer.R;
import com.hicsg.musicplayer.db.DBConst;
import com.hicsg.musicplayer.tools.LogUtils;

public class ConstClass {
	/**
	 * 保存String到ShareedPreferences中
	 */
	public static void setSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey,
			String sharedPerferencesVal) {
		SharedPreferences sharedPrefernces = context.getSharedPreferences(
				sharedPerferencesName, Activity.MODE_PRIVATE);
		sharedPrefernces.edit()
				.putString(sharedPerferencesKey, sharedPerferencesVal).commit();
	}

	/**
	 * 保存int到ShareedPreferences中
	 */
	public static void setSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey,
			int sharedPerferencesVal) {
		SharedPreferences sharedPrefernces = context.getSharedPreferences(
				sharedPerferencesName, Activity.MODE_PRIVATE);
		sharedPrefernces.edit()
				.putInt(sharedPerferencesKey, sharedPerferencesVal).commit();
	}

	/**
	 * 保存boolean到ShareedPreferences中
	 */
	public static void setSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey,
			boolean sharedPerferencesVal) {
		SharedPreferences sharedPrefernces = context.getSharedPreferences(
				sharedPerferencesName, Activity.MODE_PRIVATE);
		sharedPrefernces.edit()
				.putBoolean(sharedPerferencesKey, sharedPerferencesVal)
				.commit();
	}

	/**
	 * 保存float到ShareedPreferences中
	 */
	public static void setSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey,
			float sharedPerferencesVal) {
		SharedPreferences sharedPrefernces = context.getSharedPreferences(
				sharedPerferencesName, Activity.MODE_PRIVATE);
		sharedPrefernces.edit()
				.putFloat(sharedPerferencesKey, sharedPerferencesVal).commit();
	}

	/**
	 * 取出SharedPreferences中保存的数据(String)
	 * 默认值为空
	 */
	public static String getSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey) {
		String sharedPreferencesVal = context.getSharedPreferences(
				sharedPerferencesName, 0).getString(sharedPerferencesKey, "");
		return sharedPreferencesVal;
	}

	/**
	 * 取出ShareedPreferences中保存的数据(int)
	 * 默认值为0
	 */
	public static int getSharedPreferencesVal(String sharedPerferencesName,
			Context context, String sharedPerferencesKey) {
		if (context != null) {
			int sharedPreferencesVal = context.getSharedPreferences(
					sharedPerferencesName, 0).getInt(sharedPerferencesKey, 0);
			return sharedPreferencesVal;
		}
		return 0;
	}

	/**
	 * 取出ShareedPreferences中保存的数据(boolean)
	 * 默认值为false
	 */
	public static boolean getSharedPreferencesVal(String sharedPerferencesName,
			String sharedPerferencesKey, Context context) {
		if (context != null) {
			boolean sharedPreferencesVal = context.getSharedPreferences(
					sharedPerferencesName, 0).getBoolean(sharedPerferencesKey,
					false);
			return sharedPreferencesVal;
		}
		return false;
	}

	/**
	 * 取出ShareedPreferences中保存的数据(float)
	 * 默认值为0
	 */
	public static float getFloatSharedPreferencesVal(Context context,
			String sharedPerferencesName, String sharedPerferencesKey) {
		if (context != null) {
			float sharedPreferencesVal = context.getSharedPreferences(
					sharedPerferencesName, 0).getFloat(sharedPerferencesKey, 0);
			return sharedPreferencesVal;
		}
		return 0;
	}

	/**
	 * 保存Object类型
	 */
	public static void setObjectSharedPreferencesVal(Activity activity,
			Object model, String sharedPerferencesName,
			String sharedPerferencesKey) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			// 将对象放入OutputStream中
			oos = new ObjectOutputStream(baos);
			oos.writeObject(model);
			SharedPreferences listModelSharedPreferences = activity
					.getSharedPreferences(sharedPerferencesName,
							Activity.MODE_PRIVATE);
			// 将对象放入byte数组中，进行Base64为编码
			String listModelBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));
			SharedPreferences.Editor editor = listModelSharedPreferences.edit();
			// 将编码后的字符串写到名称为:sharedPerferencesKey文件中
			editor.putString(sharedPerferencesKey, listModelBase64);
			editor.commit();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 取出Object类型的
	 */
	public static Object getobjectSharedPreferencesVal(Activity activity,
			String sharedPerferencesName, String sharedPerferencesKey) {
		Object model = null;
		if (activity != null) {
			SharedPreferences listModelSharedPreferences = activity
					.getSharedPreferences(sharedPerferencesName,
							Activity.MODE_PRIVATE);
			String listModelBase64 = listModelSharedPreferences.getString(
					sharedPerferencesKey, "");
			if (listModelBase64 != null && listModelBase64 != "") {
				// 对Base64位进行解码
				byte[] base64Bytes = Base64.decodeBase64(listModelBase64
						.getBytes());
				ByteArrayInputStream bais = new ByteArrayInputStream(
						base64Bytes);
				try {
					ObjectInputStream ois = new ObjectInputStream(bais);
					model = ois.readObject();
				} catch (StreamCorruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return model;
	}

	/**
	 * 清空SharedPreferences
	 */
	@SuppressWarnings("static-access")
	public static void clearSharedPreferences(Context context,
			String sharedPreferencesName) {
		SharedPreferences spf = context.getSharedPreferences(
				sharedPreferencesName, context.MODE_PRIVATE);
		spf.edit().clear().commit();
	}
	
	/**
	  * 功能：检测当前URL是否可连接或是否有效,
	  * 描述：最多连接网络 3 次, 如果 3 次都不成功，视为该地址不可用
	  * @param  urlStr 指定URL网络地址
	  * @return URL
	  */
	 public static synchronized int isConnect(String urlStr) {
		 int counts = 0;
		 HttpURLConnection con;
		 int state = -1;
		 if (urlStr == null || urlStr.length() <= 0) {                       
			 return state;                 
		 }
		 while (counts < DBConst.REQUEST_COUNT) {
			 long start = 0;
			 try {
				 URL url = new URL(urlStr);
				 start = System.currentTimeMillis();
				 con = (HttpURLConnection) url.openConnection();
				 state = con.getResponseCode();
				 LogUtils.i("请求断开的URL一次需要:"+(System.currentTimeMillis()-start)+"毫秒");
				 if (state == 200) {
//					 state = "ok";
					 LogUtils.i("链接地址:{"+urlStr+",状态:可用}");
				 }else if(state == 404){
//					 retu = "404";
					 LogUtils.i("链接地址:{"+urlStr+",状态:地址404}");
				 }else{
					 LogUtils.i("链接地址:{"+urlStr+",状态:连接异常,错误代码："+state+"}");
				 }
				 break;
			 }catch (Exception ex) {
				 counts++; 
				 LogUtils.i("请求断开的URL一次需要:"+(System.currentTimeMillis()-start)+"毫秒");
				 LogUtils.i("连接第 "+counts+" 次，"+"链接地址:{"+urlStr+"--不可用,错误代码："+state+"}");
				 continue;
			 }
		 }
		 return state;
	 }
}
