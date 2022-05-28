package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link KeyboardKey}.
 */
public final class KeyPressZ implements AutonomousState {

    /**
     * This should be updated by the adapter to indicate if the key
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

    private final KeyboardKeyObserver keyObserver;

    KeyPressZ(@NotNull KeyboardKey key, @NotNull IoDeviceObserver observer) {
        this.keyObserver = new KeyboardKeyObserver(key, this, observer);
    }

    @Override
    public void update() {
        keyObserver.poll();
    }

}
