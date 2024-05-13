package org.framework.actor;

import lombok.Getter;
import lombok.Setter;
import org.framework.collider.Collider;
import org.framework.collider.ColliderType;
import org.framework.component.IComponent;
import org.framework.sprite.Sprite;
import org.framework.transform.Transform;
import org.framework.vec2.Vec2;
import org.utilities.CSet;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;


@Getter @Setter
public class Actor {
	protected final String id;
	protected Transform transform;
	protected Collider collider;
	protected Sprite sprite;

	protected Map<String, IComponent> components;


	public Actor(String id) {
		this.id = id;
		this.transform = new Transform(null, null, null, null);
		this.collider = new Collider(null, null, ColliderType.Block);
		this.sprite = new Sprite(null, null, null);

		this.components = new HashMap<>();
	}

	public boolean checkCollision(Actor other) throws Exception {
		// TO-DO
		return this.collider.checkCollision(other.collider);
	}

	public Actor addComponent(String compName, IComponent component) {
		this.components.put(compName, component);
		return this;
	}

	public void update(double deltaTime) {
		this.components.forEach((k, c) -> c.update(deltaTime));
	}

	public void render(Graphics2D g2d, Camera camera, double deltaTime) {
		if (this.sprite == null)
			return;
		this.components.forEach((k, c) -> c.render(g2d, camera, deltaTime));


		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);


		AffineTransform at = new AffineTransform();
//		at.translate(game.getInsets().left, game.getInsets().top);
		at.translate(camera.getScreenSize().x / 2, camera.getScreenSize().y / 2);
		at.scale(camera.getZoom(), camera.getZoom());
		at.translate(-camera.getTransform().getLocation().x, -camera.getTransform().getLocation().y);
		at.translate(-camera.getScreenSize().x / 2, -camera.getScreenSize().y / 2);
		at.rotate(-camera.getTransform().getActualRotation(), camera.getScreenSize().x / 2, camera.getScreenSize().y / 2);


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
