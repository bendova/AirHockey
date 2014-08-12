package com.airhockey.android.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.airhockey.android.R;

public class ColorShaderProgram extends ShaderProgram
{
	// Uniform locations
	private final int mUniformMatrixLocation;
	private final int mUniformColorLocation;
	
	// Attribute locations
	private final int mAttributePositionLocation;
	
	public ColorShaderProgram(Context context)
	{
		super(context, R.raw.simple_vertex_shader,
				R.raw.simple_fragment_shader);
		
		mUniformMatrixLocation = GLES20.glGetUniformLocation(mProgramID, U_MATRIX);
		mUniformColorLocation = GLES20.glGetUniformLocation(mProgramID, U_COLOR);
		
		mAttributePositionLocation = GLES20.glGetAttribLocation(mProgramID, A_POSITION);
	}
	
	public void setUniformMatrix(float[] matrix)
	{
		GLES20.glUniformMatrix4fv(mUniformMatrixLocation, 1, false, matrix, 0);
	}
	
	public void setUniformColor(float red, float green, float blue)
	{
		GLES20.glUniform4f(mUniformColorLocation, red, green, blue, 1f);
	}
	
	public int getPositionAttributeLocation()
	{
		return mAttributePositionLocation;
	}
}
