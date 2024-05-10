package org.framework;

/*
 * Sistem de joc video
 * -------------------
 *
 * Obiecte: Vec2, Transform
 *          Shape, Rectangle, Circle, Collider,
 *          Actor, Pawn
 *          PickUp, Button, Switch, Spawner
 *          Level, Menu
 *          UI, UIElement, UIStat, UIButton
 *
 * Actiuni: Move, Rotate, Scale,
 *          Raycast, Overlap, Collide,
 *          Follow, Jump, Fall,
 *          ChangeStat, ChangeState, Spawn,
 *          Play, Quit, etc.
 * */


import jdk.jfr.Unsigned;
import org.framework.actor.Actor;
import org.framework.services.ActorManager;
import org.framework.services.GameProperties;
import org.framework.services.InputMapper;
import org.framework.services.MapGenerator;
import org.framework.sprite.Sprite;
import org.framework.vec2.Vec2;
import org.game.player.Player;


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


    public Game() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Initialize JFrame settings...
        setSize((int)GameProperties.getScreenRes().x, (int)GameProperties.getScreenRes().y); // Set the size of the window
        setTitle("Game Window"); // Set the title of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        setLocationRelativeTo(null); // Center the window
        setResizable(false); // Make the window not resizable
        setVisible(true); // Make the window visible
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });

		/*
		int noTrees = 10;
	    for (int i = 0; i < noTrees; i++) {
		    Actor tree = ActorManager.createActor(String.format("tree-%d", i), Actor.class);
			tree.getTransform().setLocation(new Vec2(
					Math.clamp(Math.random() * GameProperties.getScreenRes().x, 100, GameProperties.getScreenRes().x - 100),
					Math.clamp(Math.random() * GameProperties.getScreenRes().y, 100, GameProperties.getScreenRes().y - 100)
			));
			tree.setSprite(new Sprite(
					"src/main/resources/treeus.png",
					null,
					new Vec2(5, 5)
			));
	    }
	    {
		    Actor player = ActorManager.createActor("player-0", Player.class);
		    player.getTransform().setLocation(new Vec2(
				    Math.clamp(Math.random() * GameProperties.getScreenRes().x, 100, GameProperties.getScreenRes().x - 100),
				    Math.clamp(Math.random() * GameProperties.getScreenRes().y, 100, GameProperties.getScreenRes().y - 100)
		    ));
		    player.setSprite(new Sprite(
				    "src/main/resources/tempPlayer.png",
				    null,
				    new Vec2(2, 2)
		    ));
	    }
	    */

	    MapGenerator.generateMap(0);
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

	private final double timeMeas = 1 / 1_000_000_000.0;
    public void update() {
        this.now = System.nanoTime();
		this.time = this.now * this.timeMeas; // Current time in seconds
        this.deltaTime = ((double) (this.now - this.last)) * this.timeMeas * GameProperties.getTimeFactor(); // Delta time in seconds
        this.last = this.now;


		for (var actor : ActorManager.getActorsIter()) {
			actor.getValue().update(this.deltaTime);
//			if (actor.getKey().equals("tree-0")) {
//				actor.getValue().getTransform().setLocation(new Vec2(
//						actor.getValue().getTransform().getLocation().x,
//						Math.sin(this.time) * 100
//				));
//			}
		}
    }

//	private boolean once = true;
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        // Draw the game...
        paint(g);

        Graphics2D g2d = (Graphics2D) g;


	    ArrayList<Actor> actorsList = ActorManager.getActorsList();
//	    if (once) {
//		    for (Actor actor : actorsList) {
//			    System.out.print(actor.getId());
//			    System.out.print(": ");
//			    System.out.print(actor.getTransform().getLocation().y);
//			    System.out.print("\n");
//		    }
//			once = false;
//		    System.out.print("\n\n------------------------------\n\n");
//	    }
//	    System.out.print(ActorManager.getActor("tree-0").getTransform().getLocation().y);
//	    System.out.print("\n");
	    for (Actor actor : actorsList) {
			actor.render(this, g2d, deltaTime);
        }


        g2d.dispose();
        bs.show();
    }



    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Game game = new Game();
        // Initialize JFrame settings...
        game.setVisible(true);


	    InputMapper.createAction(game, "left-move", "A");
	    InputMapper.createAction(game, "right-move", "D");
	    InputMapper.createAction(game, "up-move", "W");
	    InputMapper.createAction(game, "down-move", "S");

	    InputMapper.createAction(game, "left-lean", "Q");
	    InputMapper.createAction(game, "right-lean", "E");


        game.start();
    }
}