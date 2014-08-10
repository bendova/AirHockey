package com.airhockey.android.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.airhockey.android.R;

public class ColorShaderProgram extends ShaderProgram
{
	// Uniform locations
	private final int mUniformMatrixLocation;
	
	// Attribute locations
	private final int mAttributePositionLocation;
	private final int mAttributeColorLocation;
	
	public ColorShaderProgram(Context context)
	{
		super(context, R.raw.simple_vertex_shader,
				R.raw.simple_fragment_shader);
		
		mUniformMatrixLocation = GLES20.glGetUniformLocation(mProgramID, U_MATRIX);
		
		mAttributePositionLocation = GLES20.glGetAttribLocation(mProgramID, A_POSITION);
		mAttributeColorLocation = GLES20.glGetAttribLocation(mProgramID, A_COLOR);
	}
	
	public void setUniforms(float[] matrix)
	{
		GLES20.glUniformMatrix4fv(mUniformMatrixLocation, 1, false, matrix, 0);
	}
	
	public int getPositionAttributeLocation()
	{
		return mAttributePositionLocation;
	}
	
	public int getColorAttributeLocation()
	{
		return mAttributeColorLocation;
	}
	
}
