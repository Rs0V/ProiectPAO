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
import java.util.*;


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
	    Level level1 = new Level("level-1", 500, "src/main/resources/music/Level-1.wav", """
			    1.828422 -> up
			    2.191096 -> down
			    2.410786 -> down
			    2.814652 -> left
			    3.117865 -> right
			    3.438636 -> up
			    3.908544 -> right
			    4.132104 -> left
			    4.520344 -> down
			    4.865354 -> up
			    5.224652 -> right
			    5.615953 -> left
			    5.774043 -> down
			    5.900957 -> left
			    6.011427 -> up
			    6.314710 -> right
			    6.611514 -> down
			    6.917157 -> down
			    7.511195 -> right
			    7.975861 -> right
			    8.314427 -> left
			    8.655570 -> up
			    9.050713 -> right
			    9.254587 -> left
			    9.697689 -> down
			    10.025392 -> up
			    10.346916 -> right
			    10.747257 -> down
			    10.971813 -> left
			    11.389712 -> down
			    11.725099 -> down
			    12.068074 -> right
			    12.450318 -> left
			    12.675765 -> right
			    12.791798 -> left
			    12.899212 -> up
			    13.137185 -> left
			    13.408972 -> right
			    13.859036 -> down
			    14.209660 -> down
			    14.446200 -> left
			    14.639203 -> right
			    14.889839 -> up
			    15.094035 -> down
			    15.288137 -> left
			    15.494206 -> right
			    15.901436 -> down
			    16.131397 -> up
			    16.345624 -> left
			    16.531890 -> right
			    16.779714 -> left
			    17.017485 -> down
			    17.223690 -> up
			    17.641503 -> right
			    17.890724 -> down
			    18.077123 -> left
			    18.277139 -> up
			    18.490948 -> left
			    18.677959 -> down
			    18.900129 -> up
			    19.352257 -> down
			    19.532736 -> up
			    19.746446 -> down
			    19.928434 -> up
			    20.188626 -> right
			    20.429843 -> left
			    20.667168 -> right
			    20.847008 -> left
			    21.062272 -> down
			    21.279650 -> up
			    21.498636 -> up
			    21.702599 -> up
			    21.826321 -> down
			    21.948044 -> left
			    22.147811 -> right
			    22.335740 -> left
			    22.566110 -> down
			    22.767034 -> up
			    22.954570 -> left
			    23.172753 -> right
			    23.412480 -> up
			    23.626091 -> down
			    23.869292 -> up
			    24.074210 -> down
			    24.270331 -> left
			    24.517912 -> right
			    24.700110 -> left
			    24.925895 -> down
			    25.180079 -> up
			    25.258076 -> down
			    25.445929 -> up
			    25.520496 -> right
			    25.863699 -> right
			    26.059733 -> right
			    26.251797 -> right
			    26.451294 -> right
			    26.647476 -> left
			    26.774551 -> down
			    26.871602 -> left
			    26.968162 -> up
			    27.102357 -> left
			    27.204197 -> down
			    27.526595 -> right
			    27.707133 -> right
			    27.926297 -> down
			    28.116344 -> left
			    28.332297 -> down
			    28.539287 -> up
			    28.637333 -> down
			    28.726988 -> up
			    28.968457 -> right
			    29.162480 -> left
			    29.358526 -> down
			    29.563955 -> left
			    29.812425 -> right
			    30.041998 -> left
			    30.278963 -> down
			    30.467331 -> up
			    30.703191 -> down
			    30.920784 -> left
			    31.164150 -> right
			    31.371865 -> up
			    31.551329 -> down
			    31.769624 -> left
			    31.989095 -> right
			    32.074500 -> left
			    32.165610 -> right
			    32.401637 -> left
			    32.681333 -> down
			    32.868841 -> up
			    33.080662 -> left
			    33.263032 -> down
			    33.480461 -> left
			    33.576753 -> right
			    33.736038 -> up
			    33.846595 -> down
			    33.940906 -> left
			    34.088989 -> right
			    34.159021 -> left
			    """);
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