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
import org.example.vec2.Vec2;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
import java.util.ArrayList;


public class Game extends JFrame implements Runnable {
    private boolean running = false;
    private int fps = 60;

    private ArrayList<Actor> gameActorList;
    private Actor block;
    private Pawn player;

    private long lastTime;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean jumpPressed;


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

        this.lastTime = System.nanoTime();

        this.block = new Actor(null, null, null);
        this.player = new Pawn(null, null, null);

        this.gameActorList = new ArrayList<>();
        this.gameActorList.add(this.block);
        this.gameActorList.add(this.player);
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
        // Update game state...
        long now = System.nanoTime();
        double deltaTime = (now - lastTime) * timeFactor; // Delta time in seconds
        lastTime = now;

        Vec2 moveInput = new Vec2(
                (rightPressed ? 1 : 0) - (leftPressed ? 1 : 0),
                (downPressed ? 1 : 0) - (upPressed ? 1 : 0)
        );
        moveInput = moveInput.normalized();

        if (leftPressed || rightPressed || upPressed || downPressed) {
            player.getTransform().moveGlobal(moveInput.mul(deltaTime).mul(10000));
        }
    }

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
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);


        for (Actor actor : this.gameActorList) {
            AffineTransform at = new AffineTransform();
            Vec2 origin = new Vec2(
                    actor.getSprite().getImage().getWidth() * actor.getSprite().getOrigin().x,
                    actor.getSprite().getImage().getHeight() * actor.getSprite().getOrigin().y
            );
            at.translate(origin.x, origin.y);

            at.scale(actor.getTransform().getScale().x, actor.getTransform().getScale().y);
            at.scale(actor.getSprite().getScale().x, actor.getSprite().getScale().y);

            at.translate(-origin.x, -origin.y);


            at.translate(actor.getTransform().getLocation().x, actor.getTransform().getLocation().y);
            at.rotate(actor.getTransform().getActualRotation());

            g2d.drawImage(actor.getSprite().getImage(), at, null);
        }


        g2d.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        // Initialize JFrame settings...
        game.setVisible(true);



        //region LeftPressed Action
        Action leftPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.leftPressed = true;
            }
        };
        Action leftReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.leftPressed = false;
            }
        };
        String key = "left";
        KeyStroke pressedKeyStroke = KeyStroke.getKeyStroke("A");
        KeyStroke releasedKeyStroke = KeyStroke.getKeyStroke("released A");

        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, key + " pressed");
        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, key + " released");

        game.getRootPane().getActionMap().put(key + " pressed", leftPressed);
        game.getRootPane().getActionMap().put(key + " released", leftReleased);
        //endregion


        //region RightPressed Action
        Action rightPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.rightPressed = true;
            }
        };
        Action rightReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.rightPressed = false;
            }
        };
        key = "right";
        pressedKeyStroke = KeyStroke.getKeyStroke("D");
        releasedKeyStroke = KeyStroke.getKeyStroke("released D");

        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, key + " pressed");
        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, key + " released");

        game.getRootPane().getActionMap().put(key + " pressed", rightPressed);
        game.getRootPane().getActionMap().put(key + " released", rightReleased);
        //endregion


        //region UpPressed Action
        Action upPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.upPressed = true;
            }
        };
        Action upReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.upPressed = false;
            }
        };
        key = "up";
        pressedKeyStroke = KeyStroke.getKeyStroke("W");
        releasedKeyStroke = KeyStroke.getKeyStroke("released W");

        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, key + " pressed");
        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, key + " released");

        game.getRootPane().getActionMap().put(key + " pressed", upPressed);
        game.getRootPane().getActionMap().put(key + " released", upReleased);
        //endregion


        //region DownPressed Action
        Action downPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.downPressed = true;
            }
        };
        Action downReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.downPressed = false;
            }
        };
        key = "down";
        pressedKeyStroke = KeyStroke.getKeyStroke("S");
        releasedKeyStroke = KeyStroke.getKeyStroke("released S");

        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, key + " pressed");
        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, key + " released");

        game.getRootPane().getActionMap().put(key + " pressed", downPressed);
        game.getRootPane().getActionMap().put(key + " released", downReleased);
        //endregion


        //region JumpPressed Action
        Action jumpPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.jumpPressed = true;
            }
        };
        Action jumpReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.jumpPressed = false;
            }
        };
        key = "jump";
        pressedKeyStroke = KeyStroke.getKeyStroke("SPACE");
        releasedKeyStroke = KeyStroke.getKeyStroke("released SPACE");

        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pressedKeyStroke, key + " pressed");
        game.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(releasedKeyStroke, key + " released");

        game.getRootPane().getActionMap().put(key + " pressed", jumpPressed);
        game.getRootPane().getActionMap().put(key + " released", jumpReleased);
        //endregion



        game.start();
    }
}