package org.game.player.components;

import lombok.AllArgsConstructor;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.InputMapper;
import org.framework.vec2.Vec2;

import java.awt.*;

@AllArgsConstructor
public class CCameraInput implements IComponent {
	Camera self;


	@Override
	public void update(double deltaTime) {
		boolean leftMove = InputMapper.checkAction("left-arrow");
		boolean rightMove = InputMapper.checkAction("right-arrow");
		boolean upMove = InputMapper.checkAction("up-arrow");
		boolean downMove = InputMapper.checkAction("down-arrow");

		boolean leftLean = InputMapper.checkAction("left-lean");
		boolean rightLean = InputMapper.checkAction("right-lean");

		boolean zoomIn = InputMapper.checkAction("zoom-in");
		boolean zoomOut = InputMapper.checkAction("zoom-out");


		Vec2 moveInput = new Vec2(
				(rightMove ? 1 : 0) - (leftMove ? 1 : 0),
				(downMove ? 1 : 0) - (upMove ? 1 : 0)
		);
		moveInput = moveInput.normalized();

		if (leftMove || rightMove || upMove || downMove) {
			self.getTransform().moveLocal(moveInput.mul(deltaTime).mul(200));
		}
		if (leftLean || rightLean) {
			self.getTransform().rotate(deltaTime * 200 * ((rightLean?1:0) - (leftLean?1:0)));
		}
		if (zoomIn || zoomOut) {
			self.setZoom(self.getZoom() + ((zoomIn?1:0) - (zoomOut?1:0)) * deltaTime);
		}
	}

	@Override
	public void render(Graphics2D g2d, Camera camera, double deltaTime) {

	}
}
