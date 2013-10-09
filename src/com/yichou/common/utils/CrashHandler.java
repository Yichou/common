package com.yichou.common.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

/**
 * 
 * @author ?
 * 
 * @review Yichou 2013-4-7 14:38:51
 * 
 */
public final class CrashHandler implements UncaughtExceptionHandler {
	private static CrashHandler instance;
	
	// 程序的 Context 对象
	private Context context;
	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日期,作为日志文件名的一部分
//	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	// 确保外部无法创建对象，保证只有一个实例
	private CrashHandler() {
	}

	public static void init(Context context) {
		if(instance == null){
			instance = new CrashHandler();
			instance.context = context;
			// 获取系统默认的 UncaughtException 处理器
			instance.defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
			// 设置自己 为程序的默认异常处理器
			Thread.setDefaultUncaughtExceptionHandler(instance);
		}
	}

	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
		}

		// 通过反射获取Build类中的所有属性
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
	}

	/***
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称
	 */
	private void saveCrashInfo2File(Throwable ex) {
		if(!FileUtils.isSDMounted())
			return;

		StringBuffer sb = new StringBuffer();
		long timestamp = System.currentTimeMillis();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
		String time = formatter.format(new Date());
		String packageName = context.getPackageName();
		String fileName = "crash-" + time + "-" + timestamp + ".log";

		sb.append("FileName" + "=" + fileName + "\n");
		sb.append("PackageName" + "=" + packageName + "\n");

		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		byte[] data = sb.toString().getBytes();
		
		FileUtils.bytesToFile(new File(context.getExternalCacheDir(), "crash.log"), 
				data, 
				0, 
				0, 
				false);
	}

	/**
	 * 自定义异常处理，收集错误信息
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {

		if (ex == null) {
			return false;
		}
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "很抱歉，程序出现异常，即将退出!", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();

		// 收集设备参数信息
		collectDeviceInfo(context);
		
		// 保存到日志文件
//		saveCrashInfo2File(ex);
		
		return true;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		
		if (!handleException(ex) && defaultExceptionHandler != null) {
			// 如果没有处理则让系统默认的异常处理器来处理
			defaultExceptionHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
}

