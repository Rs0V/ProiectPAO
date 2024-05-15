package org.framework.animation.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.framework.actor.Actor;
import org.framework.actor.Camera;
import org.framework.animation.AnimationCurve;
import org.framework.animation.enums.EasingType;
import org.framework.component.IComponent;
import org.framework.services.TimeManager;
import org.framework.services.enums.RenderHints;
import org.framework.vec2.Vec2;
import org.utilities.Utilities;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class CAnimation<T> implements IComponent {
	protected Object targetParent;
	protected Field targetField;
	@Getter @Setter
	protected T min;
	@Getter @Setter
	protected T max;
	@Getter @Setter
	protected double duration;
	protected double t = 0;
	protected int loops = 0;
	@Getter @Setter
	protected AnimationCurve animationCurve;
	@Getter @Setter
	protected EasingType easingType;


	public CAnimation(Object targetParent, String targetName, EasingType easingType, T min, T max) {
		this.targetParent = targetParent;
		Class<?> targetParentClass = this.targetParent.getClass();
		try {
			this.targetField = targetParentClass.getDeclaredField(targetName);
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("Class '" + targetParentClass.getName() + "' does not hava a '" + targetName + "' field");
		}
		this.targetField.setAccessible(true);

		this.easingType = easingType == null ? EasingType.Linear : easingType;
		switch (this.easingType) {
			case Linear:
				this.animationCurve = new AnimationCurve();
				break;
			case EaseIn:
				this.animationCurve = new AnimationCurve(
						new Vec2(0.60, 0.15)
				);
				break;
			case EaseOut:
				this.animationCurve = new AnimationCurve(
						new Vec2(0.35, 0.85)
				);
				break;
			case EaseInOut:
				this.animationCurve = new AnimationCurve(
						new Vec2(0.42, 0.10),
						new Vec2(0.65, 0.90)
				);
				break;
		}

		this.min = min;
		this.max = max;
	}

	public void play(int loops) {
		this.loops = loops;
	}
	public void stop(boolean reset) {
		if (reset == true)
			this.t = 0;
		this.loops = 0;
	}

	@Override
	public void update() {
		if (this.loops != 0) {
			try {
				// Horrible...
				if (this.min instanceof Integer _min && this.max instanceof Integer _max) {
					if (this.easingType == EasingType.Linear)
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getLinear(this.t)));
					else
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getSmooth(this.t)));
				}
				else if (this.min instanceof Long _min && this.max instanceof Long _max) {
					if (this.easingType == EasingType.Linear)
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getLinear(this.t)));
					else
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getSmooth(this.t)));
				}
				else if (this.min instanceof Float _min && this.max instanceof Float _max) {
					if (this.easingType == EasingType.Linear)
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getLinear(this.t)));
					else
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getSmooth(this.t)));
				}
				else if (this.min instanceof Double _min && this.max instanceof Double _max) {
					if (this.easingType == EasingType.Linear)
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getLinear(this.t)));
					else
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getSmooth(this.t)));
				}
				else if (this.min instanceof Vec2 _min && this.max instanceof Vec2 _max) {
					if (this.easingType == EasingType.Linear)
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getLinear(this.t)));
					else
						this.targetField.set(this.targetParent, Utilities.lerp(_min, _max, this.animationCurve.getSmooth(this.t)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			var tempT = Utilities.jumpClamp(t + TimeManager.getDeltaTime() / duration, 0.0, 1.0);
			if (tempT < this.t) {
				if (this.loops > 0)
					this.loops--;
			}
			this.t = tempT;
		}
	}

	@Override
	public void render(Graphics2D g2d, RenderHints renderHints, Camera camera) {

	}
}
