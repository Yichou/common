package com.yichou.common.dl;

import java.io.File;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;

import com.yichou.common.utils.FileUtils;
import com.yichou.common.utils.SysUtils;

/**
 * 管理所有下载
 * 
 * @author Yichou 2013-8-9
 *
 */
public final class DownloadManager {
	static final Object mSync = new Object();
	
	private static final HashMap<String, Downloader> hashMap = new HashMap<String, Downloader>();
	
	
	public static void add(Downloader downloader) {
		synchronized (mSync) {
			hashMap.put(downloader.getFileSavePath(), downloader);
		}
	}
	
	public static void remove(Downloader downloader) {
		synchronized (mSync) {
			hashMap.remove(downloader.getFileSavePath());
		}
	}
	
	public static void stopAll() {
		synchronized (mSync) {
			for(Entry<String, Downloader> e : hashMap.entrySet()){
				e.getValue().cancel();
			}
		}
	}
	
	public static boolean isIdle() {
		synchronized (mSync) {
			return hashMap.isEmpty();
		}
	}
	
	public static boolean has(String fileSavePath) {
		synchronized (mSync) {
			return hashMap.containsKey(fileSavePath);
		}
	}
	
	/**
	 * 检查某个文件是否已经在下载了
	 * 
	 * @param fileSavePath
	 * @return
	 */
	public static boolean isFileDownloadIng(String fileSavePath) {
		if(has(fileSavePath)) //进程内
			return true;
			
		File lckFile = new File(fileSavePath + ".lck"); //进程间
		FileLock lock = FileUtils.tryFileLock(lckFile);
		if(lock == null)
			return true;
		FileUtils.freeFileLock(lock, lckFile);
		
		return false;
	}
	
	/**
	 * 启动一个下载（异步）
	 */
	public static Downloader start(Context context, String localPath, String serverPath, DownloadListener listener) {
		String dataDir = context.getFilesDir().getParent();
		
		if (!FileUtils.isSDAvailable(24) && !localPath.startsWith(dataDir)) {
			System.err.println("sdcard not available!");
			return null;
		}
		
		if(!SysUtils.isNetAvailable(context)) {
			System.err.println("net not available!");
			return null;
		}
		
		Downloader downloader = new Downloader(context, localPath, serverPath, listener);
		new Thread(downloader).start();
		
		return downloader;
	}
}
