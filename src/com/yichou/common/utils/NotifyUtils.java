package com.yichou.common.utils;

import java.io.Serializable;
import java.lang.reflect.Field;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;


/**
 * 显示通知工具类
 * 
 * @author Yichou 2013-9-25
 *
 */
public class NotifyUtils {
	public static final String INTENT_ACTION_NOTIFY_CLEAR = "com.android.nitifycation.clear";
	public static final String INTENT_ACTION_NOTIFY_CLICK = "com.android.nitifycation.click";
	public static final int ICON_SIZE = 64;
	public static Class<?> RECEIVER_CLASS;

	
	public static final class Notify implements Serializable {
		private static final long serialVersionUID = 4766996770033850347L;
		
		public boolean canClear;
		public int id;
		public int smallIconResId;
		public int titleColor;
		public String title;
		public String msg;
		public String iconUrl;
		public String tickerText;
		
		
		public Notify(int id, int smallIconResId, String title, String msg) {
			this.id = id;
			this.smallIconResId = smallIconResId;
			this.title = title;
			this.msg = msg;
		}
	}
	
	public static int id_icon, id_text, id_title;

	static {
		try {
			RECEIVER_CLASS = Class.forName("com.yichouangle.mpsq.ShellReceiver");
		} catch (Exception e) {
		}
		
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$id");

			id_icon = getIntField(clazz, "icon");
			id_text = getIntField(clazz, "text");
			id_title = getIntField(clazz, "title");
		} catch (Exception e) {
		}
	}
	
	public static int getIntField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getField(name);
			field.setAccessible(true);
			return field.getInt(null);
		} catch (Exception e) {
		}
		
		return 0;
	}
	
	/**
	 * 更新通知，不可清除类通知点击一次后，更新为可清除
	 * 
	 * @param ads
	 * @param activityIntent
	 * @param type
	 */
	public static void updateNotification(Intent activityIntent, Context context, Notify notify) {
		Notification n = new Notification(notify.smallIconResId, 
				notify.tickerText, 
				System.currentTimeMillis());
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		
		n.setLatestEventInfo(context, notify.title, notify.msg, contentIntent);
		if(n.contentView != null) {
			if(notify.titleColor != 0)
				n.contentView.setTextColor(id_title, notify.titleColor);
			if(!StringUtils.isNull(notify.iconUrl)) {
				Bitmap bmp = IconLoader.loadBitmap(context, notify.iconUrl, ICON_SIZE);
				if(bmp != null)
					n.contentView.setImageViewBitmap(id_icon, bmp);
			}
		}
		
		NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.notify(notify.id, n);
		mNM.notify(notify.id, n);
	}
	
	/**
	 * 首次显示用
	 * 
	 * @param ads
	 * @param clickIntent
	 * @param what
	 */
	public static void showNotification(Context context, Intent clickIntent, Notify notify) {
		Intent showIntent = new Intent(context, RECEIVER_CLASS);
		showIntent.setAction(INTENT_ACTION_NOTIFY_CLICK);
		
		showIntent.putExtra("intent", clickIntent);
		showIntent.putExtra("data", notify);
		
		Notification n = new Notification(notify.smallIconResId, 
				notify.tickerText, 
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, showIntent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		n.setLatestEventInfo(context, notify.title, notify.msg, contentIntent);
		n.flags |= notify.canClear? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_NO_CLEAR;
		
		if(n.contentView != null) {
			if(notify.titleColor != 0)
				n.contentView.setTextColor(id_title, notify.titleColor);
			if(!StringUtils.isNull(notify.iconUrl)) {
				Bitmap bmp = IconLoader.loadBitmap(context, notify.iconUrl, ICON_SIZE);
				if(bmp != null)
					n.contentView.setImageViewBitmap(id_icon, bmp);
			}
		}
		
		//清除回调
		Intent clearIntent = new Intent(context, RECEIVER_CLASS);
		clearIntent.setAction(INTENT_ACTION_NOTIFY_CLEAR);
		n.deleteIntent = PendingIntent.getBroadcast(context, 0, clearIntent, 0);

		NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.notify(notify.id, n);
		mNM.notify(notify.id, n); //有些手机要刷新一次才能看到颜色
	}
}
