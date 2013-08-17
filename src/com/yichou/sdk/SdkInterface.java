package com.yichou.sdk;

import android.app.Activity;
import android.content.Context;


/**
 * SDK 功能接口
 * 
 * @author Yichou
 *
 */
public interface SdkInterface {

	public void setUpdateCfg(boolean wifiOnly, boolean autoPopup);
	
	/**
	 * 刷新在线参数
	 */
	public void updateOnlineParams(Context context);
	
	/**
	 * 弹出有更新提示对话框，提示用户开始下载更新
	 * 
	 * @param info  更新信息
	 */
	public void showUpdateDialog(Context context, Object data);
	
	public void event(Context context, String id, String data);
	
	public void setCheckUpdateCallback(CheckUpdateCallback cb);
	
	public void setDownloadCallback(DownloadCallback cb);
	
	public void checkUpdate(Context context);
	
	public void onPause(Activity context);

	public void onResume(Activity context);
	
	public void enableCrashHandle(Context context, boolean enable);
}
