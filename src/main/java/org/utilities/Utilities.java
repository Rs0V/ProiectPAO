package org.utilities;



public abstract class Utilities {
	public static int jumpClamp(int value, int min, int max) {
		if (value < min)
			return max;
		else if (value > max)
			return min;
		return value;
	}
}
