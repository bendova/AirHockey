package com.airhockey.android.objects;

import java.util.List;

import android.opengl.GLES20;

import com.airhockey.android.Constants;
import com.airhockey.android.data.VertexArray;
import com.airhockey.android.objects.ObjectBuilder.DrawCommand;
import com.airhockey.android.objects.ObjectBuilder.GeneratedData;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.util.Geometry.Point;

public class Mallet
{
	private static final int POSITION_COMPONENT_COUNT = 3;
	
	private final float mRadius;
	private final float mHeight;
	
	private final VertexArray mVertexArray;
	private final List<DrawCommand> mDrawCommands;
	
	public Mallet(float radius, float height, int numPointsAroundMallet)
	{
		mRadius = radius;
		mHeight = height;
		
		Point center = new Point(0f, 0f, 0f);
		GeneratedData generatedData = ObjectBuilder.createMallet(center, 
				mRadius, mHeight, numPointsAroundMallet);
		
		mVertexArray = new VertexArray(generatedData.getVertexArray());
		mDrawCommands = generatedData.getDrawCommands();
	}
	
	public void bindData(ColorShaderProgram colorProgram)
	{
		mVertexArray.setVertexAttribPointer(
				0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT,
				0);
	}
	
	public void draw()
	{
		for(DrawCommand command: mDrawCommands)
		{
			command.draw();
		}
	}
	
	public float getHeight()
	{
		return mHeight;
	}
	
	public float getRadius()
	{
		return mRadius;
	}
}
