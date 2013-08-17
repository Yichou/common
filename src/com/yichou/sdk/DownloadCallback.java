package com.yichou.sdk;

public interface DownloadCallback {
	public static final int RET_FAILUE = 0;
	public static final int RET_SUCCESS = 1;
	
	public void OnDownloadRet(int ret);
}
