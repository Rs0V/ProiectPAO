package org.framework.level;

import lombok.Getter;
import lombok.Setter;
import org.framework.services.ChartEditor;
import org.framework.sound.components.CSound;

@Getter @Setter
public class Level {
	protected final String id;
	protected long score = 0;

	protected double notesSpeed = 300;
	protected String map;
	protected CSound song;


	public Level(String id, double notesSpeed, String songPath, String map) {
		this.id = id;
		this.notesSpeed = notesSpeed;
		this.song = new CSound(songPath, -8);
		this.map = map;
	}

	public void load() {
		ChartEditor.setNotesSpeed(this.notesSpeed);
		ChartEditor.createChart(this.map);
		this.song.play();
	}
}
