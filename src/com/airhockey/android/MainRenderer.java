package com.airhockey.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.util.Logger;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public class MainRenderer implements Renderer
{	
	private static final int BYTES_PER_FLOAT = 4;	
	
	private float[] mTableVertices = 
		{
			// triangle 1
			-0.5f, -0.5f, 
			 0.5f,  0.5f, 
			-0.5f,  0.5f,
			
			// triangle 2
			-0.5f, -0.5f, 
			 0.5f, -0.5f,
			 0.5f,  0.5f,
			
			// line 1
			-0.5f, 0f,
			 0.5f, 0f,
			
			// mallets
			0f, -0.25f,
			0f,  0.25f,
			
			// puck
			0f, 0f,
			
			// border
			-0.5f, -0.5f,
			 0.5f, -0.5f,
			 0.5f,  0.5f,
			-0.5f,  0.5f
			 
		};
	
	private static final String UNIFORM_COLOR = "u_Color";
	private int mUniformColorLocation;
	
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final String ATTRIBUTE_POSITION = "a_Position";
	private int mAttributePositionLocation;
	
	private final FloatBuffer mVertextData;
	private final Context mContext;
	
	private int mProgramID;
	
	public MainRenderer(Context context)
	{
		mContext = context;
		
		mVertextData = ByteBuffer.allocateDirect(mTableVertices.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		
		mVertextData.put(mTableVertices);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		if(createGLProgram())
		{
			mUniformColorLocation = GLES20.glGetUniformLocation(mProgramID, UNIFORM_COLOR);
			mAttributePositionLocation = GLES20.glGetAttribLocation(mProgramID, ATTRIBUTE_POSITION);
			
			mVertextData.position(0);
			GLES20.glVertexAttribPointer(mAttributePositionLocation, POSITION_COMPONENT_COUNT, 
					GLES20.GL_FLOAT, false, 0, mVertextData);
			GLES20.glEnableVertexAttribArray(mAttributePositionLocation);
		}
	}
	
	private boolean createGLProgram()
	{
		String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, 
				R.raw.simple_vertex_shader);
		int vertexShaderID = ShaderHelper.compileVertexShader(vertexShaderSource);
		
		String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, 
				R.raw.simple_fragment_shader);
		int fragmentShaderID = ShaderHelper.compileFragmentShader(fragmentShaderSource);
		
		mProgramID = ShaderHelper.linkProgram(vertexShaderID, fragmentShaderID);
		
		if(mProgramID != ShaderHelper.INVALID_ID)
		{
			if(Logger.ON)
			{
				ShaderHelper.validateProgram(mProgramID);
			}
			
			GLES20.glUseProgram(mProgramID);
			return true;
		}
		return false;
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		GLES20.glViewport(0, 0, width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		// draw the table
		GLES20.glUniform4f(mUniformColorLocation, 1.0f, 1.0f, 1.0f, 1.0f); //white
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		
		// draw the divider line
		GLES20.glUniform4f(mUniformColorLocation, 1.0f, 0.0f, 0.0f, 1.0f); //red
		GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
		
		// draw the first mallet
		GLES20.glUniform4f(mUniformColorLocation, 0.0f, 0.0f, 1.0f, 1.0f); //blue
		GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
		
		// draw the second mallet
		GLES20.glUniform4f(mUniformColorLocation, 1.0f, 0.0f, 0.0f, 1.0f); //red
		GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
		
		// draw the second puck
		GLES20.glUniform4f(mUniformColorLocation, 1.0f, 1.0f, 0.0f, 1.0f); //yellow
		GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1);
		
		// draw the second border
		GLES20.glUniform4f(mUniformColorLocation, 0.0f, 1.0f, 0.0f, 1.0f); //green
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 11, 4);
	}
}
