package org.example.actor;

import lombok.Getter;
import lombok.Setter;
import org.example.Game;
import org.example.collider.Collider;
import org.example.services.InputMapper;
import org.example.sprite.Sprite;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

import java.awt.*;
import java.awt.geom.AffineTransform;


@Getter @Setter
public class Pawn extends Actor {



	public Pawn(String id) {
		super(id);
	}

//	public Pawn(Transform transform, Collider collider, Sprite sprite) {
//		super(transform, collider, sprite);
//	}

	@Override
	public void update(double deltaTime) {
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
			this.getTransform().moveLocal(moveInput.mul(deltaTime).mul(200));
		}
		if (leftLean) {
			this.getTransform().rotate(deltaTime * 200 * -1);
		}
		if (rightLean) {
			this.getTransform().rotate(deltaTime * 200);
		}
	}

	@Override
	public void render(Game game, Graphics2D g2d, double deltaTime) {
		super.render(game, g2d, deltaTime);
	}
}
