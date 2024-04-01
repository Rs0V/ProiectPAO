package org.example.actor;

import org.example.collider.Collider;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

public class Actor {
	protected Transform transform;
	protected Collider collider;

	public Actor() {
		this.transform = new Transform();
		this.collider = null;
	}
	public Actor(Transform transform) {
		this.transform = transform;
		this.collider = null;
	}
	public Actor(Collider collider) {
		this.collider = collider;
	}
	public Actor(Transform transform, Collider collider) {
		this.transform = transform;
		this.collider = collider;
	}

	public Transform getTransform() {
		return this.transform;
	}
	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public Vec2 checkCollision(Actor other) {
		// TO-DO
		return this.collider.checkCollision(other.collider);
	}
}
