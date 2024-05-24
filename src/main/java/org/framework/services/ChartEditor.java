package org.framework.services;

import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.animation.components.CAnimation;
import org.framework.animation.enums.EasingType;
import org.framework.component.IComponent;
import org.framework.services.enums.Arrows;
import org.framework.services.enums.RenderHints;
import org.framework.services.enums.UIPositions;
import org.framework.sprite.Sprite;
import org.framework.ui.UIElement;
import org.framework.ui.components.CRemoveUIElement;
import org.framework.vec2.Vec2;
import org.game.player.objects.Note;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.utilities.Utilities;

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
	private static double noteHitTimeFrame = 200; // total: - and +; in pixels (gets processed into seconds by createChart() later)

	@Getter
	private static final int noteGoodAccuracyPercentage = 85;
	@Getter
	private static final int noteMidAccuracyPercentage = 55;
	@Getter
	private static final int noteBadAccuracyPercentage = 20;

	@Getter
	private static long score = 0;
	@Getter
	private static final int noteGoodAccScore = 10;
	@Getter
	private static final int noteMidAccScore = 5;
	@Getter
	private static final int noteBadAccScore = 2;

	/**
	 * Note: ID, TimeToHit, Direction.
	 */
	private static final List<Triplet<String, Double, Arrows>> notesList = new ArrayList<>();

	@Getter
	private static IComponent CSpawnNotes = new IComponent() {
		@Override
		public void update() {
			if (notesList.isEmpty())
				return;

			var noteTriplet = notesList.getFirst();
			if (TimeManager.getTime() > noteTriplet.getValue1()) {
				Vec2 noteSpawnPos = new Vec2();
				switch (noteTriplet.getValue2()) {
					case Left -> noteSpawnPos.x = UIManager.getUIElement("left-arrow").getTransform().getLocation().x;
					case Right -> noteSpawnPos.x = UIManager.getUIElement("right-arrow").getTransform().getLocation().x;
					case Up -> noteSpawnPos.x = UIManager.getUIElement("up-arrow").getTransform().getLocation().x;
					case Down -> noteSpawnPos.x = UIManager.getUIElement("down-arrow").getTransform().getLocation().x;
				}
				noteSpawnPos.y = notesSpawnY;

				double ht = -1;
				switch (noteTriplet.getValue2()) {
					case Left -> ht = UIManager.getUIElement("left-arrow").getTransform().getLocation().y;
					case Right -> ht = UIManager.getUIElement("right-arrow").getTransform().getLocation().y;
					case Up -> ht = UIManager.getUIElement("up-arrow").getTransform().getLocation().y;
					case Down -> ht = UIManager.getUIElement("down-arrow").getTransform().getLocation().y;
				}

				((Note) ActorManager.createActor(noteTriplet.getValue0(), Note.class))
						.setArrow(noteTriplet.getValue2())
						.setHitTime(noteTriplet.getValue1() + (ht - notesSpawnY) / notesSpeed)
						.setSpeed(notesSpeed)
						.setDespawnY(notesDespawnY)
						.getTransform().setLocation(noteSpawnPos);
				notesList.removeFirst();
			}
		}

		@Override
		public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

		}
	};


	/**
	 * Should be in the form of: "1 -> left 2 -> up 2.5 -> down ..."
	 * Entries should be ordered by the 'time to hit'.
	 */
	public static void createChart(String chartMap) {
		score = 0;
		String[] map = Arrays.stream(chartMap.split("\\s"))
				.filter(x -> x.isBlank() == false)
				.map(x -> x.trim())
				.filter(x -> x.equals("->") == false)
				.toArray(String[]::new);

		assert map.length % 2 == 0 : "Chart map isn't valid";
		for (int i = 0; i < map.length; i += 2) {
			Double hitTime = Double.parseDouble(map[i]);

			Arrows arrow = null;
			switch (map[i+1]) {
				case "left" -> arrow = Arrows.Left;
				case "right" -> arrow = Arrows.Right;
				case "up" -> arrow = Arrows.Up;
				case "down" -> arrow = Arrows.Down;
				default -> {
					assert false : "Chart map 'arrow' isn't valid"; // not very professional but meh... :P
				}
			}

			double ht = -1;
			switch (map[i+1]) {
				case "left" -> ht = UIManager.getUIElement("left-arrow").getTransform().getLocation().y;
				case "right" -> ht = UIManager.getUIElement("right-arrow").getTransform().getLocation().y;
				case "up" -> ht = UIManager.getUIElement("up-arrow").getTransform().getLocation().y;
				case "down" -> ht = UIManager.getUIElement("down-arrow").getTransform().getLocation().y;
			}
			ht = hitTime - (ht - notesSpawnY) / notesSpeed;

			notesList.add(Triplet.with(String.format("note-%s-%d", map[i+1], i / 2), ht, arrow));
		}
		noteHitTimeFrame = noteHitTimeFrame / notesSpeed / 2;
		System.out.printf("NoteHitTimeFrame set to: %f%n%n", noteHitTimeFrame);
	}

	public static void checkNoteHit(Arrows pressedArrow) {
		String dir = switch (pressedArrow) {
			case Left -> "left";
			case Right -> "right";
			case Up -> "up";
			case Down -> "down";
		};
		Note note = null;
		try {
			note = (Note) ActorManager.getActorsList().stream()
					.filter(x -> x.getId().contains("note-" + dir))
					.sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getId().substring(a.getId().lastIndexOf('-') + 1))))
					.collect(Collectors.toCollection(ArrayList::new)).getFirst();
		} catch (Exception e) {
//			e.printStackTrace();
			return;
		}

		if (
				note.getArrow() == pressedArrow
				&& note.getHitTime() - noteHitTimeFrame < TimeManager.getTime()
				&& TimeManager.getTime() < note.getHitTime() + noteHitTimeFrame
		) {
			System.out.printf("%nHit note with ID: '%s'", note.getId());
			ActorManager.removeActor(note.getId());

			String image = null;
			int accuracy = (int) (Utilities.lext(note.getHitTime() - noteHitTimeFrame, note.getHitTime() + noteHitTimeFrame, TimeManager.getTime()) * 100);
			if (accuracy < 50) {
				accuracy = (int) (accuracy * 1.0 / 50 * 100);
			} else {
				accuracy = (int) ((100 - accuracy) * 1.0 / 50 * 100);
			}

			if (accuracy > noteGoodAccuracyPercentage) {
				image = "src/main/resources/images/goodNoteHit.png";
				score += noteGoodAccScore;
			} else if (accuracy > noteMidAccuracyPercentage) {
				image = "src/main/resources/images/midNoteHit.png";
				score += noteMidAccScore;
			} else if (accuracy > noteBadAccuracyPercentage) {
				image = "src/main/resources/images/badNoteHit.png";
				score += noteBadAccScore;
			} else {
				image = "src/main/resources/images/bruhNoteHit.png";
			}
			System.out.printf("%nAccuracy: %d%%", accuracy);
			System.out.printf("         NoteTime: %f", note.getHitTime());
			System.out.printf("         Time: %f%n%n", TimeManager.getTime());

			Vec2 rndOffset = new Vec2(Math.random() * 50, Math.random() * 50);
			var scoreElem = UIManager.createUIElement(
					"accuracy-" + Utilities.generateId(),
					UIElement.class,
					UIManager.createUIPosition(UIPositions.Center, UIPositions.TopRight, 70)
			).setSprite(new Sprite(
					image,
					null,
					new Vec2(0.3, 0.3)
			));
			scoreElem.getTransform().moveGlobal(rndOffset);
			scoreElem.addComponent("fade-in", new CAnimation<>(
					scoreElem.getTransform(),
					"location",
					EasingType.EaseOut,
					scoreElem.getTransform().moveGlobal(new Vec2(0, 15)).getLocation(),
					scoreElem.getTransform().moveGlobal(new Vec2(0, -15)).getLocation(),
					2.0
			))
					.addComponent("remove-self", new CRemoveUIElement(scoreElem.getId(), 1.0))
					.addComponent("depth-first-top", new IComponent() {
						private static long depth = 0;
						private boolean once = false;

						@Override
						public void update() {
//							scoreElem.getTransform().setDepth(-scoreElem.getTransform().getLocation().y);
							if (once == false) {
								scoreElem.getTransform().setDepth(depth);
								depth++;
								once = true;
							}
						}

						@Override
						public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

						}
					});
			((CAnimation<Vec2>) scoreElem.getComponents().get("fade-in")).play(1);
			((CRemoveUIElement) scoreElem.getComponents().get("remove-self")).activate();
		}
		else {
			System.out.printf("%nNeed: " + note.getArrow().toString() + "    Pressed: " + pressedArrow.toString() + "%n");
			System.out.printf("%f < %f < %f%n%n", note.getHitTime() - noteHitTimeFrame, TimeManager.getTime(), note.getHitTime() + noteHitTimeFrame);
		}
	}
}
