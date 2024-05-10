package org.framework.services;

import jdk.jfr.Unsigned;
import org.framework.actor.Actor;
import org.framework.sprite.Sprite;
import org.framework.vec2.Vec2;
import org.game.player.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MapGenerator {
	private static final int mapLineToPixels = 100;
	private static final String[] maps = {
   """
	.....................
	....###...........##.
	#............##......
	.....................
	..##...###...........
	...............###...
	..........@..........
	........#####........
	.....................
	""",
	};


	public static Set<Actor> generateMap(int mapNumber) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Set<Actor> actors = new HashSet<>();
		String[] map = Arrays.stream(maps[mapNumber].split("\\s"))
				.filter(x -> x.isBlank() == false)
				.map(x -> x.trim())
				.toArray(String[]::new);

		int blockNumber = 0;
		for (int iline = 0; iline < map.length; iline++) {
			var line = map[iline];

			for (int ich = 0; ich < line.length(); ich++) {
				var ch = line.charAt(ich);

				Actor actor;
				switch (ch) {
					case '@':
						actor = ActorManager.createActor("player-0", Player.class);
						actor.setSprite(new Sprite(
								"src/main/resources/tempPlayer.png",
								null,
								new Vec2(2, 2)
						));
						break;
					case '#':
						actor = ActorManager.createActor(String.format("block-%d", blockNumber), Actor.class);
						actor.setSprite(new Sprite(
								"src/main/resources/square.png",
								null,
								new Vec2(.05, .05)
						));
						blockNumber++;
						break;
					default:
						continue;
				}
				actor.getTransform().setLocation(new Vec2(
						(double) ich / line.length() * (GameProperties.getScreenRes().x - 100) + 100,
						(GameProperties.getScreenRes().y - 100) - (double) (map.length - iline - 1) * mapLineToPixels
				));

				actors.add(actor);
			}
		}
		return actors;
	}
}
