package com.yichou.common.dl;

public interface IDownloadListener {
	/**
	 * 开始下载
	 * 
	 * @param total 总字节数
	 */
	public void onStart(long start, long total);
	
	/**
	 * 下载过程回调
	 * 
	 * @param cur 已下载字节数
	 * @param prog 进度 0~100
	 */
	public void onProgress(long cur, byte prog);
	
	/**
	 * 下载取消 
	 * 
	 * {@code}Downloader.cancel()
	 */
	public void onCancel();
	
	/**
	 * 下载完成
	 */
	public void onFinish();
	
	/**
	 * 下载过程出异常
	 * 
	 * @param msg
	 */
	public void onError(String msg);
}
