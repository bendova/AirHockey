package com.airhockey.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import static com.airhockey.android.Constants.INVALID_ID;

public class TextureHelper
{
	private static final String TAG = "TextureHelper";
	
	public static int loadTexture(Context context, int resourceID)
	{
		final int[] textureObjectIDs = new int[1];
		final int textureIndex = 0;
		GLES20.glGenTextures(1, textureObjectIDs, textureIndex);
		if(textureObjectIDs[0] == INVALID_ID)
		{
			Logger.warn(TAG, "loadTexture() Could not generate a new OpenGL texture object.");
			
			return INVALID_ID;
		}
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceID, options);
		
		if(bitmap == null)
		{
			Logger.warn(TAG, "loadTexture() Could not decode resource id: " + resourceID);
			
			GLES20.glDeleteTextures(1, textureObjectIDs, textureIndex);
		}
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIDs[textureIndex]);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 
				GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 
				GLES20.GL_LINEAR);
		
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, INVALID_ID);
		
		return textureObjectIDs[textureIndex];
	}
}
