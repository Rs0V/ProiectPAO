package org.utilities;


import org.framework.vec2.Vec2;

public abstract class Utilities {
	public static <T extends Comparable<T>> T jumpClamp(T value, T min, T max) {
		if (value.compareTo(min) < 0)
			return max;
		else if (value.compareTo(max) > 0)
			return min;
		return value;
	}

	public static int lerp(int min, int max, double alpha) {
		return (int) (alpha * max + (1 - alpha) * min);
	}
	public static long lerp(long min, long max, double alpha) {
		return (long) (alpha * max + (1 - alpha) * min);
	}
	public static float lerp(float min, float max, double alpha) {
		return (float) (alpha * max + (1 - alpha) * min);
	}
	public static double lerp(double min, double max, double alpha) {
		return alpha * max + (1 - alpha) * min;
	}
	public static Vec2 lerp(Vec2 min, Vec2 max, double alpha) {
		return Vec2.lerp(min, max, alpha);
	}


	public static double lext(int min, int max, int value) {
		return (value - min) * 1.0 / (max - min);
	}
	public static double lext(long min, long max, long value) {
		return (value - min) * 1.0 / (max - min);
	}
	public static double lext(float min, float max, float value) {
		return (value - min) / (max - min);
	}
	public static double lext(double min, double max, double value) {
		return (value - min) / (max - min);
	}
	public static double lext(Vec2 min, Vec2 max, Vec2 value) {
		double x = (value.x - min.x) / (max.x - min.x);
		double y = (value.y - min.y) / (max.y - min.y);
		return (x + y) / 2;
	}


	public static String generateId() {
		StringBuilder id = new StringBuilder();
		for (int i = 0; i < 18; i++) {
			if (i % 6 == 0) {
				id.append("-");
				continue;
			}
			if (Math.random() > .5)
				id.append(String.format("%d", 65 + (int) (Math.random() * 26)));
			else
				id.append(String.format("%d", 65 + (int) (Math.random() * 26)).toLowerCase());
		}
		return id.toString();
	}
}
