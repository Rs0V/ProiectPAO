package org.framework.sound.components;

import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.enums.RenderHints;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.io.File;

public class CSound implements IComponent {
	@Getter
	protected File sound;
	@Getter @Setter
	protected double volume;
	protected Clip clip = null;


	public CSound(String soundFilePath, double volume) {
		this.sound = new File(soundFilePath);
		assert volume > 0 && volume < 6 : "Volume has to be in the [0, 6] interval";
		this.volume = volume;

		try {
			var audioInputStream = AudioSystem.getAudioInputStream(this.sound);

			this.clip = AudioSystem.getClip();
			clip.open(audioInputStream);

			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue((float) this.volume);

			audioInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CSound setSound(String soundFilePath) {
		this.sound = new File(soundFilePath);
		try {
			var audioInputStream = AudioSystem.getAudioInputStream(this.sound);

			if (this.clip != null)
				this.clip.close();
			this.clip = AudioSystem.getClip();
			clip.open(audioInputStream);

			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue((float) this.volume);

			audioInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public void play() {
		if (clip.isActive() == false) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	@Override
	public void update() {

	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

	}
}
