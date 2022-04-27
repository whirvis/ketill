package io.ketill.controller;

import io.ketill.StateContainer;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

/**
 * Read-only view of an analog trigger's state.
 */
public class TriggerState extends StateContainer<TriggerStateZ>
        implements PressableState {

    /**
     * @param internalState the internal state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public TriggerState(@NotNull TriggerStateZ internalState) {
        super(internalState);
    }

    /**
     * @return the force being applied to the trigger.
     */
    public float getForce() {
        return internalState.force;
    }

    @Override
    public boolean isPressed() {
        return internalState.pressed;
    }

    @Override
    public boolean isHeld() {
        return internalState.held;
    }

}
