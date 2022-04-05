package io.ketill.controller;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

/**
 * Interface to a read-only view of an analog stick's state.
 */
public interface Stick3fc extends Vector3fc {

    /**
     * @return the state containing if this analog stick is being pressed
     * upwards.
     */
    @NotNull Button1bc up();

    /**
     * @return the state containing if this analog stick is being pressed
     * downwards.
     */
    @NotNull Button1bc down();

    /**
     * @return the state containing if this analog stick is being pressed
     * left.
     */
    @NotNull Button1bc left();

    /**
     * @return the state containing if this analog stick is being pressed
     * right.
     */
    @NotNull Button1bc right();

}
