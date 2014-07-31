package com.airhockey.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.util.Logger;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class MainRenderer implements Renderer
{	
	private static final int BYTES_PER_FLOAT = 4;	
	
	private float[] mTableVertices = 
		{
			// order of coordinates: X, Y, R, G, B
			
			// triangle fan
			 0.0f,  0.0f, 1.0f, 1.0f, 1.0f,
			-0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
			 0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
			 0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
			-0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
			-0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
			
			// line 1
			-0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
			 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
			
			// mallets
			 0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
			 0.0f,  0.4f, 1.0f, 0.0f, 0.0f,
			
			// puck
			 0.0f,  0.0f, 1.0f, 1.0f, 0.0f,
			
			// border
			-0.5f, -0.8f, 0.0f, 1.0f, 0.0f,
			 0.5f, -0.8f, 0.0f, 1.0f, 0.0f,
			 0.5f,  0.8f, 0.0f, 1.0f, 0.0f,
			-0.5f,  0.8f, 0.0f, 1.0f, 0.0f
		};
	
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final String ATTRIBUTE_POSITION = "a_Position";
	private int mAttributePositionLocation;
	
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final String ATTRIBUTE_COLOR = "a_Color";
	private int mAttributeColorLocation;
	
	private static final String UNIFORM_MATRIX = "u_Matrix";
	private final float[] mProjectionMatrix = new float[16];
	private int mUniformMatrixLocation;
	
	private final float[] mModelMatrix = new float[16];
	
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
			mVertextData.position(0);
			mAttributePositionLocation = GLES20.glGetAttribLocation(mProgramID, ATTRIBUTE_POSITION);
			GLES20.glVertexAttribPointer(mAttributePositionLocation, POSITION_COMPONENT_COUNT, 
					GLES20.GL_FLOAT, false, STRIDE, mVertextData);
			GLES20.glEnableVertexAttribArray(mAttributePositionLocation);
			
			mVertextData.position(POSITION_COMPONENT_COUNT);
			mAttributeColorLocation = GLES20.glGetAttribLocation(mProgramID, ATTRIBUTE_COLOR);
			GLES20.glVertexAttribPointer(mAttributeColorLocation, COLOR_COMPONENT_COUNT,
					GLES20.GL_FLOAT, false, STRIDE, mVertextData);
			GLES20.glEnableVertexAttribArray(mAttributeColorLocation);
			
			mUniformMatrixLocation = GLES20.glGetUniformLocation(mProgramID, UNIFORM_MATRIX);
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
		
		float aspectRatio = (float) width / (float) height;
		MatrixHelper.perspectiveM(mProjectionMatrix, 45, aspectRatio , 1.0f, 10.0f);
		
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.0f);
		Matrix.rotateM(mModelMatrix, 0, -60.0f, 1.0f, 0.0f, 0.0f);
		
		final float[] temp = new float[16];
		Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0);
		System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.length);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		GLES20.glUniformMatrix4fv(mUniformMatrixLocation, 1, false, mProjectionMatrix, 0);
		
		// draw the table
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
		
		// draw the divider line
		GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
		
		// draw the first mallet
		GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
		
		// draw the second mallet
		GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
		
		// draw the second puck
		GLES20.glDrawArrays(GLES20.GL_POINTS, 10, 1);
		
		// draw the second border
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 11, 4);
	}
}
