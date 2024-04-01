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
import org.example.shape.Circle;
import org.example.shape.Rectangle;
import org.example.transform.Transform;

public class Main {
    public static void main(String[] args) {
        Actor block = new Actor(new Transform(), new Collider(new Rectangle()));
        Pawn player = new Pawn(new Transform(), new Collider(new Circle()));
    }
}