package org.framework.services;

import lombok.Getter;

public abstract class TimeManager {
	@Getter
	private static double time = 0;
	@Getter
	private static double deltaTime = 0;

	private static long beginTime;
	private static long loadTime;
	private static long now;
	private static long last;


	private static final double timeMeas = 1.0 / 1_000_000_000;

	/**
	 * Used only once; in the constructor of Game
	 */
	public static void init() {
		beginTime = System.nanoTime(); // Game Start time before loading assets
	}

	/**
	 * Used only once; in the beggining of Game._start()
	 */
	public static void start() {
		now = System.nanoTime();
		loadTime = now - beginTime; // Time it takes to load assets
		last = now;
	}

	/**
	 * Used only once; in the beggining of Game.update()
	 */
	public static void update() {
		now = System.nanoTime();
		time = (now - (beginTime + loadTime)) * timeMeas; // Current time in seconds
		deltaTime = (now - last) * timeMeas * GameProperties.getTimeFactor(); // Delta time in seconds
		last = now;
	}
}
