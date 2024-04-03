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
import org.example.actor.Pawn;
import org.example.collider.Collider;
import org.example.shape.Rectangle;
import org.example.transform.Transform;


import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class Game extends JFrame implements Runnable {
    private boolean running = false;
    private Actor block;
    private Pawn player;
    private BufferedImage image;

    private long lastTime;

    public Game() {
        // Initialize JFrame settings...
        setSize(800, 600); // Set the size of the window
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

        lastTime = System.nanoTime();

        Actor block = new Actor(new Transform(), new Collider(new Rectangle()));
        Pawn player = new Pawn(new Transform(), new Collider(new Rectangle()));

        try {
            image = ImageIO.read(new File("src/main/resources/circle.png")); // Load the image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                Thread.sleep(1000 / 60); // Cap the frame rate to 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK); // Set the color to black
        g.fillRect(0, 0, getWidth(), getHeight()); // Fill the entire window with black
    }

    public void update() {
        // Update game state...
    }

    public void render() {
        long now = System.nanoTime();
        double deltaTime = (now - lastTime) / 1_000_000_000.0; // Delta time in seconds
        lastTime = now;

        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        // Draw the game...
        paint(g);
//        g.drawImage(image, 250, 100, null); // Draw the image at (x, y)

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        double scale = Math.abs(Math.sin(now / 1_000_000_000.0)); // Change this to your desired scale
        System.out.println(scale);

        double originX = image.getWidth() / 2.0; // Change this to your desired scaling origin x-coordinate
        double originY = image.getHeight() / 2.0; // Change this to your desired scaling origin y-coordinate

        AffineTransform at = new AffineTransform();
//        at.translate(originX, originY);
        at.scale(scale * .5, scale * .5);
//        at.translate(-originX, -originY);
//        at.translate(-250, -350);

        g2d.drawImage(image, at, null);

        g2d.dispose();

//        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        // Initialize JFrame settings...
        game.setVisible(true);
        game.start();
    }
}