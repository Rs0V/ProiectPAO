package org.framework.services;

import org.framework.actor.Actor;
import org.framework.sprite.Sprite;
import org.framework.vec2.Vec2;
import org.game.player.objects.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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


	public static Set<Actor> generateMap(int mapNumber) {
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
						assert actor != null : "Couldn't create Actor object";
						actor.setSprite(new Sprite(
								"src/main/resources/images/tempPlayer.png",
								null,
								new Vec2(2, 2)
						));
						break;
					case '#':
						actor = ActorManager.createActor(String.format("block-%d", blockNumber), Actor.class);
						assert actor != null : "Couldn't create Actor object";
						actor.setSprite(new Sprite(
								"src/main/resources/images/square.png",
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
