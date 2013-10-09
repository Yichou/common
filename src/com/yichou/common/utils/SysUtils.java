package com.yichou.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * 网络操作工具集合
 * 
 * @author Yichou
 *
 */
public final class SysUtils {
	public static final int NETTYPE_WIFI=0,
		NETTYPE_WAP=1, //代理
		NETTYPE_NET=2, //直连
		NETTYPE_UNKNOW=3;
	
	public static final int NET_ID_MOBILE=0,                  //移动
	   NET_ID_CN=1,          // 联通gsm
	   NET_ID_CDMA=2,       //联通CDMA
	   NET_ID_NONE=3,       //未插卡
	   NET_ID_OTHER=4;     /*其他网络*/

	public static boolean isNetAvailable(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        return (connectManager.getActiveNetworkInfo() != null);
	}
	
	public static boolean isWIFI(Context context) {
		return SysUtils.getNetworkType(context) == SysUtils.NETTYPE_WIFI;
	}
	
	public static int getNetworkType(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) { 
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();

			if (info != null && info.isConnected()) {
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					if(info.getType() == ConnectivityManager.TYPE_WIFI){
						Log.d("", "getNetworkType is WIFI.");
						return NETTYPE_WIFI;
					}else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
						String extInfo = info.getExtraInfo();
						if(extInfo != null && extInfo.toLowerCase().contains("wap")){
							Log.d("", "getNetworkType is WAP."); 
							return NETTYPE_WAP;
						}else {
							Log.d("", "getNetworkType is NET.");
							return NETTYPE_NET;
						}
					}
				}
			}
		}
		
		return NETTYPE_UNKNOW;
	}
	
	/**
	 * 获取网络类型，返回字符串
	 * 
	 * <li>wifi</li>
	 * <li>wap</li>
	 * <li>net</li>
	 * <li>non</li>
	 * 
	 * @param context
	 * @return
	 */
	public static String getStringNetworkType(Context context) {
		int ret = getNetworkType(context);
		if(ret == NETTYPE_WIFI)
			return "wifi";
		else if(ret == NETTYPE_WAP)
			return "wap";
		else if(ret == NETTYPE_NET)
			return "net";
		else
			return "non";
	}
	
	public static int getNetworkID(Context context) {
		String str = getImsi(context);

		if (str == null)
			return NET_ID_OTHER; //返回 NULL 会导致未插卡不能运行

		if ((str.regionMatches(0, "46000", 0, 5))
				|| (str.regionMatches(0, "46002", 0, 5))
				|| (str.regionMatches(0, "46007", 0, 5)))
			return NET_ID_MOBILE;
		else if (str.regionMatches(0, "46001", 0, 5))
			return NET_ID_CN;
		else if (str.regionMatches(0, "46003", 0, 5))
			return NET_ID_CDMA;
		else
			return NET_ID_MOBILE; //返回 NULL 会导致未插卡不能运行
	}
	
	/**
	 * 获取手机IMSI
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		if (phoneManager != null) {
			String ret = phoneManager.getSubscriberId();
			if(ret != null && ret.length()>0)
				return ret;
		}

		return null;
	}
	
	public static String getPhoneNumber(Context context) {
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		if (phoneManager != null) {
			String ret = phoneManager.getLine1Number();
			if(ret != null && ret.length()>0)
				return ret;
		}

		return null;
	}
}
