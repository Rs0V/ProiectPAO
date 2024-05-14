package org.framework.component;

import org.framework.actor.Camera;
import org.framework.services.enums.RenderHints;

import java.awt.*;

public interface IComponent {
	public void update(double deltaTime);
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera, double deltaTime);
}
