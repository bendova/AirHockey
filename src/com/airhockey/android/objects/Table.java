package com.airhockey.android.objects;

import android.opengl.GLES20;

import com.airhockey.android.Constants;
import com.airhockey.android.data.VertexArray;
import com.airhockey.android.programs.TextureShaderProgram;

public class Table
{
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + 
			TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	
	private static final float[] VERTEX_DATA =
	{
		// Order of coordinates: X, Y, S, T
		
		// Triangle Fan
		  0.0f,  0.0f, 0.5f, 0.5f,
		 -0.5f, -0.8f, 0.0f, 0.9f,
		  0.5f, -0.8f, 1.0f, 0.9f,
		  0.5f,  0.8f, 1.0f, 0.1f,
		 -0.5f,  0.8f, 0.0f, 0.1f,
		 -0.5f, -0.8f, 0.0f, 0.9f
	};
	private static final int VERTEX_COUNT = VERTEX_DATA.length 
			/ (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT);
	
	private final VertexArray mVertexArray;
	
	public Table()
	{
		mVertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void bindData(TextureShaderProgram textureProgram)
	{
		mVertexArray.setVertexAttribPointer(
				0, 
				textureProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 
				STRIDE);
		
		mVertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT, 
				textureProgram.getTextureCoordinatesAttributeLocation(), 
				TEXTURE_COORDINATES_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw()
	{
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);
	}
}
