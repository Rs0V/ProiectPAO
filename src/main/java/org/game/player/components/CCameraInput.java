package org.game.player.components;

import lombok.AllArgsConstructor;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.ChartEditor;
import org.framework.services.InputMapper;
import org.framework.services.enums.Arrows;
import org.framework.services.enums.RenderHints;
import org.framework.sound.CSound;
import org.framework.vec2.Vec2;

import java.awt.*;

public class CCameraInput implements IComponent {
	protected Camera self;
	protected double time = 0;


	public CCameraInput(Camera camera) {
		this.self = camera;
	}

	protected boolean leftHeld = false;
	protected boolean rightHeld = false;
	protected boolean upHeld = false;
	protected boolean downHeld = false;
	@Override
	public void update(double deltaTime) {
		time += deltaTime;

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
//			self.getTransform().moveLocal(moveInput.mul(deltaTime).mul(200));
		}

		if (leftMove) {
			if (this.leftHeld == false) {
				((CSound) self.getComponents().get("click-sound")).play();
				ChartEditor.checkNoteHit(Arrows.Left, this.time);
				this.leftHeld = true;
			}
		} else {
			this.leftHeld = false;
		}
		if (rightMove) {
			if (this.rightHeld == false) {
				((CSound) self.getComponents().get("click-sound")).play();
				ChartEditor.checkNoteHit(Arrows.Right, this.time);
				this.rightHeld = true;
			}
		} else {
			this.rightHeld = false;
		}
		if (upMove) {
			if (this.upHeld == false) {
				((CSound) self.getComponents().get("click-sound")).play();
				ChartEditor.checkNoteHit(Arrows.Up, this.time);
				this.upHeld = true;
			}
		} else {
			this.upHeld = false;
		}
		if (downMove) {
			if (this.downHeld == false) {
				((CSound) self.getComponents().get("click-sound")).play();
				ChartEditor.checkNoteHit(Arrows.Down, this.time);
				this.downHeld = true;
			}
		} else {
			this.downHeld = false;
		}

		if (leftLean || rightLean) {
			self.getTransform().rotate(deltaTime * 200 * ((rightLean?1:0) - (leftLean?1:0)));
		}
		if (zoomIn || zoomOut) {
			self.setZoom(self.getZoom() + ((zoomIn?1:0) - (zoomOut?1:0)) * deltaTime);
		}
	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera, double deltaTime) {

	}
}
