package org.game.player.components;

import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.ChartEditor;
import org.framework.services.InputMapper;
import org.framework.services.Recorder;
import org.framework.services.TimeManager;
import org.framework.services.enums.Arrows;
import org.framework.services.enums.RenderHints;
import org.framework.sound.components.CSound;
import org.framework.vec2.Vec2;

import java.awt.*;

public class CCameraInput implements IComponent {
	protected Camera self;


	public CCameraInput(Camera camera) {
		this.self = camera;
	}

	protected boolean leftHeld = false;
	protected boolean rightHeld = false;
	protected boolean upHeld = false;
	protected boolean downHeld = false;
	@Override
	public void update() {
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

//		if (leftMove || rightMove || upMove || downMove) {
//			self.getTransform().moveLocal(moveInput.mul(TimeManager.getDeltaTime()).mul(200));
//		}

		if (leftMove) {
			if (this.leftHeld == false) {
				((CSound) self.getComponents().get("left-click")).play();
				ChartEditor.checkNoteHit(Arrows.Left);
				this.leftHeld = true;

				Recorder.record("presses", String.format("%f -> left%n", TimeManager.getTime()));
			}
		} else {
			this.leftHeld = false;
		}
		if (rightMove) {
			if (this.rightHeld == false) {
				((CSound) self.getComponents().get("right-click")).play();
				ChartEditor.checkNoteHit(Arrows.Right);
				this.rightHeld = true;

				Recorder.record("presses", String.format("%f -> right%n", TimeManager.getTime()));
			}
		} else {
			this.rightHeld = false;
		}
		if (upMove) {
			if (this.upHeld == false) {
				((CSound) self.getComponents().get("up-click")).play();
				ChartEditor.checkNoteHit(Arrows.Up);
				this.upHeld = true;

				Recorder.record("presses", String.format("%f -> up%n", TimeManager.getTime()));
			}
		} else {
			this.upHeld = false;
		}
		if (downMove) {
			if (this.downHeld == false) {
				((CSound) self.getComponents().get("down-click")).play();
				ChartEditor.checkNoteHit(Arrows.Down);
				this.downHeld = true;

				Recorder.record("presses", String.format("%f -> down%n", TimeManager.getTime()));
			}
		} else {
			this.downHeld = false;
		}

		if (leftLean || rightLean) {
			self.getTransform().rotate(200 * ((rightLean?1:0) - (leftLean?1:0)) * TimeManager.getDeltaTime());
		}
		if (zoomIn || zoomOut) {
			self.setZoom(self.getZoom() + ((zoomIn?1:0) - (zoomOut?1:0)) * TimeManager.getDeltaTime());
		}
	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

	}
}
