package com.airhockey.android.util;

import android.util.Log;

public class Logger
{
	public static final boolean ON = true;
	
	public static void debug(String tag, String message)
	{
		if(ON)
		{
			Log.d(tag, message);
		}
	}
	
	public static void info(String tag, String message)
	{
		if(ON)
		{
			Log.i(tag, message);
		}
	}
	
	public static void warn(String tag, String message)
	{
		if(ON)
		{
			Log.w(tag, message);
		}
	}
	
	public static void error(String tag, String message)
	{
		if(ON)
		{
			Log.e(tag, message);
		}
	}
}
