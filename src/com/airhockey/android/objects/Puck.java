package com.airhockey.android.objects;

import java.util.List;

import com.airhockey.android.data.VertexArray;
import com.airhockey.android.objects.ObjectBuilder.DrawCommand;
import com.airhockey.android.objects.ObjectBuilder.GeneratedData;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.util.Geometry.Cylinder;
import com.airhockey.android.util.Geometry.Point;

public class Puck
{
	private static final int POSITION_COMPONENT_COUNT = 3;
	
	public final float mRadius;
	public final float mHeight;
	
	private final VertexArray mVertexArray;
	private final List<DrawCommand> mDrawCommands;
	
	public Puck(float radius, float height, int numPointsAroundPuck)
	{
		mRadius = radius;
		mHeight = height;
		
		GeneratedData generatedData = ObjectBuilder.createPuck(
				new Cylinder(new Point(0f, 0f, 0f), mRadius, mHeight), numPointsAroundPuck);
		
		mDrawCommands = generatedData.getDrawCommands();
		mVertexArray = new VertexArray(generatedData.getVertexArray());
	}
	
	public void bindData(ColorShaderProgram colorProgram)
	{
		mVertexArray.setVertexAttribPointer(0, colorProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 0);
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
