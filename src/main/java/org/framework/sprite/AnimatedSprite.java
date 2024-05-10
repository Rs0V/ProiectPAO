package org.framework.sprite;

import lombok.Getter;
import lombok.Setter;
import org.framework.vec2.Vec2;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@Getter @Setter
public class AnimatedSprite extends Sprite {
	protected int frameRate;
	protected PlaybackType playbackType;
	protected Vec2 cellSize;
	protected int noImages;
	protected int imgIndex = 0;


	public AnimatedSprite(String imagePath, Vec2 cellSize, int noImages, Integer frameRate) {
		super(imagePath, null, null);
		this.frameRate = frameRate == null ? 15 : frameRate;
		this.playbackType = PlaybackType.Loop;
		this.cellSize = cellSize;
		this.noImages = noImages;
	}

	public void play(PlaybackType playbackType) {
		this.playbackType = playbackType;
	}

	public void stop(AnimStop animStop) {
		switch (animStop) {
			case AnimStop.Start:
				this.playbackType = PlaybackType.Still;
				this.imgIndex = 0;
				break;
			case AnimStop.Current:
				this.playbackType = PlaybackType.Still;
				break;
			case AnimStop.End:
				this.playbackType = PlaybackType.Still;
				this.imgIndex = noImages - 1;
				break;
		}
	}
	public void stopExact(int imgIndex) {
		this.playbackType = PlaybackType.Still;
		this.imgIndex = imgIndex;
	}

	public void reset() {
		this.imgIndex = 0;
	}
}
