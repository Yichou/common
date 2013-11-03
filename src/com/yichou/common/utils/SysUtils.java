package cn.mrpyx.ads.common.utils;

import java.util.Locale;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
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
	
	public static final int NETWORK_TYPE_GSM = 1;
	public static final int NETWORK_TYPE_CDMA = 2;
	public static final int NETWORK_TYPE_CDMA2000 = 3;
	public static final int NETWORK_TYPE_WCDMA = 4;
	public static final int NETWORK_TYPE_TDSCDMA = 5;
	public static final int NERWORK_TYPE_WIFI = 0;

	enum Operator{
		UNKNOW,
		MOBILE,	//中国移动
		TELECOM,//中国电信
		UNICOM	//中国联通
	}

	enum Standard{
		TYPE_XX,//未知网络
		TYPE_2G,//2G网络
		TYPE_3G	//3G网络
	}
	

	public static boolean isNetAvailable(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        return (connectManager.getActiveNetworkInfo() != null);
	}
	
	public static boolean isWIFI(Context context) {
		return SysUtils.getNetworkType(context) == SysUtils.NETTYPE_WIFI;
	}
	
	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) { 
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();

			if (info != null 
					&& info.isConnected() 
					&& info.getState() == NetworkInfo.State.CONNECTED)
			{
				return info;
			}
		}
		
		return null;
	}
	
	public static int getNetworkType(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);

		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				Log.d("", "getNetworkType is WIFI.");
				return NETTYPE_WIFI;
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				String extInfo = info.getExtraInfo();
				if (extInfo != null && extInfo.toLowerCase(Locale.getDefault()).contains("wap")) {
					Log.d("", "getNetworkType is WAP.");
					return NETTYPE_WAP;
				} else {
					Log.d("", "getNetworkType is NET.");
					return NETTYPE_NET;
				}
			}
		}

		return NETTYPE_UNKNOW;
	}
	
	/**
	 * 判断当前连接的网络是 wifi或者3g
	 * 
	 * @return true 你不用担心用户流量了
	 */
	public static boolean isWifiOr3g(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);

		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			} else {
				return (getNetworkStandard(context) == Standard.TYPE_3G);
				
//				String apn = info.getExtraInfo().toLowerCase(Locale.getDefault());
//				if(apn != null 
//						&& (apn.contains("3g") || apn.contains("ctnet"))) {
//					Log.d("", "is 3g");
//					return true;
//				}
			}
		}
		
		return false;
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
	
	/**
	 * 获取当前连接的网络的 apn 名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkApn(Context context) {
		return getActiveNetworkInfo(context).getExtraInfo().toLowerCase(Locale.getDefault());
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
	
	/**
	 * 电话状态是否可读
	 * @param context
	 * @return
	 */
	private static boolean isPhoneStateReadable(Context context){
		PackageManager pm = context.getPackageManager();
		String pkgName = context.getPackageName();
		int readable = pm.checkPermission(permission.READ_PHONE_STATE, pkgName);
		
		return readable == PackageManager.PERMISSION_GRANTED;
	}
	
	/**
	 * 获取网络运营商
	 * @param context
	 * @return
	 */
	private static Operator getNetworkOperator(Context context) {
		if (!isPhoneStateReadable(context))
			return Operator.UNKNOW;
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if(imsi == null || imsi.length() < 10)
			return Operator.UNKNOW;
		
		int mcc = context.getResources().getConfiguration().mcc;
		if (mcc == 0) mcc = Integer.valueOf(imsi.substring(0, 3));
		int mnc = context.getResources().getConfiguration().mnc;
		if (mnc == 0) mnc = Integer.valueOf(imsi.substring(4, 5));
		if(mcc != 460)
			return Operator.UNKNOW;
		
		switch (mnc) {
		case 0:
		case 2:
		case 7:
			return Operator.MOBILE;
		case 1:
			return Operator.UNICOM;
		case 3:
			return Operator.TELECOM;
		default:
			return Operator.UNKNOW;
		}
	}

	/**
	 * 获取网络选项
	 * @param context
	 * @return
	 */
	private static Standard getNetworkStandard(Context context) {
		if (!isPhoneStateReadable(context))
			return Standard.TYPE_XX;
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		
		switch (tm.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case 15:
			return Standard.TYPE_3G;
			
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return Standard.TYPE_2G;
			
		default:
			return Standard.TYPE_XX;
		}
	}

	/**
	 * 获取网络类型 = 运营商 + 选项
	 * @param context
	 * @return
	 */
	public static final int getNetWorkType (Context context) {
		Operator operator = getNetworkOperator(context);
		Standard standard = getNetworkStandard(context);
		if( standard == Standard.TYPE_2G 
				&& (operator == Operator.MOBILE || operator == Operator.UNICOM) )
			return NETWORK_TYPE_GSM;
		if( standard == Standard.TYPE_2G 
				&& operator == Operator.TELECOM )
			return NETWORK_TYPE_CDMA;
		if( standard == Standard.TYPE_3G 
				&& operator == Operator.MOBILE )
			return NETWORK_TYPE_TDSCDMA;
		if( standard == Standard.TYPE_3G 
				&& operator == Operator.UNICOM )
			return NETWORK_TYPE_WCDMA;
		if( standard == Standard.TYPE_3G 
				&& operator == Operator.TELECOM )
			return NETWORK_TYPE_CDMA2000;
		return 0;
	}
}
