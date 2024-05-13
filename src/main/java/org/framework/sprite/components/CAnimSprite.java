package org.framework.sprite.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.sprite.AnimatedSprite;
import org.framework.sprite.enums.AnimStop;
import org.framework.sprite.enums.PlaybackType;

import javax.management.ConstructorParameters;
import java.awt.*;

public class CAnimSprite implements IComponent {
	@Getter @Setter
	protected AnimatedSprite animSprite;
	protected PlaybackType playbackType = PlaybackType.Still;
	protected double timer = -1;

	protected int start = 0;
	protected int end = 0;
	protected int direction = 1;
	protected int times = 0;

	protected int stopIndex = -1;


	@Override
	public void update(double deltaTime) {
		if (this.playbackType == PlaybackType.Still) {
			timer = -1;
			return;
		}

		if (timer < 0) {
			switch (this.playbackType) {
				case Play -> {
					if (direction > 0)
						animSprite.goToNextFrame(this.start, this.end);
					else
						animSprite.goToPrevFrame(this.start, this.end);

					timer = 1.0 / animSprite.getFrameRate();
					times -= (times > 0) ? 1 : 0;
				}
				case PingPong -> {
					if (this.animSprite.getImgIndex() == this.start || this.animSprite.getImgIndex() == this.end) {
						this.direction = -this.direction;
						timer = 1.0 / animSprite.getFrameRate();
						times -= (times > 0 && this.animSprite.getImgIndex() == this.start) ? 1 : 0;
					}
				}
			}
		}

		if (times == 0 || (stopIndex > -1 && this.animSprite.getImgIndex() == stopIndex)) {
			this.playbackType = PlaybackType.Still;
			resetVarsToDefault();
		}

		timer -= deltaTime;
	}

	@Override
	public void render(Graphics2D g2d, Camera camera, double deltaTime) {

	}

	public void play(PlaybackType playbackType, Integer start, Integer end, Integer direction, Integer times) {
		this.playbackType = playbackType;

		this.start = start == null ? 0 : start;
		this.end = end == null ? this.animSprite.getFrames().size() - 1 : end;
		assert this.start < this.end;

		this.direction = direction == null ? 1 : direction;
		this.times = times == null ? -1 : times;

		this.animSprite.setImgIndex(this.start);
		this.animSprite.setImage(this.animSprite.getFrames().get(this.start));
		timer = 1.0 / animSprite.getFrameRate();
	}

	public void stop(AnimStop animStop) {
		this.playbackType = PlaybackType.Still;
		switch (animStop) {
			case AnimStop.Start:
				this.animSprite.setImgIndex(0);
				this.animSprite.setImage(this.animSprite.getFrames().getFirst());
				break;
//			case AnimStop.Current:
//				break;
			case AnimStop.End:
				this.animSprite.setImgIndex(this.animSprite.getFrames().size() - 1);
				this.animSprite.setImage(this.animSprite.getFrames().getLast());
				break;
		}
		resetVarsToDefault();
	}

	/**
	 * Waits for the animation to get to the Start/Current/End frame before stopping.
	 */
	public void stopWait(AnimStop animStop) {
		this.stopIndex = switch (animStop) {
			case Start -> this.start;
			case Current -> this.animSprite.getImgIndex();
			case End -> this.end;
		};
	}

	public void stopAt(int imgIndex) {
		this.playbackType = PlaybackType.Still;
		this.animSprite.setImgIndex(imgIndex);
		this.animSprite.setImage(this.animSprite.getFrames().get(imgIndex));
		resetVarsToDefault();
	}

	/**
	 * Waits for the animation to get to the Start/Current/End frame before stopping.
	 * @param imgIndex Index of the frame to stop at.
	 */
	public void stopAtWait(int imgIndex) {
		this.stopIndex = imgIndex;
	}

	protected void resetVarsToDefault() {
		this.timer = -1;
		this.stopIndex = -1;
	}
}
