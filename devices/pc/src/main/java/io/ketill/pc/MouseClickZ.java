package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link MouseButton}.
 */
public final class MouseClickZ implements AutonomousState {

    public boolean pressed;

    @AutonomousField
    public boolean held;

    private final MouseClickObserver clickObserver;

    MouseClickZ(@NotNull MouseButton button,
                @NotNull IoDeviceObserver observer) {
        this.clickObserver = new MouseClickObserver(button, this, observer);
    }

    @Override
    public void update() {
        clickObserver.poll();
    }

}
