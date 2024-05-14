package org.framework.services;

import org.framework.actor.Actor;
import org.framework.actor.Pawn;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ActorManager {
	private static final Map<String, Actor> actorsMap = new HashMap<>();


	public static Actor createActor(String id, Class<?> actorClassType) {
		try {
			Constructor<?> constr = actorClassType.getDeclaredConstructor(String.class);
			Actor newActor = (Actor) constr.newInstance(id);
			actorsMap.put(id, newActor);
			return newActor;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
