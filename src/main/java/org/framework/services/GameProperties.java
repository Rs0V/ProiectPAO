package org.framework.services;


import jdk.jfr.Unsigned;
import lombok.Getter;
import lombok.Setter;
import org.framework.vec2.Vec2;

public abstract class GameProperties {
	@Unsigned @Getter
	private static final int fps = 240;
	@Getter @Setter
	private static Vec2 screenRes = new Vec2(800, 600);
	@Getter @Setter
	private static double g = 38;
	@Getter @Setter
	private static double timeFactor = 1;
}
