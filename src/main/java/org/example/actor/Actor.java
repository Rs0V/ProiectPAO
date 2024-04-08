package org.example.actor;

import lombok.Getter;
import lombok.Setter;
import org.example.collider.Collider;
import org.example.collider.ColliderType;
import org.example.sprite.Sprite;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Actor {
	@Getter @Setter
	protected Transform transform;
	@Getter @Setter
	protected Collider collider;
	@Getter @Setter
	protected Sprite sprite;


	public Actor(Transform transform, Collider collider, Sprite sprite) {
		this.transform = transform == null ? new Transform(null, 0, null) : transform;
		this.collider = collider == null ? new Collider(null, null, ColliderType.Block) : collider;
		this.sprite = sprite == null ? new Sprite(null, null, null) : sprite;
	}

	public boolean checkCollision(Actor other) throws Exception {
		// TO-DO
		return this.collider.checkCollision(other.collider);
	}
}
