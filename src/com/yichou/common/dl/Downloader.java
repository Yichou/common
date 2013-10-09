package com.yichou.common.dl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.FileLock;

import android.content.Context;
import android.util.Log;

import com.yichou.common.utils.FileUtils;
import com.yichou.common.utils.HttpUtils;



/**
 * 下载器
 * 
 * @author Yichou 2013-9-9
 *
 */
public class Downloader implements Runnable {
	static final String TAG = "Downloader";
	
	public static final int RW_BUF_SIZE = 16*1024;
	
	private String fileSavePath;
	private String urlString;
	private IDownloadListener listener;
	private boolean canceled = false;
	private long mTotalLength, mCurLength;
	private Context mContext;
	
	
	public String getFileSavePath() {
		return fileSavePath;
	}
	
	public String getUrlString() {
		return urlString;
	}
	
	public Downloader(Context context, String fileSavePath, String urlString, DownloadListener listener) {
		this.fileSavePath = fileSavePath;
		this.urlString = urlString;
		this.listener = listener;
		this.mContext = context;
	}
	
	public void setDownloadListener(DownloadListener listener) {
		this.listener = listener;
	}

	/**
	 * 取消下载
	 */
	public void cancel() {
		canceled = true;
	}
	
	/**
	 * 获取一个 用于下载的 HttpURLConnection
	 * 
	 * @param urlString 地址
	 * @param startPos 起始位置
	 * @return
	 */
	public HttpURLConnection getDownloadConnection(String urlString, long startPos) {
		try {
			URL url = new URL(urlString);
			Proxy proxy = HttpUtils.getProxy(mContext);
			
			HttpURLConnection conn;
			if(proxy != null)
				conn = (HttpURLConnection) url.openConnection(proxy);
			else
				conn = (HttpURLConnection) url.openConnection();
			
			conn.setAllowUserInteraction(true);
			conn.setRequestProperty("User-Agent", "NetFox");
			conn.setReadTimeout(5*1000); //设置超时时间
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			conn.addRequestProperty("Range", "bytes=" + startPos + "-");
//			conn.addRequestProperty("Connection" , "Kepp-Alive"); 
			
			return conn;
		} catch (MalformedURLException e) {
		} catch (ProtocolException e) {
		} catch (IOException e) {
		}
		
		return null;
	}
	
	@Override
	public void run() {
		InputStream is = null;
		RandomAccessFile raf = null;
		HttpURLConnection conn = null;
		
		// 检查并创建父目录
		FileUtils.checkParentPath(fileSavePath);
		
		//文件加锁
		File lckFile = new File(fileSavePath + ".lck");
		FileLock lock = FileUtils.tryFileLock(lckFile);
		if(lock == null)
			return;

		try {
			DownloadManager.add(this);
			
			long startPos = 0;
			
			File file2 = new File(fileSavePath);
			if(file2.exists()) {
				if(listener != null) { //文件已存在，直接回调成功
					listener.onStart(mTotalLength, mTotalLength);
					listener.onFinish();
					return;
				}
//				if(file2.isDirectory()) //如果文件存在，删除重下
//					FileUtils.removeDir(file2);
//				else
//					file2.delete();
			} else { //目标文件不存在，下载到零时文件
				file2 = new File(fileSavePath + ".tmp"); //临时文件
				if(file2.exists()){
					if(file2.isFile()){ 
						startPos = file2.length(); //临时文件存在，获取其长度，续传
					} else { //存在，但不是文件，不会鬼吧？管他是什么删掉他
						startPos = 0;
						FileUtils.removeDir(file2);
					}
				}
			}
			
			raf = new RandomAccessFile(file2, "rw");
			raf.seek(startPos);
			
			conn = getDownloadConnection(urlString, startPos);
			if(conn == null){
				if(listener != null) listener.onError("open Connection FAIL!");
				return;
			}
			
			mTotalLength = conn.getContentLength();
			if(mTotalLength == -1){
				if(listener != null) listener.onError("get ContentLength FAIL!");
				return;
			}
			
			mTotalLength += startPos;
			mCurLength = startPos;
			
			int read = 0;
			byte buf[] = new byte[RW_BUF_SIZE];
			
			// 获取文件大小
			is = new BufferedInputStream(conn.getInputStream()); //使用 BufferedInputStream 提高性能
			//开始回调
			if(listener != null) 
				listener.onStart(startPos, mTotalLength);
			Log.d(TAG, "begin from " + startPos);

			do {
				read = is.read(buf);
				if(read == -1){ //下完了
					file2.renameTo(new File(fileSavePath)); //改名
					if(listener != null) 
						listener.onFinish();
					return; //下完了，直接返回
				}
				
				raf.write(buf, 0, read);
				mCurLength += read;
				
				if(listener != null) 
					listener.onProgress(mCurLength, (byte)((mCurLength/(float)mTotalLength)*100));
				
				Thread.sleep(50);
			} while (!canceled);// 点击取消就停止下载
			
			if(canceled){
				if(listener != null) 
					listener.onCancel();
			}
		} catch (Exception e) {
			if(listener != null) listener.onError(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			try {
				raf.close();
			} catch (Exception e) {
			}
			try {
				is.close();
			} catch (Exception e) {
			}

			if(conn != null)
				conn.disconnect();
			
			DownloadManager.remove(this);
			FileUtils.freeFileLock(lock, lckFile);
			Log.d(TAG, "end at " + mCurLength);
		}
	}
}
