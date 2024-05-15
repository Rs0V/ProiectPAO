package org.framework.actor;

import lombok.Getter;
import lombok.Setter;
import org.framework.Game;
import org.framework.services.InputMapper;
import org.framework.services.TimeManager;
import org.framework.vec2.Vec2;

import java.awt.*;


@Getter @Setter
public class Pawn extends Actor {

	public Pawn(String id) {
		super(id);
	}

	@Override
	public void update() {
		super.update();

		boolean leftMove = InputMapper.checkAction("left-move");
		boolean rightMove = InputMapper.checkAction("right-move");
		boolean upMove = InputMapper.checkAction("up-move");
		boolean downMove = InputMapper.checkAction("down-move");

		boolean leftLean = InputMapper.checkAction("left-lean");
		boolean rightLean = InputMapper.checkAction("right-lean");


		Vec2 moveInput = new Vec2(
				(rightMove ? 1 : 0) - (leftMove ? 1 : 0),
				(downMove ? 1 : 0) - (upMove ? 1 : 0)
		);
		moveInput = moveInput.normalized();

		if (leftMove || rightMove || upMove || downMove) {
			this.getTransform().moveLocal(moveInput.mul(TimeManager.getDeltaTime()).mul(200));
		}
		if (leftLean) {
			this.getTransform().rotate(-200 * TimeManager.getDeltaTime());
		}
		if (rightLean) {
			this.getTransform().rotate(200 * TimeManager.getDeltaTime());
		}
	}
}
