package org.example.collider;

import lombok.Getter;
import lombok.Setter;
import org.example.shape.Circle;
import org.example.shape.Rectangle;
import org.example.shape.Shape;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

@Getter @Setter
public class Collider {
	private Transform transform;
	private Shape shape;
	private ColliderType type;


	public Collider(Shape shape, Transform transform, ColliderType type) {
		this.shape = shape == null ? new Rectangle(null) : shape;
		this.transform = transform == null ? new Transform(null, null, null, null) : transform;
		this.type = type;
	}

	public boolean checkCollision(Collider other) throws Exception {
		if (this.type == ColliderType.Ignore){
			throw new Exception("'checkCollision()' called on Collider of type ColliderType.Ignore");
		}

		if (other.type == ColliderType.Ignore)
			return false;


		boolean collided = true;
		if (this.shape instanceof Rectangle thisShape) {
			double thisCos = Math.cos(Math.toRadians(-this.transform.getRotation()));
			double thisSin = Math.sin(Math.toRadians(-this.transform.getRotation()));

			Vec2 thisDir = new Vec2(thisCos, thisSin);
			Vec2 thisDirNormal = new Vec2(thisDir.y, -thisDir.x);

			Vec2 thisDisplaceX = thisDir.mul(thisShape.getWidth() / 2).mul(this.transform.getScale().x);
			Vec2 thisDisplaceY = thisDirNormal.mul(thisShape.getHeight() / 2).mul(this.transform.getScale().y);

			Vec2[] thisPoints = {
					this.transform.getLocation().add(thisDisplaceX.mul(-1)).add(thisDisplaceY.mul(-1)),
					this.transform.getLocation().add(thisDisplaceX).add(thisDisplaceY.mul(-1)),
					this.transform.getLocation().add(thisDisplaceX).add(thisDisplaceY),
					this.transform.getLocation().add(thisDisplaceX.mul(-1)).add(thisDisplaceY)
			};

			if (other.shape instanceof Rectangle otherShape) {
				double otherCos = Math.cos(Math.toRadians(-other.transform.getRotation()));
				double otherSin = Math.sin(Math.toRadians(-other.transform.getRotation()));

				Vec2 otherDir = new Vec2(otherCos, otherSin);
				Vec2 otherDirNormal = new Vec2(otherDir.y, -otherDir.x);

				Vec2 otherDisplaceX = otherDir.mul(otherShape.getWidth() / 2).mul(other.transform.getScale().x);
				Vec2 otherDisplaceY = otherDirNormal.mul(otherShape.getHeight() / 2).mul(other.transform.getScale().y);

				Vec2[] otherPoints = {
						other.transform.getLocation().add(otherDisplaceX.mul(-1)).add(otherDisplaceY.mul(-1)),
						other.transform.getLocation().add(otherDisplaceX).add(otherDisplaceY.mul(-1)),
						other.transform.getLocation().add(otherDisplaceX).add(otherDisplaceY),
						other.transform.getLocation().add(otherDisplaceX.mul(-1)).add(otherDisplaceY)
				};

				Vec2[][] pointsSet = {thisPoints, otherPoints};
				for (Vec2[] points : pointsSet) {
					for (int ip1 = 0; ip1 < points.length; ++ip1) {
						int ip2 = (ip1 + 1) % points.length;

						Vec2 point1 = points[ip1];
						Vec2 point2 = points[ip2];

						Vec2 normal = new Vec2(point2.y - point1.y, point1.x - point2.x);

						Double minA = null, maxA = null;
						for (Vec2 point : thisPoints) {
							double projected = normal.dot(point);
							if (minA == null || projected < minA)
								minA = projected;
							if (maxA == null || projected > maxA)
								maxA = projected;
						}

						Double minB = null, maxB = null;
						for (Vec2 point : otherPoints) {
							double projected = normal.dot(point);
							if (minB == null || projected < minB)
								minB = projected;
							if (maxB == null || projected > maxB)
								maxB = projected;
						}

						if (maxA < minB || maxB < minA) {
							collided = false;
							break;
						}
					}
				}
			}
			else if (other.shape instanceof Circle otherShape) {
				for (Vec2 point : thisPoints) {
					if (point.dist(other.transform.getLocation()) > otherShape.getRadius()) {
						collided = false;
					}
				}
			}
		}
		else if (this.shape instanceof Circle thisShape) {
			if (other.shape instanceof Rectangle otherShape) {
				double otherCos = Math.cos(Math.toRadians(-other.transform.getRotation()));
				double otherSin = Math.sin(Math.toRadians(-other.transform.getRotation()));

				Vec2 otherDir = new Vec2(otherCos, otherSin);
				Vec2 otherDirNormal = new Vec2(otherDir.y, -otherDir.x);

				Vec2 otherDisplaceX = otherDir.mul(otherShape.getWidth() / 2).mul(other.transform.getScale().x);
				Vec2 otherDisplaceY = otherDirNormal.mul(otherShape.getHeight() / 2).mul(other.transform.getScale().y);

				Vec2[] otherPoints = {
						other.transform.getLocation().add(otherDisplaceX.mul(-1)).add(otherDisplaceY.mul(-1)),
						other.transform.getLocation().add(otherDisplaceX).add(otherDisplaceY.mul(-1)),
						other.transform.getLocation().add(otherDisplaceX).add(otherDisplaceY),
						other.transform.getLocation().add(otherDisplaceX.mul(-1)).add(otherDisplaceY)
				};

				for (Vec2 point : otherPoints) {
					if (point.dist(this.transform.getLocation()) > thisShape.getRadius()) {
						collided = false;
					}
				}
			}
			else if (other.shape instanceof Circle otherShape) {
				if (this.transform.getLocation().dist(other.transform.getLocation())
						> thisShape.getRadius() + otherShape.getRadius()) {
					collided = false;
				}
			}
		}

		return collided;
	}
}
