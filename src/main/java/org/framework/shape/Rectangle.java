package org.framework.shape;

import lombok.Getter;
import lombok.Setter;
import org.framework.vec2.Vec2;

@Getter @Setter
public class Rectangle extends Shape {
	private Vec2 size;


	public Rectangle(Vec2 size) {
		this.size = size == null ? new Vec2(1, 1) : size;
	}

	public double getWidth() {
		return this.size.x;
	}
	public Rectangle setWidth(double width) {
		this.size.x = width;
		return this;
	}

	public double getHeight() {
		return this.size.y;
	}
	public Rectangle setHeight(double height) {
		this.size.y = height;
		return this;
	}
}
