package org.framework.animation;

import org.framework.vec2.Vec2;
import org.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class AnimationCurve {
	protected List<Vec2> points; // (0, 0) and (1, 1) are included by default


	/**
	 * Initialize the animation curve with points in the (0, 0) ... (1, 1) range sorted in ascending order.
	 */
	public AnimationCurve(Vec2... points) {
		this.points = new ArrayList<>(List.of(points));
		this.points.addFirst(new Vec2());
		this.points.addLast(new Vec2(1, 1));
	}

	public double getLinear(double t) {
		int i;
		for (i = 0; i < this.points.size(); i++) {
			if (t >= this.points.get(i).x)
				break;
		}
		Vec2 a = this.points.get(i);
		Vec2 b = this.points.get(i+1);
		return Utilities.lerp(a, b, (t - a.x) / (b.x - a.x)).y;
	}

	public double getSmooth(double t) {
		int i;
		for (i = 0; i < this.points.size(); i++) {
			if (t >= this.points.get(i).x)
				break;
		}
		Vec2 a, b, c;
		if (i + 2 == this.points.size()) {
			a = this.points.get(i-1);
			b = this.points.get(i);
			c = this.points.get(i+1);
		} else {
			a = this.points.get(i);
			b = this.points.get(i+1);
			c = this.points.get(i+2);
		}
		Vec2 ab = Utilities.lerp(a, b, (t - a.x) / (b.x - a.x));
		Vec2 bc = Utilities.lerp(b, c, (t - a.x) / (b.x - a.x));
		return Utilities.lerp(ab, bc, (t - a.x) / (b.x - a.x)).y;
	}
}
