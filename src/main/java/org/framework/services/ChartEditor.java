package org.framework.services;

import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.component.IComponent;
import org.framework.services.enums.Arrows;
import org.framework.services.enums.RenderHints;
import org.framework.sprite.Sprite;
import org.framework.ui.UIElement;
import org.framework.vec2.Vec2;
import org.game.player.objects.Note;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ChartEditor {
	@Getter @Setter
	private static double notesSpawnY = -100;
	@Getter @Setter
	private static double notesDespawnY;
	@Getter @Setter
	private static double notesSpeed = 300;
	@Getter @Setter
	private static Double noteHitTimeFrame = null; // in seconds

	/**
	 * Note: ID, TimeToHit, Direction.
	 */
	private static final List<Triplet<String, Double, Arrows>> notesList = new ArrayList<>();

	@Getter @Setter
	private static double startTime = 0;

	@Getter
	private static IComponent CSpawnNotes = new IComponent() {
		private double time = 0;


		@Override
		public void update(double deltaTime) {
			if (notesList.isEmpty())
				return;

			var noteTriplet = notesList.getFirst();
			if (time - startTime > noteTriplet.getValue1()) {
				// Value Switch will permanently borrow the Object Reference.
				// Results into dangling reference that will eventually get garbage collected.
//				Vec2 noteSpawnPos = switch (noteTriplet.getValue2()) {
//					case Left -> UIManager.getUIElement("left-arrow").getTransform().getLocation();
//					case Right -> UIManager.getUIElement("right-arrow").getTransform().getLocation();
//					case Up -> UIManager.getUIElement("up-arrow").getTransform().getLocation();
//					case Down -> UIManager.getUIElement("down-arrow").getTransform().getLocation();
//				};
				Vec2 noteSpawnPos = new Vec2();
				switch (noteTriplet.getValue2()) {
					case Left -> noteSpawnPos.x = UIManager.getUIElement("left-arrow").getTransform().getLocation().x;
					case Right -> noteSpawnPos.x = UIManager.getUIElement("right-arrow").getTransform().getLocation().x;
					case Up -> noteSpawnPos.x = UIManager.getUIElement("up-arrow").getTransform().getLocation().x;
					case Down -> noteSpawnPos.x = UIManager.getUIElement("down-arrow").getTransform().getLocation().x;
				};
				noteSpawnPos.y = notesSpawnY;

				((Note) ActorManager.createActor(noteTriplet.getValue0(), Note.class))
						.setArrow(noteTriplet.getValue2())
						.setTime(startTime + noteTriplet.getValue1() + (notesDespawnY - notesSpawnY) / notesSpeed)
						.setSpeed(notesSpeed)
						.setDespawnY(notesDespawnY)
						.getTransform().setLocation(noteSpawnPos);
				notesList.removeFirst();
			}
			time += deltaTime;
		}

		@Override
		public void render(Graphics2D g2d, RenderHints renderHints, Camera camera, double deltaTime) {

		}
	};


	/**
	 * Should be in the form of: "1 -> left 2 -> up 2.5 -> down ..."
	 * Entries should be ordered by the 'time to hit'.
	 */
	public static void createChart(String chartMap) {
		String[] map = Arrays.stream(chartMap.split("\\s"))
				.filter(x -> x.isBlank() == false)
				.map(x -> x.trim())
				.filter(x -> x.equals("->") == false)
				.toArray(String[]::new);

		assert map.length % 2 == 0 : "Chart map isn't valid";
		for (int i = 0; i < map.length; i += 2) {
			Double time = Double.parseDouble(map[i]);
			Arrows arrow = switch (map[i+1]) {
				case "left" -> Arrows.Left;
				case "right" -> Arrows.Right;
				case "up" -> Arrows.Up;
				case "down" -> Arrows.Down;
				default -> {
					assert 1 == 2 : "Chart map 'arrow' isn't valid"; // not very professional but meh... :P
					yield Arrows.Left;
				}
			};
			notesList.add(Triplet.with(String.format("note-%d", i / 2), time - (notesDespawnY - notesSpawnY) / notesSpeed, arrow));
		}

		if (noteHitTimeFrame == null) {
			noteHitTimeFrame = 150 / notesSpeed * 2;
			System.out.printf("NoteHitTimeFrame set to: %f%n%n", noteHitTimeFrame);
		}
	}

	public static void checkNoteHit(Arrows pressedArrow, double time) {
		Note note = null;
		try {
			note = (Note) ActorManager.getActorsList().stream()
					.filter(x -> x.getId().contains("note"))
					.sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getId().substring(5))))
					.collect(Collectors.toCollection(ArrayList::new)).getFirst();
		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}

		if (note.getArrow() == pressedArrow && note.getTime() - noteHitTimeFrame < time && time < note.getTime() + noteHitTimeFrame) {
			System.out.printf("%nHit note with ID: '" + note.getId() + "'%n%n");
			ActorManager.removeActor(note.getId());
		}
		else {
			System.out.printf("%nNeed: " + note.getArrow().toString() + "    Pressed: " + pressedArrow.toString() + "%n");
			System.out.printf("%f < %f < %f%n%n", note.getTime() - noteHitTimeFrame, time, note.getTime() + noteHitTimeFrame);
		}
	}
}
