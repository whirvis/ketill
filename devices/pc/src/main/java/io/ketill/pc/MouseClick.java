package io.ketill.pc;

import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Interface to a read-only view of a mouse button's state.
 */
public class MouseClick implements PressableState {

    private final @NotNull MouseClickZ internalState;

    /**
     * @param internalState the internal button state.
     * @throws NullPointerException if {@code internalState} is {@code null}.
     */
    public MouseClick(@NotNull MouseClickZ internalState) {
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
