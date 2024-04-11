package org.example.actor;

import lombok.Getter;
import lombok.Setter;
import org.example.Game;
import org.example.collider.Collider;
import org.example.collider.ColliderType;
import org.example.sprite.Sprite;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;


@Getter @Setter
public class Actor {
	protected final String id;
	protected Transform transform;
	protected Collider collider;
	protected Sprite sprite;


	public Actor(String id) {
		this.id = id;
		this.transform = new Transform(null, null, null, null);
		this.collider = new Collider(null, null, ColliderType.Block);
		this.sprite = new Sprite(null, null, null);
	}

//	public Actor(String id, Transform transform, Collider collider, Sprite sprite) {
//		this.id = id;
//		this.transform = transform == null ? new Transform(null, null, null, null) : transform;
//		this.collider = collider == null ? new Collider(null, null, ColliderType.Block) : collider;
//		this.sprite = sprite == null ? new Sprite(null, null, null) : sprite;
//	}

	public boolean checkCollision(Actor other) throws Exception {
		// TO-DO
		return this.collider.checkCollision(other.collider);
	}

	public void update(double deltaTime) {

	}

	public void render(Game game, Graphics2D g2d, double deltaTime) {
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);


		AffineTransform at = new AffineTransform();
		at.translate(game.getInsets().left, game.getInsets().top);

		at.translate(this.getTransform().getLocation().x, this.getTransform().getLocation().y);

		Vec2 origin = new Vec2(
				this.getSprite().getImage().getWidth() * this.getSprite().getOrigin().x,
				this.getSprite().getImage().getHeight() * this.getSprite().getOrigin().y
		);
		at.scale(this.getSprite().getScale().x, this.getSprite().getScale().y);
		at.scale(this.getTransform().getScale().x, this.getTransform().getScale().y);

		at.translate(-origin.x, -origin.y);
		at.rotate(this.getTransform().getActualRotation(), origin.x, origin.y);


		g2d.drawImage(this.getSprite().getImage(), at, null);
	}
}
