package org.example.vec2;

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
        return new Vec2(this.x / len, this.y / len);
    }

    public double dotProd(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }
}
