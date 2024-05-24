package org.framework;

import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.animation.components.CAnimation;
import org.framework.animation.enums.EasingType;
import org.framework.component.IComponent;
import org.framework.level.Level;
import org.framework.services.*;
import org.framework.services.UIManager;
import org.framework.services.enums.RenderHints;
import org.framework.services.enums.UIPositions;
import org.framework.sound.components.CSound;
import org.framework.sprite.Sprite;
import org.framework.ui.UIElement;
import org.framework.vec2.Vec2;
import org.game.player.components.CCameraInput;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class Game extends JFrame implements Runnable {
    private boolean running = false;


    public Game() {
        setSize((int)GameProperties.getScreenRes().x, (int)GameProperties.getScreenRes().y);
        setTitle("Game Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });


		TimeManager.init();

		Camera camera = (Camera) ActorManager.createActor("camera-0", Camera.class);
	    assert camera != null : "Couldn't create Camera object";
	    camera
			    .addComponent("left-click", new CSound("src/main/resources/sounds/click.wav", 4))
			    .addComponent("right-click", new CSound("src/main/resources/sounds/click.wav", 4))
			    .addComponent("up-click", new CSound("src/main/resources/sounds/click.wav", 4))
			    .addComponent("down-click", new CSound("src/main/resources/sounds/click.wav", 4))
			    .addComponent("input", new CCameraInput(camera))
	    ;
		UIManager.setGame(this);
		UIManager.setMainCamera(camera);


	    var leftArrow = UIManager.createUIElement(
				"left-arrow",
			    UIElement.class,
			    UIManager.createUIPosition(UIPositions.BottomLeft, UIPositions.Bottom, 55)
	    );
	    assert leftArrow != null : "Couldn't create UIElement";
	    leftArrow.setSprite(new Sprite(
				"src/main/resources/images/left_arrow.png",
				null,
				new Vec2(.3, .3)
		)).getTransform().setRotation(-90.0).moveGlobal(new Vec2(0.0, -80));


	    var upArrow = UIManager.createUIElement(
				"up-arrow",
			    UIElement.class,
			    UIManager.createUIPosition(UIPositions.Bottom, UIPositions.BottomRight, 15)
	    );
	    assert upArrow != null : "Couldn't create UIElement";
	    upArrow.setSprite(new Sprite(
			    "src/main/resources/images/up_arrow.png",
			    null,
			    new Vec2(.3, .3)
	    )).getTransform().setRotation(0.0).moveGlobal(new Vec2(0.0, -80));


	    var downArrow = UIManager.createUIElement(
				"down-arrow",
			    UIElement.class,
			    UIManager.createUIPosition(UIPositions.BottomLeft, UIPositions.Bottom, 85)
	    );
	    assert downArrow != null : "Couldn't create UIElement";
	    downArrow.setSprite(new Sprite(
			    "src/main/resources/images/down_arrow.png",
			    null,
			    new Vec2(.3, .3)
	    )).getTransform().setRotation(-180.0).moveGlobal(new Vec2(0.0, -80));


	    var rightArrow = UIManager.createUIElement(
				"right-arrow",
			    UIElement.class,
			    UIManager.createUIPosition(UIPositions.Bottom, UIPositions.BottomRight, 45)
	    );
	    assert rightArrow != null : "Couldn't create UIElement";
	    rightArrow.setSprite(new Sprite(
			    "src/main/resources/images/right_arrow.png",
			    null,
			    new Vec2(.3, .3)
	    )).getTransform().setRotation(90.0).moveGlobal(new Vec2(0.0, -80));


		ChartEditor.setNotesDespawnY(camera.getScreenSize().y + 100);

		//region Create level-1 ...
	    String levelMap = null;
	    try {
		    levelMap = String.join("\n", Files.readAllLines(Paths.get("src/main/java/org/framework/level/levels/level-1.txt"), StandardCharsets.UTF_8));
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	    Level level1 = new Level(
				"level-1",
			    700,
			    "src/main/resources/music/Level-1.wav",
			    levelMap);
	    //endregion
		level1.load();

		camera.addComponent("beat-zoom", new CAnimation(
				camera,
				"zoom",
				EasingType.EaseOut,
				camera.getZoom(),
				camera.getZoom() * 1.02,
				0.43
				));
	    ((CAnimation) camera.getComponents().get("beat-zoom")).play(-1);

	    leftArrow.setAffectedByCamera(true);
	    rightArrow.setAffectedByCamera(true);
	    upArrow.setAffectedByCamera(true);
	    downArrow.setAffectedByCamera(true);

		ActorManager.createActor("background", Actor.class)
				.setSprite(new Sprite(
						"src/main/resources/images/posterized(pp).png",
						null,
						new Vec2(.7, .7)
				)).setCollider(null)
				.getTransform()
				.setLocation(UIManager.createUIPosition(UIPositions.Center, null, null))
				.setDepth(-1000);
	}

    //region start(), stop(), run(), paint() -> (clear screen with BLACK)
    public synchronized void start() {
        running = true;
        new Thread(this).start();
    }

    public synchronized void stop() {
		_stop();
        running = false;
    }

    public void run() {
		_start();
        while (running) {
            update();
            render();
            try {
                Thread.sleep(1000 / GameProperties.getFps()); // Cap the frame rate to 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK); // Set the color to black
        g.fillRect(0, 0, getWidth(), getHeight()); // Fill the entire window with black
    }
    //endregion

	public void _start() {
		TimeManager.start();
		Recorder.watch("presses", "src/main/java/org/framework/services/records/presses.txt", true);
	}

	public void _stop() {
		System.out.printf("%n%nScore: %dpts%n", ChartEditor.getScore());
	}

    public void update() {
		TimeManager.update();

		ChartEditor.getCSpawnNotes().update();

		for (var actor : ActorManager.getActorsIter()) {
			actor.getValue().update();
		}
		ActorManager.safe();

		for (var uiElement : UIManager.getUIElementsIter()) {
			uiElement.getValue().update();
		}
		UIManager.safe();
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        paint(g);

        Graphics2D g2d = (Graphics2D) g;


//	    ChartEditor.getCSpawnNotes().render(g2d, RenderHints.Smooth, (Camera) ActorManager.getActor("camera-0"));

	    ArrayList<Actor> actorsList = ActorManager.getActorsList();
		actorsList.sort(Comparator.comparingDouble(a -> a.getTransform().getDepth()));
	    for (Actor actor : actorsList) {
			actor.render(g2d, RenderHints.Pixelated, (Camera) ActorManager.getActor("camera-0"));
        }
		ActorManager.safe();

	    ArrayList<UIElement> uiElementsList = UIManager.getUIElementsList();
	    uiElementsList.sort(Comparator.comparingDouble(a -> a.getTransform().getDepth()));
	    for (UIElement uiElement : uiElementsList) {
		    uiElement.render(g2d, RenderHints.Smooth, (Camera) ActorManager.getActor("camera-0"));
	    }
		UIManager.safe();


        g2d.dispose();
        bs.show();
    }



    public static void main(String[] args) {
        Game game = new Game();
        // Initialize JFrame settings...
        game.setVisible(true);


	    InputMapper.createAction(game, "left-move", "A");
	    InputMapper.createAction(game, "right-move", "D");
	    InputMapper.createAction(game, "up-move", "W");
	    InputMapper.createAction(game, "down-move", "S");

	    InputMapper.createAction(game, "left-arrow", "LEFT");
	    InputMapper.createAction(game, "right-arrow", "RIGHT");
	    InputMapper.createAction(game, "up-arrow", "UP");
	    InputMapper.createAction(game, "down-arrow", "DOWN");

	    InputMapper.createAction(game, "zoom-in", "I");
	    InputMapper.createAction(game, "zoom-out", "O");


	    InputMapper.createAction(game, "left-lean", "Q");
	    InputMapper.createAction(game, "right-lean", "E");


        game.start();
    }
}