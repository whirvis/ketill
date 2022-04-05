package io.ketill.pc;

import io.ketill.AdapterUpdatedField;
import io.ketill.pressable.MonitorUpdatedField;

/**
 * Contains the state of a {@link MouseButton}.
 */
public class Click1b implements Click1bc {

    @AdapterUpdatedField
    public boolean clicked;

    @MonitorUpdatedField
    public boolean held;

    /**
     * @param clicked the initial click state.
     */
    public Click1b(boolean clicked) {
        this.clicked = clicked;
    }

    /**
     * Constructs a new {@code Click1b} with {@code clicked} set to
     * {@code false}.
     */
    public Click1b() {
        this(false);
    }

    @Override
    public boolean isClicked() {
        return this.clicked;
    }

    @Override
    public boolean isHeld() {
        return this.held;
    }

}
