package com.airhockey.android.util;

import android.util.FloatMath;

public class Geometry
{
	private static final String TAG = "Geometry";
	
	public static class Point
	{
		public final float mX;
		public final float mY;
		public final float mZ;
		
		public Point(float x, float y, float z)
		{
			mX = x;
			mY = y;
			mZ = z;
		}
		
		public float getX()
		{
			return mX;
		}
		public float getY()
		{
			return mY;
		}
		public float getZ()
		{
			return mZ;
		}
		
		public Point translate(Vector v)
		{
			return new Point(mX + v.getX(),
							mY + v.getY(),
							mZ + v.getZ());
		}
		
		public Point translateY(float distance)
		{
			return new Point(mX, mY + distance, mZ);
		}
		
		@Override
		public String toString()
		{
			return "("+ mX + "," + mY + "," + mZ + ")";
		}
	}
	
	public static class Vector
	{
		public final float mX;
		public final float mY;
		public final float mZ;
		
		public Vector(float x, float y, float z)
		{
			mX = x;
			mY = y;
			mZ = z;
		}
		
		public float getX()
		{
			return mX;
		}
		
		public float getY()
		{
			return mY;
		}
		
		public float getZ()
		{
			return mZ;
		}
		
		public float dotProduct(Vector other)
		{
			return (mX * other.getX() + 
					mY * other.getY() +
					mZ * other.getZ()
					);
		}
		
		public Vector scale(float factor)
		{
			return new Vector(mX * factor, 
					mY * factor, mZ * factor);
		}
		
		public Vector crossProduct(Vector other)
		{
			final float crossX = mY * other.getZ() - mZ * other.getY();
			final float crossY = mZ * other.getX() - mX * other.getZ();
			final float crossZ = mX * other.getY() - mY * other.getX();
			return new Vector(crossX, crossY, crossZ);
		}
		
		public float length()
		{
			return FloatMath.sqrt(mX * mX + mY * mY + mZ * mZ);
		}
		
		@Override
		public String toString()
		{
			return "("+ mX + "," + mY + "," + mZ + ")";
		}
	}
	
	public static class Circle
	{
		public final Point mCenter;
		public final float mRadius;
		
		public Circle(Point center, float radius)
		{
			mCenter = center;
			mRadius = radius;
		}
		
		public Circle scale(float scaleFactor)
		{
			return new Circle(mCenter, mRadius * scaleFactor);
		}
	}
	
	public static class Cylinder
	{
		public final Point mCenter;
		public final float mRadius;
		public final float mHeight;
		
		public Cylinder(Point center, float radius, float height)
		{
			mCenter = center;
			mRadius = radius;
			mHeight = height;
		}
		
		public Point getCenter()
		{
			return mCenter;
		}
		public float getRadius()
		{
			return mRadius;
		}
		public float getHeight()
		{
			return mHeight;
		}
	}
	
	public static class Ray
	{
		private final Point mStartPoint;
		private final Vector mDirectionVector;
		
		public Ray(Point startPoint, Vector directionVector)
		{
			mStartPoint = startPoint;
			mDirectionVector = directionVector;
		}
		
		public Point getStartPoint()
		{
			return mStartPoint;
		}
		
		public Vector getDirectionVector()
		{
			return mDirectionVector;
		}
	}
	
	public static class Sphere
	{
		private final Point mCenter;
		private final float mRadius;
		
		public Sphere(Point center, float radius)
		{
			mCenter = center;
			mRadius = radius;
		}
		
		public Point getCenter()
		{
			return mCenter;
		}
		
		public float getRadius()
		{
			return mRadius;
		}
	}
	
	public static class Plane
	{
		private final Point mOrigin;
		private final Vector mNormal;
		
		public Plane(Point origin, Vector normal)
		{
			mOrigin = origin;
			mNormal = normal;
		}
		
		public Point getOrigin()
		{
			return mOrigin;
		}
		
		public Vector getNormalVector()
		{
			return mNormal;
		}
	}
	
	public static Vector vectorBetween(Point from, Point to)
	{
		return new Vector(to.getX() - from.getX(), 
						to.getY() - from.getY(),
						to.getZ() - from.getZ());
	}
	
	public static boolean intersects(Sphere sphere, Ray ray)
	{
		return distanceBetween(sphere.getCenter(), ray) < sphere.getRadius();
	}
	
	public static float distanceBetween(Point point, Ray ray)
	{
		Vector p1ToPoint = vectorBetween(ray.getStartPoint(), point);
		Vector p2ToPoint = vectorBetween(ray.getStartPoint().translate(ray.getDirectionVector()), 
				point);
		
		// http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
		float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
		float lengthOfBase = ray.getDirectionVector().length();
		
		float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
		return distanceFromPointToRay;
	}
	
	public static Point intersectionPoint(Ray ray, Plane plane)
	{
		Point rayStartPoint = ray.getStartPoint();
		Vector rayToPlaneVector = vectorBetween(rayStartPoint, plane.getOrigin());
		
		Vector planeNormalVector = plane.getNormalVector();
		Vector rayDirectionVector = ray.getDirectionVector();
		float scaleFactor = rayToPlaneVector.dotProduct(planeNormalVector)
				/ ray.getDirectionVector().dotProduct(planeNormalVector);
		
		Point intersectionPoint = rayStartPoint.translate(rayDirectionVector.scale(scaleFactor));
		return intersectionPoint;
	}
}
