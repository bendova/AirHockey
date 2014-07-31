package com.airhockey.android.util;

public class MatrixHelper
{
	public static void perspectiveM(float[] matrix, float yFieldOfViewInDegrees, 
			float aspectRatio, float distanceToNearPlane, float distanceToTheFarPlane)
	{
		final float angleInDegrees = (float) (yFieldOfViewInDegrees * Math.PI / 180.0);
		final float focalLengthOfCamera = (float) (1.0 / Math.tan(angleInDegrees / 2.0));
		
		matrix[0] = focalLengthOfCamera / aspectRatio;
		matrix[1] = 0.0f;
		matrix[2] = 0.0f;
		matrix[3] = 0.0f;
		
		matrix[4] = 0.0f;
		matrix[5] = focalLengthOfCamera;
		matrix[6] = 0.0f;
		matrix[7] = 0.0f;
		
		matrix[8] = 0.0f;
		matrix[9] = 0.0f;
		matrix[10] = -((distanceToTheFarPlane + distanceToNearPlane) / 
						(distanceToTheFarPlane - distanceToNearPlane));
		matrix[11] = -1.0f;
		
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = -((2.0f * distanceToTheFarPlane * distanceToNearPlane) / 
						(distanceToTheFarPlane - distanceToNearPlane));
		matrix[15] = 0.0f;
	}
}
