package io.ketill.controller;

import io.ketill.AdapterUpdatedField;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of an {@link AnalogTrigger}.
 */
public class Trigger1f implements Trigger1fc {

    @AdapterUpdatedField
    public float force;

    public @NotNull Button1b button;

    /**
     * @param force the initial trigger force.
     */
    public Trigger1f(float force) {
        this.force = force;
        this.button = new Button1b();
    }

    /**
     * Constructs a new {@code Trigger1f} with a force of {@code 0.0F}.
     */
    public Trigger1f() {
        this(0.0F);
    }

    @Override
    public float getForce() {
        return this.force;
    }

    @Override
    public @NotNull Button1bc button() {
        return this.button;
    }

}
