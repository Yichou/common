package com.yichou.common.dl;

public abstract class DownloadListener implements IDownloadListener {
	public void onStart(long start, long total){
	}

	public void onProgress(long cur, byte prog){
	}

	public void onCancel(){
	}

	public void onFinish(){
	}
	
	public void onError(String msg) {
	}
}
