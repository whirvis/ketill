package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Contains the state of a {@link MouseButton}.
 */
public class MouseClickZ implements AutonomousState {

    public boolean pressed;

    @AutonomousField
    public boolean held;

    private final MouseClickObserver clickObserver;

    /**
     * @param button   the button which created this state.
     * @param observer the I/O device observer.
     * @throws NullPointerException if {@code button} or {@code observer}
     *                              are {@code null}.
     */
    public MouseClickZ(@NotNull MouseButton button,
                       @NotNull IoDeviceObserver observer) {
        Objects.requireNonNull(button, "button cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");

        this.clickObserver = new MouseClickObserver(button, this, observer);
    }

    @Override
    public void update() {
        clickObserver.poll();
    }

}
