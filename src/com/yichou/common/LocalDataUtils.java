package com.yichou.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.yichou.common.CipherUtils;
import com.yichou.common.FileUtils;

/**
 * 本地数据根据
 * 
 * @author Yichou
 *
 */
public final class LocalDataUtils {
	public static String ICON_STORE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	
	
	public static String genName(String key){
		return CipherUtils.Md5Enc16(key.getBytes());
	}

	public static void toLocal(Context context, String key, Bitmap bitmap) {
		String name = genName(key) + ".png";
		File file;
		
		if(FileUtils.isSDMounted()){
			file = new File(ICON_STORE_PATH, name);
		}else {
			file = context.getFileStreamPath(name);
		}
		
		FileUtils.checkParentPath(file);
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static Bitmap getBitmap(Context context, String key) {
		String name = genName(key) + ".png";
		File file;
		
		if(FileUtils.isSDMounted()){
			file = new File(ICON_STORE_PATH, name);
		}else {
			file = context.getFileStreamPath(name);
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		
		return null;
	}
	
	public static void toPrivate(Context context, String name, String string) {
		File file = context.getFileStreamPath(name);
		FileUtils.bytesToFile(file, string.getBytes());
	}
	
	public static String getStringFromPrivate(Context context, String name) {
		File file = context.getFileStreamPath(name);
		byte[] buf = FileUtils.fileToBytes(file);
		if(buf != null)
			return new String(buf);
		return null;
	}
}
