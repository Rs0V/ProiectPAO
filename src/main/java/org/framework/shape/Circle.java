package org.framework.shape;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Circle extends Shape {
	private double radius;


	public Circle(Double radius) {
		this.radius = radius == null ? 1 : radius;
	}
}
