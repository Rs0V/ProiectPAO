package org.example.shape;

import org.example.vec2.Vec2;

public class Rectangle extends Shape {
	private Vec2 size;

	public Rectangle() {
		this.size = new Vec2(1, 1);
	}
	public Rectangle(Vec2 size) {
		this.size = size;
	}

	public Vec2 getSize() {
		return this.size;
	}
	public Rectangle setSize(Vec2 size) {
		this.size = size;
		return this;
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
