package org.example;

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


import org.example.actor.Actor;
import org.example.services.ActorManager;
import org.example.services.InputMapper;
import org.example.sprite.Sprite;
import org.example.vec2.Vec2;


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
    private final int fps = 60;
    private long last = System.nanoTime();
	private long now;
	private double time;
	double deltaTime;
	private Vec2 screenRes = new Vec2(800, 600);


    public Game() {
        // Initialize JFrame settings...
        setSize((int)screenRes.x, (int)screenRes.y); // Set the size of the window
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

		int noTrees = 10;
	    for (int i = 0; i < noTrees; i++) {
		    Actor tree = ActorManager.createActor(String.format("tree-%d", i), false);
			tree.getTransform().setLocation(new Vec2(
					Math.clamp(Math.random() * screenRes.x, 100, screenRes.x - 100),
					Math.clamp(Math.random() * screenRes.y, 100, screenRes.y - 100)
			));
			tree.setSprite(new Sprite(
					"src/main/resources/treeus.png",
					null,
					new Vec2(5, 5)
			));
	    }
	    {
		    Actor player = ActorManager.createActor("player-0", true);
		    player.getTransform().setLocation(new Vec2(
				    Math.clamp(Math.random() * screenRes.x, 100, screenRes.x - 100),
				    Math.clamp(Math.random() * screenRes.y, 100, screenRes.y - 100)
		    ));
		    player.setSprite(new Sprite(
				    "src/main/resources/tempPlayer.png",
				    null,
				    new Vec2(2, 2)
		    ));
	    }
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
                Thread.sleep(1000 / fps); // Cap the frame rate to 60 FPS
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

    private final double timeFactor = 1 / 1_000_000_000.0;
    public void update() {
        this.now = System.nanoTime();
		this.time = this.now * this.timeFactor; // Current time in seconds
        this.deltaTime = (this.now - this.last) * this.timeFactor; // Delta time in seconds
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

	    this.time = this.now * this.timeFactor; // Current time in seconds
		this.deltaTime = (this.now - this.last) * this.timeFactor; // Delta time in seconds


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



    public static void main(String[] args) {
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