package com.airhockey.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.objects.Mallet;
import com.airhockey.android.objects.Puck;
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
	
	private final float[] mViewMatrix = new float[16];
	private final float[] mViewProjectionMatrix = new float[16];
	private final float[] mModelViewProjectionMatrix = new float[16];
	
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	
	private Puck mPuck;
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
		mMallet = new Mallet(0.08f, 0.15f, 32);
		mPuck = new Puck(0.06f, 0.02f, 32);
		
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
		
		Matrix.setLookAtM(mViewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		
		// draw the table
		positionTableInScene();
		mTextureProgram.useProgram();
		mTextureProgram.setUniforms(mModelViewProjectionMatrix, mTextureID);
		mTable.bindData(mTextureProgram);
		mTable.draw();
		
		// draw mallet 1
		positionObjectInScene(0f, mMallet.getHeight() / 2f, -0.4f);
		mColorProgram.useProgram();
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(1f, 0f, 0f);
		mMallet.bindData(mColorProgram);
		mMallet.draw();
		
		// draw mallet 2
		positionObjectInScene(0f, mMallet.getHeight() / 2f, 0.4f);
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(0f, 1f, 0f);
		mMallet.draw();
		
		// draw the puck
		positionObjectInScene(0f, mPuck.getHeight() / 2f, 0f);
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(0.8f, 0.8f, 1f);
		mPuck.bindData(mColorProgram);
		mPuck.draw();
	}
	
	private void positionTableInScene()
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, -90f, 1f, 0f, 0f);
		Matrix.multiplyMM(mModelViewProjectionMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0);
	}
	
	private void positionObjectInScene(float x, float y, float z)
	{
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, x, y, z);
		Matrix.multiplyMM(mModelViewProjectionMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0);
	}
}
