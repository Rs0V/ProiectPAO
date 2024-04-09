package org.example.actor;

import org.example.collider.Collider;
import org.example.sprite.Sprite;
import org.example.transform.Transform;


public class Pawn extends Actor {
	public Pawn(Transform transform, Collider collider, Sprite sprite) {
		super(transform, collider, sprite);
	}
}
