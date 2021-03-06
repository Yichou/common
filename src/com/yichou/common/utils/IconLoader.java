package com.yichou.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;

/**
 * SDK 统一图标加载工具
 * 
 * @author Yichou 2013-8-7
 * 
 */
public final class IconLoader {
	public static String ICON_STORE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

	public static Bitmap fitDpi(Resources res, Bitmap src, int size) {
		if (size == 0)
			return src;

		DisplayMetrics display = res.getDisplayMetrics();
		int srcW = src.getWidth();
		int dstW = (int) (display.densityDpi * size / 240.0);

		if (srcW != dstW) { // 缩放位图
			Bitmap tmpBitmap = Bitmap.createScaledBitmap(src, dstW, dstW, true);
			src.recycle();
			return tmpBitmap;
		}

		return src;
	}

	public static Bitmap fromLocal(Context context, String key) {
		String name = key;
		File file;

		if (FileUtils.isSDMounted()) {
			file = new File(ICON_STORE_PATH, name);
		} else {
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

	public static void toLocal(Context context, String key, Bitmap bitmap) {
		String name = key;
		File file;

		if (FileUtils.isSDMounted()) {
			file = new File(ICON_STORE_PATH, name);
		} else {
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

	/**
	 * 加载一张图片，一般用于应用图标（阻塞）
	 * 
	 * <p>本地采用 url 16 位 md5 字符串缓存，本地存在则取本地加载，否则从url加载。</p>
	 * 
	 * @param context
	 * @param url
	 *            图片下载地址
	 *            
	 * @param size 
	 * 			 图片缩放到多大，0 不缩放
	 * 
	 * @return 成功返回 bitmap ，失败返回 null
	 */
	public static Bitmap loadBitmap(Context context, String url, int size) {
		Bitmap bitmap = null;

		String key = CipherUtils.Md5Enc16(url.getBytes());

		bitmap = fromLocal(context, key);
		if (bitmap == null) {
			try {
				bitmap = BitmapFactory.decodeStream(new BufferedInputStream(new URL(url).openStream()));
				toLocal(context, key, bitmap);
			} catch (Exception e) {
				System.err.println("load bitmap fail " + e.getMessage());
			}
		}

		return (bitmap != null ? fitDpi(context.getResources(), bitmap, size) : null);
	}
}
