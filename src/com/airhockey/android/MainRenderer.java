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
	private class MalletData
	{
		private Point mCurrentPosition;
		private Point mPreviousPosition;
		private boolean mIsPressed;
		
		public MalletData(Point currentPosition)
		{
			mPreviousPosition = currentPosition;
			mCurrentPosition = currentPosition;
			mIsPressed = false;
		}
		public Point getPosition()
		{
			return mCurrentPosition;
		}
		public Point getPreviousPosition()
		{
			return mPreviousPosition;
		}
		public void setPosition(Point newPosition)
		{
			mPreviousPosition = mCurrentPosition;
			mCurrentPosition = newPosition;
		}
		public boolean isPressed()
		{
			return mIsPressed;
		}
		public void setIsPressed(boolean isNowPressed)
		{
			mIsPressed = isNowPressed;
			if(mIsPressed == false)
			{
				mPreviousPosition = mCurrentPosition;
			}
		}
	}
	
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
	
//	private boolean mIsRedMalletPressed = false;
//	private boolean mIsGreenMalletPressed = false;
//	private Point mGreenMalletPosition;
//	private Point mRedMalletPosition;
	
//	private Point mPreviousMalletPosition;
	
	private MalletData mRedMalletData;
	private MalletData mGreenMalletData;
	
	private Point mPuckPosition;
	private Vector mPuckVector;
	
	private final float mLeftBound = -0.5f;
	private final float mRightBound = 0.5f;
	private final float mFarBound = -0.8f;
	private final float mNearBound = 0.8f;
	
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
		
		mRedMalletData = new MalletData(new Point(0f, mMallet.getHeight() / 2f, -0.4f));
		mGreenMalletData = new MalletData(new Point(0f, mMallet.getHeight() / 2f, 0.4f));
		
		mPuckPosition = new Point(0f, mPuck.getHeight() / 2f, 0f);
		mPuckVector = new Vector(0f, 0f, 0f);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		GLES20.glViewport(0, 0, width, height);
		
		final float cameraAngle = 45f;
		final float aspectRatio = (float) width / (float) height;
		MatrixHelper.perspectiveM(mProjectionMatrix, cameraAngle, aspectRatio , 1.0f, 10.0f);
		
		Matrix.setLookAtM(mViewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
		
		Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		Matrix.invertM(mInvertedViewProjectionMatrix, 0, mViewProjectionMatrix, 0);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		// draw the table
		positionTableInScene();
		mTextureProgram.useProgram();
		mTextureProgram.setUniforms(mModelViewProjectionMatrix, mTableTextureID);
		mTable.bindData(mTextureProgram);
		mTable.draw();
		
		// draw mallet 1
		Point redMalletPosition = mRedMalletData.getPosition();
		positionObjectInScene(redMalletPosition.getX(), redMalletPosition.getY(), redMalletPosition.getZ());
		mColorProgram.useProgram();
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(1f, 0f, 0f);
		mMallet.bindData(mColorProgram);
		mMallet.draw();
		
		// draw mallet 2
		Point greenMalletPosition = mGreenMalletData.getPosition();
		positionObjectInScene(greenMalletPosition.getX(), greenMalletPosition.getY(), greenMalletPosition.getZ());
		mColorProgram.setUniformMatrix(mModelViewProjectionMatrix);
		mColorProgram.setUniformColor(0f, 1f, 0f);
		mMallet.draw();
				
		// draw the puck
		updatePuckPosition();
		positionObjectInScene(mPuckPosition.getX(), mPuckPosition.getY(), mPuckPosition.getZ());
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
		
		mGreenMalletData.setIsPressed(intersectsMallet(mGreenMalletData.getPosition(), ray));
		if(mGreenMalletData.isPressed() == false)
		{
			mRedMalletData.setIsPressed(intersectsMallet(mRedMalletData.getPosition(), ray));
		}
	}
	
	private boolean intersectsMallet(Point malletPosition, Ray ray)
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
		if(mGreenMalletData.isPressed())
		{
			updateMalletPosition(mGreenMalletData, normalizedX, normalizedY, 0f, mNearBound);
		}
		else if(mRedMalletData.isPressed())
		{
			updateMalletPosition(mRedMalletData, normalizedX, normalizedY, mFarBound, 0f);
		}
	}
	
	private void updateMalletPosition(MalletData malletData, float normalizedX, float normalizedY, 
			float farBound, float nearBound)
	{
		Point pointOnTable = getNewPointOnTable(normalizedX, normalizedY, 
				malletData.getPosition());
		pointOnTable = clampMalletPointOnTableSide(pointOnTable, mLeftBound, mRightBound, 
				farBound, nearBound);
		malletData.setPosition(pointOnTable);
	}
	
	private Point getNewPointOnTable(float normalizedX, float normalizedY, Point originalPoint)
	{
		Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
		
		Plane hockeyTablePlane = new Plane(new Point(0f, 0f, 0f), new Vector(0f, 1f, 0f));
		Point touchedPointOnTable = Geometry.intersectionPoint(ray, hockeyTablePlane);
		touchedPointOnTable.setY(originalPoint.getY());
		return touchedPointOnTable;
	}
	
	private Point clampMalletPointOnTableSide(Point pointOnTable, float leftBound, float right,
			float farBound, float nearBound)
	{
		float malletRadius = mMallet.getRadius();
		pointOnTable.setX(Geometry.clamp(pointOnTable.getX(), 
				leftBound + malletRadius,
				right - malletRadius));
		
		pointOnTable.setZ(Geometry.clamp(pointOnTable.getZ(), 
				farBound + malletRadius, 
				nearBound - malletRadius));
		
		return pointOnTable;
	}
	
	public void handleTouchUp(float normalizedX, float normalizedY)
	{
		if(mGreenMalletData.isPressed())
		{
			mGreenMalletData.setIsPressed(false);
		}
		else if(mRedMalletData.isPressed())
		{
			mRedMalletData.setIsPressed(false);
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
	
	private void updatePuckPosition()
	{
		checkIntersectionWithPuck(mGreenMalletData);
		checkIntersectionWithPuck(mRedMalletData);

		mPuckPosition = mPuckPosition.translate(mPuckVector);
		
		float puckRadius = mPuck.getRadius();
		float leftEdge = mLeftBound + puckRadius;
		float rightEdge = mRightBound - puckRadius;
		if((mPuckPosition.getX() < leftEdge) ||
			(mPuckPosition.getX() > rightEdge))
		{
			mPuckVector = new Vector(-mPuckVector.getX(), mPuckVector.getY(), mPuckVector.getZ());
			mPuckVector = mPuckVector.scale(0.9f);
		}
		
		float farEdge = mFarBound + puckRadius;
		float nearEdge = mNearBound - puckRadius;
		if((mPuckPosition.getZ() < farEdge) ||
			(mPuckPosition.getZ() > nearEdge))
		{
			mPuckVector = new Vector(mPuckVector.getX(), mPuckVector.getY(), -mPuckVector.getZ());
			mPuckVector = mPuckVector.scale(0.9f);
		}
		mPuckVector = mPuckVector.scale(0.99f);
		
		float newX = Geometry.clamp(mPuckPosition.getX(), leftEdge, rightEdge);
		float newZ = Geometry.clamp(mPuckPosition.getZ(), farEdge, nearEdge);
		
		mPuckPosition = new Point(newX, mPuckPosition.getY(), newZ);
	}
	
	private void checkIntersectionWithPuck(MalletData malletData)
	{
		Point currentMalletPosition = malletData.getPosition();
		float distance = Geometry.vectorBetween(currentMalletPosition, mPuckPosition).length();
		if(distance < (mPuck.getRadius() + mMallet.getRadius()))
		{
			Vector movementVectorFromMallet = Geometry.vectorBetween(malletData.getPreviousPosition(), 
					currentMalletPosition);
			mPuckVector.setX(composeValues(mPuckVector.getX(), movementVectorFromMallet.getX()));
			mPuckVector.setY(composeValues(mPuckVector.getY(), movementVectorFromMallet.getY()));
			mPuckVector.setZ(composeValues(mPuckVector.getZ(), movementVectorFromMallet.getZ()));
		}
	}
	
	private float composeValues(float dimension1, float dimension2)
	{
		if(dimension2 != 0)
		{
			return (dimension1 + dimension2);
		}
		else
		{
			return -dimension1;
		}
	}
}
