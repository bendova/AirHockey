package com.airhockey.android.programs;

import com.airhockey.android.R;

import android.content.Context;
import android.opengl.GLES20;

public class TextureShaderProgram extends ShaderProgram
{
	// Uniform locations
	private final int mUniformMatrixLocation;
	private final int mUniformTextureUnitLocation;
	
	// Attribute locations
	private final int mAttributePositionLocation;
	private final int mAttributeTextureCoordinatesLocation;
	
	public TextureShaderProgram(Context context)
	{
		super(context, R.raw.texture_vertex_shader,
				R.raw.texture_fragment_shader);
		
		mUniformMatrixLocation = GLES20.glGetUniformLocation(mProgramID, U_MATRIX);
		mUniformTextureUnitLocation = GLES20.glGetUniformLocation(mProgramID, U_TEXTURE_UNIT);
		
		mAttributePositionLocation = GLES20.glGetAttribLocation(mProgramID, A_POSITION);
		mAttributeTextureCoordinatesLocation = GLES20.
				glGetAttribLocation(mProgramID, A_TEXTURE_COORDINATES);
	}
	
	public void setUniforms(float[] matrix, int textureID)
	{
		// TODO I still don't get what this code does
		
		GLES20.glUniformMatrix4fv(mUniformMatrixLocation, 1, false, matrix, 0);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		
		GLES20.glUniform1i(mUniformTextureUnitLocation, 0);
	}
	
	public int getPositionAttributeLocation()
	{
		return mAttributePositionLocation;
	}
	
	public int getTextureCoordinatesAttributeLocation()
	{
		return mAttributeTextureCoordinatesLocation;
	}
}
