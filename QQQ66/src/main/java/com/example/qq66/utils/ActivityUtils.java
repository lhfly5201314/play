package com.example.qq66.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtils {
	/**
	 * 打开Activity
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Activity activityA,Class clazz){
		startActivity(activityA,clazz,false);
	};
	/**
	 * 
	 * @param activity
	 * @param isFinish
	 */
	public static void startActivity(Activity activityA,Class clazz,boolean isFinish){
		Intent intent = new Intent(activityA, clazz);
		activityA.startActivity(intent);
		if (isFinish) {
			activityA.finish();
		}
	};
}
