package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link MouseButton}.
 * <p>
 * <b>Note: </b> The name {@code MouseClickZ} was deliberately chosen so
 * there would be no conflict with the class {@code ButtonStateZ} found in
 * the {@code controller} module.
 */
public final class MouseClickZ implements AutonomousState {

    /**
     * This should be updated by the adapter to indicate if the button
     * is currently pressed.
     */
    public boolean pressed;

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is updated automatically by the state.
     *
     * @see #pressed
     */
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
