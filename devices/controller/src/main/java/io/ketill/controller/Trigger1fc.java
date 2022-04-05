package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * Interface to a read-only view of an analog trigger's state.
 */
public interface Trigger1fc {

    /**
     * @return the force being applied to the trigger.
     */
    float getForce();

    /**
     * @return the state containing if this analog trigger is being pressed.
     */
    @NotNull Button1bc button();

    /**
     * Shorthand for {@code this.button().isPressed()}.
     *
     * @return {@code true} if this analog trigger is currently being pressed,
     * {@code false} otherwise.
     */
    default boolean isPressed() {
        return this.button().isPressed();
    }

    /**
     * Shorthand for {@code this.button().isHeld()}.
     *
     * @return {@code true} if this analog trigger is currently being held
     * down, {@code false} otherwise.
     */
    default boolean isHeld() {
        return this.button().isHeld();
    }

}
