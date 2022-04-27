package io.ketill.pc;

import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Read only view of a keyboard key's state.
 */
public class KeyPress implements PressableState {

    private final @NotNull KeyPressZ internalState;

    /**
     * @param internalState the internal button state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public KeyPress(@NotNull KeyPressZ internalState) {
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
