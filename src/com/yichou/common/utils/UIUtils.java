package com.yichou.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class UIUtils {

	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	public static int pix2dp(Resources r, int pix) {
		DisplayMetrics metrics = r.getDisplayMetrics();
		return Math.round(pix*metrics.density);
	}
	
	public static int revColor(int color) {
		int a = (color >> 24);
		int r = 0xFF - (color >> 16) & 0xFF;
		int g = 0xFF - (color >> 8) & 0xFF;
		int b = 0xFF - (color) & 0xFF;
		
		return ((a<<24)|(r<<16)|(g<<8)|b);
	}
}
