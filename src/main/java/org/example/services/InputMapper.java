package org.example.services;

import org.example.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class InputMapper {
	private static final Map<String, Boolean> inputMap = new HashMap<>();


	public static void createAction(Game game, String actionName, String key){
		Action actionPressed = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				inputMap.put(actionName, true);
			}
		};
		Action actionReleased = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				inputMap.put(actionName, false);
			}
		};
		KeyStroke pressedKeyStroke = KeyStroke.getKeyStroke(key);
		KeyStroke releasedKeyStroke = KeyStroke.getKeyStroke("released " + key);

		game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, actionName + " pressed");
		game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, actionName + " released");

		game.getRootPane().getActionMap().put(actionName + " pressed", actionPressed);
		game.getRootPane().getActionMap().put(actionName + " released", actionReleased);
	}

	public static boolean checkAction(String actionName) {
		Boolean value = inputMap.get(actionName);
		return value == null ? false : value;
	}
}
