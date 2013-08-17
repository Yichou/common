package com.yichou.sdk;


public interface CheckUpdateCallback {
	/**
	 * 检测失败
	 */
	public static final int RET_FAILUE = 0;
	
	/**
	 * 没有更新
	 */
	public static final int RET_NO_NEW = 1;
	
	/**
	 * 有更新，可以弹出下载对话框
	 */
	public static final int RET_HAS_NEW = 2;
	
	/**
	 * 设置仅 wifi 更新，但是当前不是连的 wifi
	 */
	public static final int RET_NO_WIFI = 3;
	
	public void onCheckUpdateRet(int ret, Object data);
}
