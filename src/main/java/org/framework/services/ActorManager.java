package org.framework.services;

import org.framework.actor.Actor;
import org.framework.actor.Pawn;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ActorManager {
	private static boolean safeToRemove = true;
	private static final List<String> removeQueue = new ArrayList<>();
	private static final Map<String, Actor> actorsMap = new HashMap<>();


	public static Actor createActor(String id, Class<?> actorClassType) {
		try {
			Constructor<?> constr = actorClassType.getDeclaredConstructor(String.class);
			Actor newActor = (Actor) constr.newInstance(id);
			actorsMap.put(id, newActor);
			return newActor;
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("Class '" + actorClassType.getName() + "' doesn't inherit from Actor"); // workaround for rust-like panic!()
		}
	}

	public static Actor getActor(String id) {
		return actorsMap.get(id);
	}
	public static void removeActor(String id) {
		if (safeToRemove == false) {
			if (id != null)
				removeQueue.add(id);
		} else {
			if (id != null)
				removeQueue.add(id);
			while (removeQueue.isEmpty() == false) {
				actorsMap.remove(removeQueue.getFirst());
				removeQueue.removeFirst();
			}
		}
	}

	public static Set<Map.Entry<String, Actor>> getActorsIter() {
		safeToRemove = false;
		return actorsMap.entrySet();
	}

	public static ArrayList<Actor> getActorsList() {
		safeToRemove = false;
		return actorsMap.entrySet()
				.stream().map(Map.Entry::getValue)
				.sorted(Comparator.comparingDouble(a -> a.getTransform().getLocation().y))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static void safe() {
		safeToRemove = true;
		removeActor(null);
	}
}
