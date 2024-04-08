package org.example.actor;

import org.example.collider.Collider;
import org.example.sprite.Sprite;
import org.example.transform.Transform;
import org.example.vec2.Vec2;

import java.awt.image.BufferedImage;

public class Pawn extends Actor {
	public Pawn(Transform transform, Collider collider, Sprite sprite) {
		super(transform, collider, sprite);
	}
}
