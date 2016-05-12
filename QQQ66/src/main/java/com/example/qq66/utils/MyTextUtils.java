package com.example.qq66.utils;

import android.text.TextUtils;

public class MyTextUtils {
	/**
	 * 有一个参数为空则为空
	 * @param parames
	 * @return
	 */
	public static boolean isEmpty(String... parames) {
		for (String str : parames) {
			if (TextUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}
}
