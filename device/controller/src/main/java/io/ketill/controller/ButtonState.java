package io.ketill.controller;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

/**
 * Read-only view of a button's state.
 */
public final class ButtonState extends ContainerState<ButtonStateZ>
        implements PressableState {

    ButtonState(@NotNull ButtonStateZ internalState) {
        super(internalState);
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
