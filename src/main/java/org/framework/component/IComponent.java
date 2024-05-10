package org.framework.component;

import org.framework.actor.Camera;

import java.awt.*;

public interface IComponent {
	public void update(double deltaTime);
	public void render(Graphics2D g2d, Camera camera, double deltaTime);
}
