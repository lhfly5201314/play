package com.example.qq66.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	/**
	 * 返回当前时间的字符串形式 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getNowDateTime(){
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(pattern );
		return format.format(new Date());
	}
}
