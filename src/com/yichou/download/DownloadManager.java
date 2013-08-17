package com.yichou.download;

import java.io.File;
import java.nio.channels.FileLock;
import java.util.HashMap;

import android.content.Context;
import android.widget.Toast;

import com.yichou.common.FileUtils;

/**
 * 管理所有下载
 * 
 * @author Yichou 2013-8-9
 *
 */
public final class DownloadManager {
	private static final HashMap<String, Downloader> hashMap = new HashMap<String, Downloader>();
	
	public static void add(Downloader downloader) {
		hashMap.put(downloader.getFileSavePath(), downloader);
	}
	
	public static void remove(Downloader downloader) {
		hashMap.remove(downloader.getFileSavePath());
	}
	
	/**
	 * 检查某个文件是否已经在下载了
	 * 
	 * @param fileSavePath
	 * @return
	 */
	public static boolean isFileDownloadIng(String fileSavePath) {
		if(hashMap.containsKey(fileSavePath)) //进程内
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
	 * 
	 * @param context
	 * @param localPath 文件存储路径
	 * @param serverPath 下载路径
	 * @param listener 
	 * @return
	 */
	public static Downloader start(Context context, String localPath, String serverPath, DownloadListener listener) {
		if (!FileUtils.isSDMounted()) {
			Toast.makeText(context, "SD卡未挂载！", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		Downloader downloader = new Downloader(context, localPath, serverPath, listener);
		new Thread(downloader).start();
		
		return downloader;
	}
}
