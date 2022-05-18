package io.ketill.pc;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

/**
 * Read only view of a keyboard key's state.
 */
public final class KeyPress extends ContainerState<KeyPressZ>
        implements PressableState {

    KeyPress(@NotNull KeyPressZ internalState) {
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
