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
	private static Double noteHitTimeFrame = null; // in seconds

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
				};
				noteSpawnPos.y = notesSpawnY;

				((Note) ActorManager.createActor(noteTriplet.getValue0(), Note.class))
						.setArrow(noteTriplet.getValue2())
						.setHitTime(noteTriplet.getValue1() + (notesDespawnY - notesSpawnY) / notesSpeed)
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
		String[] map = Arrays.stream(chartMap.split("\\s"))
				.filter(x -> x.isBlank() == false)
				.map(x -> x.trim())
				.filter(x -> x.equals("->") == false)
				.toArray(String[]::new);

		assert map.length % 2 == 0 : "Chart map isn't valid";
		for (int i = 0; i < map.length; i += 2) {
			Double hitTime = Double.parseDouble(map[i]);
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
			notesList.add(Triplet.with(String.format("note-%s-%d", map[i+1], i / 2), hitTime - (notesDespawnY - notesSpawnY) / notesSpeed, arrow));
		}

		if (noteHitTimeFrame == null) {
			noteHitTimeFrame = 150 / notesSpeed * 2;
			System.out.printf("NoteHitTimeFrame set to: %f%n%n", noteHitTimeFrame);
		}
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
			System.out.printf("%nHit note with ID: '" + note.getId() + "'%n%n");
			ActorManager.removeActor(note.getId());

			Vec2 rndOffset = new Vec2(Math.random() * 50, Math.random() * 50);
			var scoreElem = UIManager.createUIElement(
					Utilities.generateId(),
					UIElement.class,
					UIManager.createUIPosition(UIPositions.Center, UIPositions.TopRight, 70)
			).setSprite(new Sprite(
					"src/main/resources/images/goodNoteHit.png",
					null,
					new Vec2(0.3, 0.3)
			));
			scoreElem.getTransform().moveGlobal(rndOffset);
			scoreElem.addComponent("fade-in", new CAnimation<Vec2>(
					scoreElem.getTransform(),
					"location",
					EasingType.EaseInOut,
					scoreElem.getTransform().moveGlobal(new Vec2(0, 50)).getLocation(),
					scoreElem.getTransform().moveGlobal(new Vec2(0, -50)).getLocation()
			)).addComponent("remove-self", new CRemoveUIElement(scoreElem.getId(), 2));
			((CAnimation<Vec2>) scoreElem.getComponents().get("fade-in")).play(1);
			((CRemoveUIElement) scoreElem.getComponents().get("remove-self")).activate();
		}
		else {
			System.out.printf("%nNeed: " + note.getArrow().toString() + "    Pressed: " + pressedArrow.toString() + "%n");
			System.out.printf("%f < %f < %f%n%n", note.getHitTime() - noteHitTimeFrame, TimeManager.getTime(), note.getHitTime() + noteHitTimeFrame);
		}
	}
}
