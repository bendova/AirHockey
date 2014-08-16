package com.airhockey.android;

import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity 
{
	private static final int OPENGL_ES_VERSION = 2;
	
	private GLSurfaceView mGLSurfaceView;
	private boolean mIsRenderSet = false;
	private final MainRenderer mMainRenderer;
	
	public MainActivity()
	{
		mMainRenderer = new MainRenderer(this);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        initGLSurfaceView();
        addTouchListener();
        setContentView(mGLSurfaceView);
    }
    
    private void initGLSurfaceView()
    {
		mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(OPENGL_ES_VERSION);
        mGLSurfaceView.setRenderer(mMainRenderer);
        mIsRenderSet = true;
    }
    
    private void addTouchListener()
    {
    	mGLSurfaceView.setOnTouchListener(new OnTouchListener()
    	{
    		@Override
    		public boolean onTouch(View view, MotionEvent event)
    		{
    			if(event != null)
    			{
    				final float normalizedX =  ((event.getX() / (float)view.getWidth()) * 2 - 1);
    				final float normalizedY = -((event.getY() / (float)view.getHeight()) * 2 - 1);
    				
    				switch(event.getAction())
    				{
    				case MotionEvent.ACTION_DOWN:
    					sendTouchDownEvent(normalizedX, normalizedY);
    					break;
    				case MotionEvent.ACTION_MOVE:
    					sendTouchMoveEvent(normalizedX, normalizedY);
    					break;
    				case MotionEvent.ACTION_UP:
    					sendTouchUpEvent(normalizedX, normalizedY);
    					break;
    				}
					return true;
    			}
    			return false;
    		}
    	});
    }
    
    private void sendTouchDownEvent(final float normalizedX, final float normalizedY)
    {
    	mGLSurfaceView.queueEvent(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    			mMainRenderer.handleTouchDown(normalizedX, normalizedY);
    		}
    	});
    }
    
    private void sendTouchMoveEvent(final float normalizedX, final float normalizedY)
    {
    	mGLSurfaceView.queueEvent(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    			mMainRenderer.handleTouchMove(normalizedX, normalizedY);
    		}
    	});
    }
    
    private void sendTouchUpEvent(final float normalizedX, final float normalizedY)
    {
    	mGLSurfaceView.queueEvent(new Runnable()
    	{
    		@Override
    		public void run()
    		{
    			mMainRenderer.handleTouchUp(normalizedX, normalizedY);
    		}
    	});
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(mIsRenderSet)
    	{
    		mGLSurfaceView.onPause();
    	}
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(mIsRenderSet)
    	{
    		mGLSurfaceView.onResume();
    	}
    }
}
