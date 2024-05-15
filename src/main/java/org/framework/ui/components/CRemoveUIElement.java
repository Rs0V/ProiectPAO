package org.framework.ui.components;

import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.ActorManager;
import org.framework.services.TimeManager;
import org.framework.services.UIManager;
import org.framework.services.enums.RenderHints;

import java.awt.*;

public class CRemoveUIElement implements IComponent {
	protected String uiElementId;
	protected double waitTime;
	protected boolean active = false;


	public CRemoveUIElement(String uiElementId, double waitTime) {
		this.uiElementId = uiElementId;
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
				UIManager.removeUIElement(this.uiElementId);
		}
	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

	}
}
