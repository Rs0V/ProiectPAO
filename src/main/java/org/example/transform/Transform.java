package org.example.transform;

import org.example.vec2.Vec2;

public class Transform {
	private Vec2 location, scale;
	private double rotation; // in degrees

	public Transform() {
		this.location = new Vec2();
		this.rotation = 0;
		this.scale = new Vec2(1, 1);
	}
	public Transform(Vec2 location) {
		this.location = location;
		this.rotation = 0;
		this.scale = new Vec2(1, 1);
	}
	public Transform(Vec2 location, double rotation, Vec2 scale) {
		this.location = location;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vec2 getForward() {
		return new Vec2(Math.cos(Math.toRadians(-this.rotation)), Math.sin(Math.toRadians(-this.rotation)));
	}
	public Vec2 getUp() {
		return new Vec2(Math.cos(Math.toRadians(-this.rotation + 90)), Math.sin(Math.toRadians(-this.rotation + 90)));
	}

	public Transform moveGlobal(Vec2 delta) {
		this.location = this.location.add(delta);
		return this;
	}
	public Transform moveLocal(Vec2 delta) {
		this.location = this.getForward().mul(delta.x).add(this.getUp().mul(delta.y));
		return this;
	}

	public Vec2 getLocation() {
		return this.location;
	}
	public Transform setLocation(Vec2 location) {
		this.location = location;
		return this;
	}

	public Transform rotate(double delta) {
		this.rotation += delta;
		return this;
	}

	public double getRotation() {
		return this.rotation;
	}
	public Transform setRotation(double rotation) {
		this.rotation = rotation;
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

	public Vec2 getScale() {
		return this.scale;
	}
	public Transform setScale(Vec2 scale) {
		this.scale = scale;
		return this;
	}
}
