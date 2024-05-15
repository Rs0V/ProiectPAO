package org.framework.component;

import org.framework.actor.Camera;
import org.framework.services.enums.RenderHints;

import java.awt.*;

public interface IComponent {
	public void update();
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera);
}
