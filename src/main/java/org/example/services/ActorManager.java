package org.example.services;

import org.example.actor.Actor;
import org.example.actor.Pawn;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ActorManager {
	private static final Map<String, Actor> actorsMap = new HashMap<>();


	public static Actor createActor(String id, boolean isPawn) {
		Actor newActor = isPawn
				? new Pawn(id)
				: new Actor(id);
		actorsMap.put(id, newActor);
		return newActor;
	}

	public static Actor getActor(String id) {
		return actorsMap.get(id);
	}

	public static Set<Map.Entry<String, Actor>> getActorsIter() {
		return actorsMap.entrySet();
	}

	public static ArrayList<Actor> getActorsList() {
		return actorsMap.entrySet()
				.stream().map(Map.Entry::getValue)
				.sorted(Comparator.comparingDouble(a -> a.getTransform().getLocation().y))
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
