package io.ketill.controller;

import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Read-only view of a button's state.
 */
public class ButtonState implements PressableState {

    private final @NotNull ButtonStateZ internalState;

    /**
     * @param internalState the internal button state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public ButtonState(@NotNull ButtonStateZ internalState) {
        this.internalState = Objects.requireNonNull(internalState,
                "internalState cannot be null");
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
