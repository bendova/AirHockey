package com.airhockey.android;

import android.support.v7.app.ActionBarActivity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity 
{
	private static final int OPENGL_ES_VERSION = 2;
	
	private GLSurfaceView mGLSurfaceView;
	private boolean mIsRenderSet = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        initGLSurfaceView();
        setContentView(mGLSurfaceView);
    }
    
    private void initGLSurfaceView()
    {
		mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(OPENGL_ES_VERSION);
        mGLSurfaceView.setRenderer(new MainRenderer(this));
        mIsRenderSet = true;
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
