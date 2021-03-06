package com.airhockey.android.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.airhockey.android.Constants;

import android.opengl.GLES20;

public class VertexArray
{	
	private final FloatBuffer mFloatBuffer;
	
	public VertexArray(float[] vertexData)
	{
		mFloatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(vertexData);
	}
	
	public void setVertexAttribPointer(int dataOffset, int attributeLocation, 
			int componentCount, int stride)
	{
		mFloatBuffer.position(dataOffset);
		GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT, 
				false, stride, mFloatBuffer);
		GLES20.glEnableVertexAttribArray(attributeLocation);
		
		mFloatBuffer.position(0);
	}
}
