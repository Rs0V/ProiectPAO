package org.example.sprite;

import lombok.Getter;
import lombok.Setter;
import org.example.vec2.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
	@Getter @Setter
	private BufferedImage image;
	@Getter @Setter
	private Vec2 origin;
	@Getter @Setter
	private Vec2 scale;

	public Sprite(String imagePath, Vec2 origin, Vec2 scale) {
		if (imagePath == null || imagePath.trim().isEmpty()) {
			try {
				this.image = ImageIO.read(new File("src/main/resources/square.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				this.image = ImageIO.read(new File(imagePath.trim()));;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.origin = origin == null ? new Vec2(0.5, 0.5) : origin;
		this.scale = scale == null ? new Vec2(0.01, 0.01) : scale;
	}
}
