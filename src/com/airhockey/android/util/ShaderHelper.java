package com.airhockey.android.util;

import android.opengl.GLES20;

public class ShaderHelper
{
	public static final int INVALID_ID = 0;

	private static final String TAG = "ShaderHelper";
	
	public static int compileVertexShader(String shaderCode)
	{
		if(shaderCode != null)
		{
			return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
		}
		return INVALID_ID;
	}
	
	public static int compileFragmentShader(String shaderCode)
	{
		if(shaderCode != null)
		{
			return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
		}
		return INVALID_ID;
	}
	
	private static int compileShader(int shaderType, String shaderCode)
	{
		final int shaderObjectID = GLES20.glCreateShader(shaderType);
		if(shaderObjectID != INVALID_ID)
		{
			GLES20.glShaderSource(shaderObjectID, shaderCode);
			GLES20.glCompileShader(shaderObjectID);
			
			String shaderInfoLog =  GLES20.glGetShaderInfoLog(shaderObjectID);
			if(shaderInfoLog.isEmpty() == false)
			{
				Logger.info(TAG, "Results of compiling source:\n" 
						+ shaderCode + "\n" 
						+ shaderInfoLog);
			}
			return getShaderCompilationResult(shaderObjectID);
		}
		else 
		{
			Logger.warn(TAG, "Could not create shader object.");
			
			return INVALID_ID;
		}
	}
	
	private static int getShaderCompilationResult(int shaderObjectID)
	{
		final int[] compileStatus = new int[1];
		final int RESULT_INDEX = 0;
		GLES20.glGetShaderiv(shaderObjectID, GLES20.GL_COMPILE_STATUS, compileStatus, RESULT_INDEX);
		
		if(compileStatus[RESULT_INDEX] == INVALID_ID)
		{
			GLES20.glDeleteShader(shaderObjectID);
			return INVALID_ID;
		}
		return shaderObjectID;
	}
	
	public static int linkProgram(int vertexShaderID, int fragmentShaderID)
	{
		final int programObjectID = GLES20.glCreateProgram();
		if(programObjectID != INVALID_ID)
		{
			GLES20.glAttachShader(programObjectID, vertexShaderID);
			GLES20.glAttachShader(programObjectID, fragmentShaderID);
			GLES20.glLinkProgram(programObjectID);
			
			String programInfoLog = GLES20.glGetProgramInfoLog(programObjectID);
			if(programInfoLog.isEmpty() == false)
			{
				Logger.info(TAG, "Result of linking program:\n" + programInfoLog);
			}
			return getProgramLinkResult(programObjectID);
		}
		else
		{
			Logger.warn(TAG, "Could not create a new program.");
			return INVALID_ID;
		}
	}
	
	private static int getProgramLinkResult(int programObjectID)
	{
		final int[] linkStatus = new int[1];
		final int RESULT_INDEX = 0;
		GLES20.glGetProgramiv(programObjectID, GLES20.GL_LINK_STATUS, linkStatus, RESULT_INDEX);
		
		if(linkStatus[RESULT_INDEX] == INVALID_ID)
		{
			GLES20.glDeleteProgram(programObjectID);
			
			Logger.warn(TAG, "Linking the program failed.");
			
			return INVALID_ID;
		}
		return programObjectID;
	}
	
	public static boolean validateProgram(int programObjectID)
	{
		GLES20.glValidateProgram(programObjectID);
		
		final int[] validationStatus = new int[1];
		final int RESULT_INDEX = 0;
		GLES20.glGetProgramiv(programObjectID, GLES20.GL_VALIDATE_STATUS, 
				validationStatus, RESULT_INDEX);
		
		String programInfoLog = GLES20.glGetProgramInfoLog(programObjectID);
		if(programInfoLog.isEmpty() == false)
		{
			Logger.info(TAG, "Result of validating program: " + validationStatus[RESULT_INDEX] 
					+ "\nLog: " + GLES20.glGetProgramInfoLog(programObjectID));
		}
		return (validationStatus[RESULT_INDEX] != INVALID_ID);
	}
}
