package org.framework.ui;

import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.enums.RenderHints;
import org.framework.sprite.AnimatedSprite;
import org.framework.sprite.Sprite;
import org.framework.transform.Transform;
import org.framework.vec2.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class UIElement {
	protected final String id;
	protected Transform transform;
	protected Sprite sprite;
	protected boolean affectedByCamera = false;

	protected Map<String, IComponent> components;


	public UIElement(String id) {
		this.id = id;
		this.transform = new Transform(null, null, null, null);
		this.sprite = new Sprite(null, null, null);

		this.components = new HashMap<>();
	}

	public UIElement addComponent(String compName, IComponent component) {
		this.components.put(compName, component);
		return this;
	}

	public void update() {
		this.components.forEach((k, c) -> c.update());
		if (this.sprite instanceof AnimatedSprite animatedSprite) {
			animatedSprite.getAnimComp().update();
		}
	}

	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {
		if (this.sprite == null)
			return;
		this.components.forEach((k, c) -> c.render(g2d, renderHints, camera));
		if (this.sprite instanceof AnimatedSprite animatedSprite) {
			animatedSprite.getAnimComp().render(g2d, renderHints, camera);
		}

		var rh = switch (renderHints) {
			case Pixelated -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
			case Smooth -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		};
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, rh);


		AffineTransform at = new AffineTransform();

		if (this.affectedByCamera == true) {
			at.translate(camera.getScreenSize().x / 2, camera.getScreenSize().y / 2);
			at.scale(camera.getZoom(), camera.getZoom());
			at.translate(-camera.getTransform().getLocation().x, -camera.getTransform().getLocation().y);
			at.translate(-camera.getScreenSize().x / 2, -camera.getScreenSize().y / 2);
			at.rotate(-camera.getTransform().getActualRotation(), camera.getScreenSize().x / 2, camera.getScreenSize().y / 2);
		}

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
