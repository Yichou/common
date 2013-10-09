package com.yichou.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.res.Resources;


/**
 * 时间、日期、本地化 相关工具类
 * 
 * @author Yichou 2013-6-24
 *
 */
public final class TimeUtils {
	/**
	 * @return 当前地域
	 */
	public static Locale getLocale(){
		try {
			return Resources.getSystem().getConfiguration().locale;
		} catch (Exception e) {
		}
		return Locale.CHINA;
	}
	
	/**
	 * @return 形式  2013/12/12 12:23:32
	 */
	public static String getDateTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss", getLocale());
		return sdf.format(new Date());
	}
	
	/**
	 * @return 形式  2013/12/12 12:23:32
	 */
	public static String formatDate(long ms) {
		// 取系统时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", getLocale());
		return format.format(new Date(ms));
	}
	
	/**
	 * @return 形式  12:23:32
	 */
	public static String getTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", getLocale());
		return sdf.format(new Date());
	}
	
	public static int getDayOfYear() {
		return Calendar.getInstance(getLocale()).get(Calendar.DAY_OF_YEAR);
	}
	
	public static int getHourOfDay() {
		return Calendar.getInstance(getLocale()).get(Calendar.HOUR_OF_DAY);
	}
	
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}
