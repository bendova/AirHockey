package com.airhockey.android.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

public class ShaderProgram
{
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	
	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	
	protected final int mProgramID;
	
	protected ShaderProgram(Context context, int vertexShaderResourceID, 
			int fragmentShaderResourceID)
	{
		String vertexShaderSource = TextResourceReader.
				readTextFileFromResource(context, vertexShaderResourceID);
		String fragmentShaderSource = TextResourceReader.
				readTextFileFromResource(context, fragmentShaderResourceID);
		mProgramID = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
	}
	
	public void useProgram()
	{
		GLES20.glUseProgram(mProgramID);
	}
}
