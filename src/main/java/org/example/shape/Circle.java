package org.example.shape;

public class Circle extends Shape {
	private double radius;

	public Circle() {
		this.radius = 1;
	}
	public Circle(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return this.radius;
	}
	public Circle setRadius(double radius) {
		this.radius = radius;
		return this;
	}
}
