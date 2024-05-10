package org.game.player.objects;

import lombok.Getter;
import lombok.Setter;
import org.framework.Game;
import org.framework.actor.Pawn;
import org.framework.services.GameProperties;
import org.framework.services.InputMapper;
import org.framework.vec2.Vec2;

import java.awt.*;

@Getter @Setter
public class Player extends Pawn {
	protected double speed;
	protected double jumpForce;
	protected double gravity = 0;
	protected double mass;

	public Player(String id) {
		super(id);
		this.speed = 400;
		this.jumpForce = 900;
		this.mass = 60;
	}

	@Override
	public void update(double deltaTime) {
		boolean leftMove = InputMapper.checkAction("left-move");
		boolean rightMove = InputMapper.checkAction("right-move");
		boolean upMove = InputMapper.checkAction("up-move");

		Vec2 moveInput = new Vec2(
				(rightMove ? 1 : 0) - (leftMove ? 1 : 0),
				0
		);
		moveInput = moveInput.normalized();

		if (leftMove || rightMove) {
			this.getTransform().moveLocal(moveInput.mul(this.speed).mul(deltaTime));
		}
		if (upMove && this.gravity < 1 /*jump threshold*/) {
			this.gravity = this.jumpForce;
		}
		this.getTransform().moveLocal(new Vec2(0, -this.gravity * deltaTime));
		this.gravity -= this.mass * GameProperties.getG() * deltaTime;
	}
}
