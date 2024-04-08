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


	public Transform(Vec2 location, double rotation, Vec2 scale) {
		this.location = location == null ? new Vec2() : location;
		this.rotation = rotation;
		this.scale = scale == null ? new Vec2(1, 1) : scale;;
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
		return Math.toRadians(-this.rotation);
	}
	public Transform setActualRotation(double rotation) {
		this.rotation = Math.toDegrees(-rotation);
		return this;
	}
}
