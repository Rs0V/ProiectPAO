package org.framework;

import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.services.ActorManager;
import org.framework.services.GameProperties;
import org.framework.services.InputMapper;
import org.framework.services.MapGenerator;
import org.framework.services.UIManager;
import org.framework.services.enums.RenderHints;
import org.framework.services.enums.UIPositions;
import org.framework.sound.CSound;
import org.framework.sprite.AnimatedSprite;
import org.framework.sprite.Sprite;
import org.framework.sprite.enums.PlaybackType;
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
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class Game extends JFrame implements Runnable {
    private boolean running = false;

    private long last = System.nanoTime();
	private long now;
	private double time;
	double deltaTime;


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


	    MapGenerator.generateMap(0);

		Camera camera = (Camera) ActorManager.createActor("camera-0", Camera.class);
	    assert camera != null : "Couldn't create Camera object";
	    camera
			    .addComponent("click-sound", new CSound("src/main/resources/sounds/click.wav", 4))
			    .addComponent("input", new CCameraInput(camera))
	    ;
		UIManager.setGame(this);
		UIManager.setMainCamera(camera);

		ActorManager.getActor("player-0").setSprite(new AnimatedSprite(
				"src/main/resources/images/Ijee.png",
				new Vec2(32, 32),
				6,
				5)
		).getTransform().setScale(new Vec2(20, 20));

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
			    UIManager.createUIPosition(UIPositions.BottomLeft, UIPositions.Bottom, 85)
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
			    UIManager.createUIPosition(UIPositions.Bottom, UIPositions.BottomRight, 15)
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
	}

    //region start(), stop(), run(), paint() -> (clear screen with BLACK)
    public synchronized void start() {
        running = true;
        new Thread(this).start();
    }

    public synchronized void stop() {
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
		((AnimatedSprite) ActorManager.getActor("player-0").getSprite())
				.getAnimComp()
				.play(PlaybackType.Play, null, null, null, null);
	}

	private final double timeMeas = 1 / 1_000_000_000.0;
    public void update() {
        this.now = System.nanoTime();
		this.time = this.now * this.timeMeas; // Current time in seconds
        this.deltaTime = ((double) (this.now - this.last)) * this.timeMeas * GameProperties.getTimeFactor(); // Delta time in seconds
        this.last = this.now;


		for (var actor : ActorManager.getActorsIter()) {
			actor.getValue().update(this.deltaTime);
		}
		for (var uiElement : UIManager.getUIElementsIter()) {
			uiElement.getValue().update(this.deltaTime);
		}
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


	    ArrayList<Actor> actorsList = ActorManager.getActorsList();
	    for (Actor actor : actorsList) {
			actor.render(g2d, RenderHints.Pixelated, (Camera) ActorManager.getActor("camera-0"), deltaTime);
        }
	    ArrayList<UIElement> uiElementsList = UIManager.getUIElementsList();
	    for (UIElement uiElement : uiElementsList) {
		    uiElement.render(g2d, RenderHints.Smooth, (Camera) ActorManager.getActor("camera-0"), deltaTime);
	    }


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