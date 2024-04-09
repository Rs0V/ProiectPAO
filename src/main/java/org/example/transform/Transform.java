package org.example.transform;

import lombok.Getter;
import lombok.Setter;
import org.example.vec2.Vec2;

public class Transform {
	@Getter @Setter
	private Vec2 location;
	@Getter @Setter
	private Vec2 scale;
	@Getter @Setter
	private double rotation; // in clockwise degrees
	@Getter @Setter
	private double depth;


	public Transform(Vec2 location, Double rotation, Vec2 scale, Double depth) {
		this.location = location == null ? new Vec2() : location;
		this.rotation = rotation == null ? 0 : rotation;
		this.scale = scale == null ? new Vec2(1, 1) : scale;
		this.depth = depth == null ? 0 : depth;
	}

	public Vec2 getForward() {
		return new Vec2(Math.cos(Math.toRadians(this.rotation)), Math.sin(Math.toRadians(this.rotation))).normalized();
	}
	public Vec2 getUp() {
		return new Vec2(Math.cos(Math.toRadians(this.rotation + 90)), Math.sin(Math.toRadians(this.rotation + 90))).normalized();
	}

	public Transform moveGlobal(Vec2 delta) {
		this.location = this.location.add(delta);
		return this;
	}
	public Transform moveLocal(Vec2 delta) {
		this.location = this.location.add(this.getForward().mul(delta.x).add(this.getUp().mul(delta.y)));
		return this;
	}

	public Transform rotate(double delta) {
		this.rotation += delta;
		return this;
	}

	public Transform mulScale(double factor) {
		this.scale = this.scale.mul(factor);
		return this;
	}
	public Transform mulScale(Vec2 factor) {
		this.scale = new Vec2(this.scale.x * factor.x, this.scale.y * factor.y);
		return this;
	}

	public double getActualRotation() {
		return Math.toRadians(this.rotation);
	}
	public Transform setActualRotation(double rotation) {
		this.rotation = Math.toDegrees(rotation);
		return this;
	}
}
