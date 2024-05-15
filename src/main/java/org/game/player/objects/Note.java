package org.game.player.objects;

import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.collider.Collider;
import org.framework.collider.ColliderType;
import org.framework.services.ActorManager;
import org.framework.services.TimeManager;
import org.framework.services.enums.Arrows;
import org.framework.services.enums.RenderHints;
import org.framework.sprite.Sprite;
import org.framework.vec2.Vec2;

import java.awt.*;

@Getter
public class Note extends Actor {
	@Setter
	protected double speed;
	@Setter
	protected double hitTime; // The moment (in seconds) when the note should be pressed
	protected Arrows arrow;
	@Setter
	protected double despawnY;

	public Note(String id) {
		super(id);
		this.setCollider(new Collider(null, null, ColliderType.Overlap));
	}

	@Override
	public void update() {
		super.update();
		this.getTransform().moveGlobal(new Vec2(0, speed * TimeManager.getDeltaTime()));
		if (this.getTransform().getLocation().y > this.despawnY) {
			System.out.printf("%nRemoving object with ID: '" + this.id + "' ...%n%n");
			ActorManager.removeActor(this.id);
		}
	}

	public Note setArrow(Arrows arrow) {
		this.arrow = arrow;
		this.sprite = new Sprite(
				switch (this.arrow) {
					case Left -> "src/main/resources/images/left_arrow.png";
					case Right -> "src/main/resources/images/right_arrow.png";
					case Up -> "src/main/resources/images/up_arrow.png";
					case Down -> "src/main/resources/images/down_arrow.png";
				},
				null,
				new Vec2(.3, .3)
		);
		this.getTransform().setRotation(
				switch (this.arrow) {
					case Left -> -90;
					case Right -> 90;
					case Up -> 0;
					case Down -> -180;
				}
		);
		return this;
	}
}
