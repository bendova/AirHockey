package com.airhockey.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.airhockey.android.objects.Mallet;
import com.airhockey.android.objects.Puck;
import com.airhockey.android.objects.Table;
import com.airhockey.android.programs.ColorShaderProgram;
import com.airhockey.android.programs.TextureShaderProgram;
import com.airhockey.android.util.Geometry;
import com.airhockey.android.util.Geometry.Point;
import com.airhockey.android.util.Geometry.Vector;
import com.airhockey.android.util.Geometry.Ray;
import com.airhockey.android.util.Geometry.Sphere;
import com.airhockey.android.util.Geometry.Plane;
import com.airhockey.android.util.Logger;
import com.airhockey.android.util.MatrixHelper;
import com.airhockey.android.util.TextureHelper;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class MainRenderer implements Renderer
{	
	private static final String TAG = "MainRenderer";
	
	private final Context mContext;
	
	private final float[] mViewMatrix = new float[16];
	private final float[] mViewProjectionMatrix = new float[16];
	private final float[] mModelViewProjectionMatrix = new float[16];
	
	private final float[] mInvertedViewProjectionMatrix = new float[16];
	
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	
	private Puck mPuck;
	private Table mTable;
	private Mallet mMallet;
	
	private TextureShaderProgram mTextureProgram;
	private ColorShaderProgram mColorProgram;
	
	private int mTableTextureID;
	
	private boolean mIsRedMalletPressed = false;
	private boolean mIsGreenMalletPressed = false;
	private Point mGreenMalletPosition;
	private Point mRedMalletPosition;
	
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
		
		mTableTextureID = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
		
		mRedMalletPosition = new Point(0f, mMallet.getHeight() / 2f, -0.4f);
		mGreenMalletPosition = new Point(0f, mMallet.getHeight() / 2f, 0.4f);
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
		Matrix.invertM(mInvertedViewProjectionMatrix, 0, mViewProjectionMatrix, 0);
		
		// draw the table
		positionTableInScene();
		mTextureProgram.useProgram();
		mTextureProgram.setUniforms(mModelViewProjectionMatrix, mTableTextureID);
		mTable.bindData(mTextureProgram);
		mTable.draw();
		
		// draw mallet 1
		positionObjectInScene(mRedMalletPosition.getX(), mRedMalletPosition.getY(), mRedMalletPosition.getZ());
		mColorProgram.useProgram();
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(1f, 0f, 0f);
		mMallet.bindData(mColorProgram);
		mMallet.draw();
		
		// draw mallet 2
		positionObjectInScene(mGreenMalletPosition.getX(), mGreenMalletPosition.getY(), mGreenMalletPosition.getZ());
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
	
	public void handleTouchDown(float normalizedX, float normalizedY)
	{
		Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
		
		mIsGreenMalletPressed = intersectsRedMallet(mGreenMalletPosition, ray);
		if(mIsGreenMalletPressed == false)
		{
			mIsRedMalletPressed = intersectsRedMallet(mRedMalletPosition, ray);
		}
	}
	
	private boolean intersectsRedMallet(Point malletPosition, Ray ray)
	{
		Sphere malletBoundindSphere = new Sphere(
				new Point(malletPosition.getX(), 
						malletPosition.getY(),
						malletPosition.getZ()), 
						mMallet.getHeight() / 2f);
		
		return Geometry.intersects(malletBoundindSphere, ray);
	}
	
	public void handleTouchMove(float normalizedX, float normalizedY)
	{
		if(mIsGreenMalletPressed)
		{
			mGreenMalletPosition = getNewPointOnTable(normalizedX, normalizedY, 
					mGreenMalletPosition);
		}
		else if(mIsRedMalletPressed)
		{
			mRedMalletPosition = getNewPointOnTable(normalizedX, normalizedY, 
					mRedMalletPosition);
		}
	}
	
	private Point getNewPointOnTable(float normalizedX, float normalizedY, Point originalPoint)
	{
		Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
		
		Plane hockeyTablePlane = new Plane(new Point(0f, 0f, 0f), new Vector(0f, 1f, 0f));
		Point touchedPointOnTable = Geometry.intersectionPoint(ray, hockeyTablePlane);
		
		return new Point(touchedPointOnTable.getX(), originalPoint.getY(), 
				touchedPointOnTable.getZ());
	}
	
	public void handleTouchUp(float normalizedX, float normalizedY)
	{
//		Logger.info(TAG, "handleTouchUp() x = " + normalizedX + ", y = " + normalizedY);
		
		if(mIsGreenMalletPressed)
		{
			mIsGreenMalletPressed = false;
		}
	}
	
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY)
	{
		final float[] nearPointNDC = {normalizedX, normalizedY, -1f, 1f};
		final float[] farPointNDC = {normalizedX, normalizedY, 1f, 1f};
		
		final float[] nearPointWorld = new float[4];
		final float[] farPointWorld = new float[4];
		
		Matrix.multiplyMV(nearPointWorld, 0, mInvertedViewProjectionMatrix, 0, nearPointNDC, 0);
		Matrix.multiplyMV(farPointWorld, 0, mInvertedViewProjectionMatrix, 0, farPointNDC, 0);
		
		divideByW(nearPointWorld);
		divideByW(farPointWorld);
		
		Point nearPointOfRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
		Point farPointOfRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
		
		return new Ray(nearPointOfRay, Geometry.vectorBetween(nearPointOfRay, farPointOfRay));
	}
	
	private void divideByW(float[] vector)
	{
		final float w = vector[3];
		vector[0] /= w;
		vector[1] /= w;
		vector[2] /= w;
	}
}
