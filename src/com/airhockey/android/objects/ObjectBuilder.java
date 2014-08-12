package com.airhockey.android.objects;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.util.FloatMath;

import com.airhockey.android.util.Geometry.Point;
import com.airhockey.android.util.Geometry.Circle;
import com.airhockey.android.util.Geometry.Cylinder;

public class ObjectBuilder
{
	static interface DrawCommand
	{
		void draw();
	}
	
	static class GeneratedData
	{
		private final float[] mVertexArray;
		private final List<DrawCommand> mDrawCommands;
		
		GeneratedData(float[] vertexArray, List<DrawCommand> drawCommands)
		{
			mVertexArray = vertexArray;
			mDrawCommands = drawCommands;
		}
		public float[] getVertexArray()
		{
			return mVertexArray;
		}
		public List<DrawCommand> getDrawCommands()
		{
			return mDrawCommands;
		}
	}
	
	private static final int FLOATS_PER_VERTEX = 3;
	private final float[] mVertexData;
	private final List<DrawCommand> mDrawList;
	private int mOffset = 0;
	
	private ObjectBuilder(int sizeInVertices)
	{
		mVertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
		mDrawList = new ArrayList<DrawCommand>();
	}
	
	private static int sizeOfCircleInVertices(int numPoints)
	{
		return 1 + (numPoints + 1);
	}
	
	private static int sizeOfOpenCylinderInVertices(int numPoints)
	{
		return (numPoints + 1) * 2;
	}
	
	public static GeneratedData createPuck(Cylinder puck, int numPoints)
	{
		int verticesCount = sizeOfCircleInVertices(numPoints) +
				sizeOfOpenCylinderInVertices(numPoints);
		
		ObjectBuilder builder = new ObjectBuilder(verticesCount);
		
		Circle puckTop = new Circle(puck.mCenter.translateY(puck.mHeight / 2f),
				puck.mRadius);
		
		builder.appendCircle(puckTop, numPoints);
		builder.appendOpenCylinder(puck, numPoints);
		
		return builder.build();
	}
	
	private void appendCircle(Circle circle, int numPoints)
	{
		final int startVertex = mOffset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfCircleInVertices(numPoints);
		
		// center point of fan
		mVertexData[mOffset++] = circle.mCenter.mX;
		mVertexData[mOffset++] = circle.mCenter.mY;
		mVertexData[mOffset++] = circle.mCenter.mZ;
		
		for(int i = 0; i < numPoints; ++i)
		{
			float angleInRadians = ((float) i / (float) numPoints)
					* ((float) Math.PI * 2f);
			
			mVertexData[mOffset++] = circle.mCenter.mX + 
					circle.mRadius * FloatMath.cos(angleInRadians);
			mVertexData[mOffset++] = circle.mCenter.mY;
			mVertexData[mOffset++] = circle.mCenter.mZ + 
					circle.mRadius * FloatMath.sin(angleInRadians);
		}
		
		// end point of fan
		mVertexData[mOffset++] = circle.mCenter.mX;
		mVertexData[mOffset++] = circle.mCenter.mY;
		mVertexData[mOffset++] = circle.mCenter.mZ;
		
		mDrawList.add(new DrawCommand()
		{
			@Override
			public void draw()
			{
				GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
			}
		});
	}
	
	private void appendOpenCylinder(Cylinder cylinder, int numPoints)
	{
		final int startVertex = mOffset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
		
		final float yStart = cylinder.mCenter.mY - (cylinder.mHeight / 2);
		final float yEnd = cylinder.mCenter.mY + (cylinder.mHeight / 2);
		
		for(int i = 0; i <= numPoints; ++i)
		{
			float angleInRadians = ((float)i / (float)numPoints) * ((float)Math.PI * 2f);
			
			float xPosition = cylinder.mCenter.mX + 
					cylinder.mRadius * FloatMath.cos(angleInRadians);
			
			float zPosition = cylinder.mCenter.mZ +
					cylinder.mRadius * FloatMath.sin(angleInRadians);
			
			mVertexData[mOffset++] = xPosition;
			mVertexData[mOffset++] = yStart;
			mVertexData[mOffset++] = zPosition;
			
			mVertexData[mOffset++] = xPosition;
			mVertexData[mOffset++] = yEnd;
			mVertexData[mOffset++] = zPosition;
		}
		
		mDrawList.add(new DrawCommand()
		{
			@Override
			public void draw()
			{
				GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
			}
		});
	}
	
	private GeneratedData build()
	{
		return new GeneratedData(mVertexData, mDrawList);
	}
	
	static GeneratedData createMallet(Point center, float radius, float height, int numPoints)
	{
		int size = sizeOfCircleInVertices(numPoints) * 2
				+ sizeOfOpenCylinderInVertices(numPoints) * 2;
		
		ObjectBuilder builder = new ObjectBuilder(size);
		
		final float BASE_HEIGHT = 0.25f;
		float baseHeight = height * BASE_HEIGHT;
		
		Circle baseCircle = new Circle(center.translateY(-baseHeight), radius);
		Cylinder baseCylinder = new Cylinder(baseCircle.mCenter.translateY(-baseHeight / 2f), 
				radius, baseHeight);
		
		builder.appendCircle(baseCircle, numPoints);
		builder.appendOpenCylinder(baseCylinder, numPoints);
		
		final float TOP_HEIGHT = 1f - BASE_HEIGHT;
		float handleHeight = height * TOP_HEIGHT;
		float handleRadius = radius / 3f;
		
		Circle handleCircle = new Circle(center.translateY(height / 2f), handleRadius);
		Cylinder handleCylinder = new Cylinder(handleCircle.mCenter.translateY(-handleHeight / 2f),
				handleRadius, handleHeight);
		
		builder.appendCircle(handleCircle, numPoints);
		builder.appendOpenCylinder(handleCylinder, numPoints);
		
		return builder.build();
	}
}
