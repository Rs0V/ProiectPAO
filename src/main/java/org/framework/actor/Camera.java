package org.framework.actor;

import lombok.Getter;
import lombok.Setter;
import org.framework.services.GameProperties;
import org.framework.transform.Transform;
import org.framework.vec2.Vec2;

@Getter @Setter
public class Camera extends Actor {
	private double zoom;
	private Vec2 screenSize;


	public Camera(String id) {
		super(id);
		this.sprite = null;
		this.collider = null;

		this.zoom = 1;
		this.screenSize = new Vec2(GameProperties.getScreenRes().x, GameProperties.getScreenRes().y);
	}
}
