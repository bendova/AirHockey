package com.airhockey.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader
{
	public static String readTextFileFromResource(Context context, int resourceID)
	{
		StringBuilder content = new StringBuilder();
		try
		{
			InputStream inputStream = context.getResources().openRawResource(resourceID);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			String nextLine;
			while((nextLine = bufferedReader.readLine()) != null)
			{
				content.append(nextLine);
				content.append('\n');
			}
		}
		catch(IOException exception)
		{
			throw new RuntimeException("Could not open resource: " + resourceID, exception);
		}
		catch(Resources.NotFoundException exception)
		{
			throw new RuntimeException("Resource not found: " + resourceID, exception);			
		}
		
		return content.toString();
	}
}
