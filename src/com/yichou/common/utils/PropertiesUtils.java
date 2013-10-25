package com.yichou.common.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * 键值对存储工具
 * 
 * @author Yichou 2013-8-15
 *
 */
public final class PropertiesUtils extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 创建实例
	 * 
	 * @param is
	 * @return 失败返回 null
	 */
	public static PropertiesUtils create(InputStream is) {
		PropertiesUtils prop = new PropertiesUtils();
		try {
			prop.load(is);
			return prop;
		} catch (Exception e) {
		}
		
		return null;
	}
	
	private PropertiesUtils() {
	}
	
	public int getInt(String key, int def) {
		String value = getProperty(key); //null if it can't be found
		if(value != null) {
			try {
				return Integer.valueOf(value);
			} catch (Exception e) {
			}
		}
		return def;
	}
	
	public String getString(String key, String def) {
		return getProperty(key); //null if it can't be found
	}
	
	public boolean getBoolean(String key, boolean def) {
		String value = getProperty(key); //null if it can't be found
		if(value != null) {
			try {
				return Boolean.valueOf(value);
			} catch (Exception e) {
			}
		}
		return def;
	}
	
	public void set(String key, String value) {
		setProperty(key, value);
	}

	public void set(String key, int value) {
		setProperty(key, String.valueOf(value));
	}
	
	public void set(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}
}
