package com.yichou.download;

public abstract class DownloadListener implements IDownloadListener {
	public void onStart(long total){
		
	}

	/**
	 * 下载过程回调
	 * 
	 * @param cur
	 * @param prog 0~100
	 */
	public void onProgress(long cur, byte prog){
	}

	public void onCancel(){
	}

	public void onFinish(){
		
	}
	
	/**
	 * 下载过程出异常
	 * 
	 * @param msg
	 */
	public void onError(String msg) {
		System.out.println(msg);
	}
}
