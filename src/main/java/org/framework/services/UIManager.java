package org.framework.services;

import lombok.Getter;
import lombok.Setter;
import org.framework.Game;
import org.framework.actor.Camera;
import org.framework.services.enums.UIPositions;
import org.framework.ui.UIComponent;
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
	private static final Map<String, UIComponent> uiComponentsMap = new HashMap<>();


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
	 * Used with createUIComponent(...) to better position the UIComponents created.
	 */
	public static Vec2 createUIPosition(UIPositions pos1, UIPositions pos2, Integer percentage) {
		assert pos1 != null : "The first 'uiPosition' argument mustn't be null";
		pos2 = pos2 == null ? pos1 : pos2;
		percentage = percentage == null ? 50 : percentage;
		return Vec2.lerp(uiPosToVec2(pos1), uiPosToVec2(pos2), (double) percentage / 100);
	}

	public static UIComponent createUIComponent(String id, Class<?> uiClassType, Vec2 position) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Constructor<?> constr = uiClassType.getDeclaredConstructor(String.class);
		UIComponent newUIComponent = (UIComponent) constr.newInstance(id);
		newUIComponent.getTransform().setLocation(position);
		uiComponentsMap.put(id, newUIComponent);
		return newUIComponent;
	}

	public static UIComponent getUIComponent(String id) {
		return uiComponentsMap.get(id);
	}

	public static Set<Map.Entry<String, UIComponent>> getUIComponentsIter() {
		return uiComponentsMap.entrySet();
	}

	public static ArrayList<UIComponent> getUIComponentsList() {
		return uiComponentsMap.entrySet()
				.stream().map(Map.Entry::getValue)
				.sorted(Comparator.comparingDouble(a -> -a.getTransform().getDepth()))
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
