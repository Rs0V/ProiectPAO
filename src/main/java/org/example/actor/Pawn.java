package org.example.actor;

import org.example.collider.Collider;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

public class Pawn extends Actor {
	public Pawn() {
		super();
	}
	public Pawn(Transform transform) {
		super(transform);
	}
	public Pawn(Collider collider) {
		super(collider);
	}
	public Pawn(Transform transform, Collider collider) {
		super(transform, collider);
	}
}
