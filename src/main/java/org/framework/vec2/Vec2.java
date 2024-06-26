package org.framework.vec2;

public class Vec2 {
    public double x, y;


    public Vec2() {
        this.x = 0;
        this.y = 0;
    }
    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vec2 lerp(Vec2 v1, Vec2 v2, double alpha) {
        double x = alpha * v2.x + (1 - alpha) * v1.x;
        double y = alpha * v2.y + (1 - alpha) * v1.y;
        return new Vec2(x, y);
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }
    public Vec2 mul(double number) {
        return new Vec2(this.x * number, this.y * number);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    public Vec2 normalized() {
        double len = this.length();
        len = len == 0 ? 1 : len;
        return new Vec2(this.x / len, this.y / len);
    }

    public double dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public double dist(Vec2 other) {
        return other.add(this.mul(-1)).length();
    }
    public boolean in(Vec2 topLeft, Vec2 bottomRight) {
        return topLeft.x <= this.x && this.x <= bottomRight.x
                && topLeft.y <= this.y && this.y <= bottomRight.y;
    }
	public boolean on(Vec2 v1, Vec2 v2) {
		return v1.dist(this) + v2.dist(this) == v1.dist(v2);
	}

    @Override
    public String toString() {
        return "Vec2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
