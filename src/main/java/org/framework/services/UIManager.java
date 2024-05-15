package org.framework.services;

import lombok.Getter;
import lombok.Setter;
import org.framework.Game;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.services.enums.UIPositions;
import org.framework.ui.UIElement;
import org.framework.vec2.Vec2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class UIManager {
	@Getter @Setter
	private static Game game;
	@Getter @Setter
	private static Camera mainCamera;
	private static boolean safeToRemove = true;
	private static final List<String> removeQueue = new ArrayList<>();
	private static final Map<String, UIElement> uiElementsMap = new HashMap<>();


	private static Vec2 uiPosToVec2(UIPositions position) {
		return switch (position) {
			case Bottom -> new Vec2(mainCamera.getScreenSize().x / 2, mainCamera.getScreenSize().y);
			case Top -> new Vec2(mainCamera.getScreenSize().x / 2, 0.0 + game.getInsets().top);
			case Left -> new Vec2(0.0, mainCamera.getScreenSize().y / 2 + (double) game.getInsets().top / 2);
			case Right -> new Vec2(mainCamera.getScreenSize().x, mainCamera.getScreenSize().y / 2 + (double) game.getInsets().top / 2);
			case BottomLeft -> new Vec2(0.0, mainCamera.getScreenSize().y);
			case BottomRight -> new Vec2(mainCamera.getScreenSize().x, mainCamera.getScreenSize().y);
			case TopLeft -> new Vec2(0.0, 0.0 + game.getInsets().top);
			case TopRight -> new Vec2(mainCamera.getScreenSize().x, 0.0 + game.getInsets().top);
			case Center -> new Vec2(mainCamera.getScreenSize().x / 2, mainCamera.getScreenSize().y / 2 + (double) game.getInsets().top / 2);
		};
	}

	/**
	 * Used with createUIElement(...) to better position the UIElements created.
	 */
	public static Vec2 createUIPosition(UIPositions pos1, UIPositions pos2, Integer percentage) {
		assert pos1 != null : "The first 'uiPosition' argument mustn't be null";
		pos2 = pos2 == null ? pos1 : pos2;
		percentage = percentage == null ? 50 : percentage;
		return Vec2.lerp(uiPosToVec2(pos1), uiPosToVec2(pos2), (double) percentage / 100);
	}

	public static UIElement createUIElement(String id, Class<?> uiClassType, Vec2 position) {
		try {
			Constructor<?> constr = uiClassType.getDeclaredConstructor(String.class);
			UIElement newUIElement = (UIElement) constr.newInstance(id);
			newUIElement.getTransform().setLocation(position);
			uiElementsMap.put(id, newUIElement);
			return newUIElement;
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("Class '" + uiClassType.getName() + "' doesn't inherit from UIElement"); // workaround for rust-like panic!()
		}
	}

	public static UIElement getUIElement(String id) {
		return uiElementsMap.get(id);
	}
	public static void removeUIElement(String id) {
		if (safeToRemove == false) {
			if (id != null)
				removeQueue.add(id);
		} else {
			if (id != null)
				removeQueue.add(id);
			while (removeQueue.isEmpty() == false) {
				uiElementsMap.remove(removeQueue.getFirst());
				removeQueue.removeFirst();
			}
		}
	}

	public static Set<Map.Entry<String, UIElement>> getUIElementsIter() {
		safeToRemove = false;
		return uiElementsMap.entrySet();
	}

	public static ArrayList<UIElement> getUIElementsList() {
		safeToRemove = false;
		return uiElementsMap.entrySet()
				.stream().map(Map.Entry::getValue)
				.sorted(Comparator.comparingDouble(a -> -a.getTransform().getDepth()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static void safe() {
		safeToRemove = true;
		removeUIElement(null);
	}
}
