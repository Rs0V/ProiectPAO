package org.framework.actor.components;

import lombok.AllArgsConstructor;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.ActorManager;
import org.framework.services.TimeManager;
import org.framework.services.enums.RenderHints;

import java.awt.*;

public class CRemoveActor implements IComponent {
	protected String actorId;
	protected double waitTime;
	protected boolean active = false;


	public CRemoveActor(String actorId, double waitTime) {
		this.actorId = actorId;
		this.waitTime = waitTime;
	}

	public void activate() {
		this.active = true;
	}

	@Override
	public void update() {
		if (this.active) {
			this.waitTime -= TimeManager.getDeltaTime();
			if (this.waitTime < 0)
				ActorManager.removeActor(this.actorId);
		}
	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

	}
}
