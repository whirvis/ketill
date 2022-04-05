package io.ketill.controller;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Contains the state of a {@link AnalogStick}.
 */
public class Stick3f extends Vector3f implements Stick3fc {

    public final @NotNull Button1b up, down, left, right;

    /**
     * @param x the initial value of the X-axis.
     * @param y the initial value of the Y-axis.
     * @param z the initial value of the Y-axis.
     */
    public Stick3f(float x, float y, float z) {
        super(x, y, z);
        this.up = new Button1b();
        this.down = new Button1b();
        this.left = new Button1b();
        this.right = new Button1b();
    }

    /**
     * Constructs a new {@code Stick3f} with all three components being
     * initialized to {@code 0.0F}.
     */
    public Stick3f() {
        this(0.0F, 0.0F, 0.0F);
    }

    @Override
    public @NotNull Button1bc up() {
        return this.up;
    }

    @Override
    public @NotNull Button1bc down() {
        return this.down;
    }

    @Override
    public @NotNull Button1bc left() {
        return this.left;
    }

    @Override
    public @NotNull Button1bc right() {
        return this.right;
    }

}
