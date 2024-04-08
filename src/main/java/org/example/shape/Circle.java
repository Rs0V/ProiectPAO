package org.example.shape;

import lombok.Getter;
import lombok.Setter;

public class Circle extends Shape {
	@Getter @Setter
	private double radius;


	public Circle(double radius) {
		this.radius = radius;
	}
}
