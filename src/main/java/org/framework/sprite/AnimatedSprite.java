package org.framework.sprite;

import lombok.Getter;
import lombok.Setter;
import org.utilities.Utilities;
import org.framework.sprite.components.CAnimSprite;
import org.framework.vec2.Vec2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class AnimatedSprite extends Sprite {
	protected List<BufferedImage> frames;
	protected int frameRate;
	protected int imgIndex;

	protected CAnimSprite animComp;


	public AnimatedSprite(String imagePath, Vec2 cellSize, int noImages, Integer frameRate) {
		super(imagePath, null, null);
		this.frames = new ArrayList<>();

		this.frameRate = frameRate == null ? 10 : frameRate;
		this.animComp = new CAnimSprite();
		this.animComp.setAnimSprite(this);

		int x = 0, y = 0;
		while(noImages-- > 0) {
			frames.add(this.image.getSubimage(x, y, (int) cellSize.x, (int) cellSize.y));
			x += (int) cellSize.x;
			if (x >= this.image.getWidth()) {
				x = 0;
				y += (int) cellSize.y;
			}
		}

		this.imgIndex = 0;
		this.image = this.frames.getFirst();
	}

	public BufferedImage getNextFrame(Integer start, Integer end) {
		int _start = start == null ? 0 : start;
		int _end = start == null ? this.frames.size() - 1 : end;

		return this.frames.get(Utilities.jumpClamp(this.imgIndex + 1, _start, _end));
	}
	public BufferedImage getPrevFrame(Integer start, Integer end) {
		int _start = start == null ? 0 : start;
		int _end = start == null ? this.frames.size() - 1 : end;

		return this.frames.get(Utilities.jumpClamp(this.getImgIndex() - 1, _start, _end));
	}

	public BufferedImage goToNextFrame(Integer start, Integer end) {
		int _start = start == null ? 0 : start;
		int _end = start == null ? this.frames.size() - 1 : end;

		this.imgIndex = Utilities.jumpClamp(this.imgIndex + 1, _start, _end);
		this.image = this.frames.get(this.imgIndex);
		return this.image;
	}
	public BufferedImage goToPrevFrame(Integer start, Integer end) {
		int _start = start == null ? 0 : start;
		int _end = start == null ? this.frames.size() - 1 : end;

		this.imgIndex = Utilities.jumpClamp(this.imgIndex - 1, _start, _end);
		this.image = this.frames.get(this.imgIndex);
		return this.image;
	}

	public boolean isAtStart() {
		return this.imgIndex == 0;
	}
	public boolean isAtEnd() {
		return this.imgIndex == this.frames.size() - 1;
	}
}
