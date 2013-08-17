package com.yichou.sdk;

import android.app.Activity;
import android.content.Context;


/**
 * 
 * SDK 接口封装类
 * 
 * @author Yichou
 *
 */
public final class SdkUtils {
	private static SdkInterface realImpl;
	
	public static void setRealImpl(SdkInterface realImpl) {
		SdkUtils.realImpl = realImpl;
	}
	
	public static void setUpdateCfg(boolean wifiOnly, boolean autoPopup){
		if(realImpl == null) return;
		
		realImpl.setUpdateCfg(wifiOnly, autoPopup);
	}
	
	/**
	 * 刷新在线参数
	 */
	public static void updateOnlineParams(Context context) {
		if(realImpl == null) return;

		realImpl.updateOnlineParams(context);
	}
	
	/**
	 * 弹出有更新提示对话框，提示用户开始下载更新
	 * 
	 * @param info  更新信息
	 */
	public static void showUpdateDialog(Context context, Object info) {
		if(realImpl == null) return;
		
		realImpl.showUpdateDialog(context, info);
	}

	public static void event(Context context, String id, String data) {
		if(realImpl == null) return;
		
		realImpl.event(context, id, data);
	}
	
	public static void setCheckUpdateCallback(CheckUpdateCallback cb) {
		if(realImpl == null) return;
		
		realImpl.setCheckUpdateCallback(cb);
	}
	
	public static void setDownloadCallback(DownloadCallback cb) {
		if(realImpl == null) return;
		
		realImpl.setDownloadCallback(cb);
	}
	
	public static void checkUpdate(Context context) {
		if(realImpl == null) return;

		realImpl.checkUpdate(context);
	}
	
	public static void onPause(Activity context) {
		if(realImpl == null) return;

		realImpl.onPause(context);
	}

	public static void onResume(Activity context) {
		if(realImpl == null) return;

		realImpl.onResume(context);
	}
	
	public static void enableCrashHandle(Context context, boolean enable) {
		if(realImpl == null) return;
		
		realImpl.enableCrashHandle(context, enable);
	}
}
