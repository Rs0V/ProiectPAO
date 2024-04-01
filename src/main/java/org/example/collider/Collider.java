package org.example.collider;

import org.example.shape.Circle;
import org.example.shape.Rectangle;
import org.example.shape.Shape;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

public class Collider {
	private Transform transform;
	private Shape shape;
	private ColliderType type;

	public Collider(Shape shape) {
		this.shape = shape;
		this.transform = new Transform();
		this.type = ColliderType.Block;
	}
	public Collider(Shape shape, ColliderType type) {
		this.shape = shape;
		this.transform = new Transform();
		this.type = type;
	}
	public Collider(Shape shape, Transform transform, ColliderType type) {
		this.shape = shape;
		this.transform = transform;
		this.type = type;
	}

	public Vec2 checkCollision(Collider other) {
		if (this.type == ColliderType.Ignore){
//			throw new Exception("'checkCollision()' called on Collider of type ColliderType.Ignore");
		}

		if (other.type == ColliderType.Ignore)
			return null;

		boolean collided = false;
		if (this.shape.getClass() == Rectangle.class) {
			if (other.shape.getClass() == Rectangle.class) {
				// TO-DO
			}
			else if (other.shape.getClass() == Circle.class) {
				// TO-DO
			}
		}
		else if (this.shape.getClass() == Circle.class) {
			if (other.shape.getClass() == Rectangle.class) {
				// TO-DO
			}
			else if (other.shape.getClass() == Circle.class) {
				// TO-DO
			}
		}

		return (collided) ? new Vec2(123, 123) : new Vec2();
	}

	public Transform getTransform() {
		return this.transform;
	}
	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public ColliderType getType() {
		return this.type;
	}
	public void setType(ColliderType type) {
		this.type = type;
	}
}
