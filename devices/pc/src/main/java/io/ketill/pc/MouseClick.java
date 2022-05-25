package io.ketill.pc;

import io.ketill.ContainerState;
import io.ketill.pressable.PressableState;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to a read-only view of a mouse button's state.
 * <p>
 * <b>Note: </b> The name {@code MouseClick} was deliberately chosen so
 * there would be no conflict with the class {@code ButtonState} found in
 * the {@code controller} module.
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
