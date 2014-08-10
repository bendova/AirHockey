package com.airhockey.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.objects.Mallet;
import com.airhockey.android.objects.Table;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.Logger;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;
import com.airhockey.android.util.TextureHelper;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import static com.airhockey.android.Constants.BYTES_PER_FLOAT;
import static com.airhockey.android.Constants.INVALID_ID;

public class MainRenderer implements Renderer
{	
	private final Context mContext;
	
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	
	private Table mTable;
	private Mallet mMallet;
	
	private TextureShaderProgram mTextureProgram;
	private ColorShaderProgram mColorProgram;
	
	private int mTextureID;
	
	public MainRenderer(Context context)
	{
		mContext = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		mTable = new Table();
		mMallet = new Mallet();
		
		mTextureProgram = new TextureShaderProgram(mContext);
		mColorProgram = new ColorShaderProgram(mContext);
		
		mTextureID = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		GLES20.glViewport(0, 0, width, height);
		
		final float cameraAngle = 45f;
		final float aspectRatio = (float) width / (float) height;
		MatrixHelper.perspectiveM(mProjectionMatrix, cameraAngle, aspectRatio , 1.0f, 10.0f);
		
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
		
		// draw the table
		mTextureProgram.useProgram();
		mTextureProgram.setUniforms(mProjectionMatrix, mTextureID);
		mTable.bindData(mTextureProgram);
		mTable.draw();
		
		// draw the mallets
		mColorProgram.useProgram();
		mColorProgram.setUniforms(mProjectionMatrix);
		mMallet.bindData(mColorProgram);
		mMallet.draw();
	}
}
