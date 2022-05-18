package io.ketill.pc;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to a read-only view of a mouse button's state.
 */
public final class MouseClick extends ContainerState<MouseClickZ>
        implements PressableState {

    MouseClick(@NotNull MouseClickZ internalState) {
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
