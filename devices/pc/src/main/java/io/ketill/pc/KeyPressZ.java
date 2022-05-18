package io.ketill.pc;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Contains the state of a {@link KeyboardKey}.
 */
public class KeyPressZ implements AutonomousState {

    public boolean pressed;

    @AutonomousField
    public boolean held;

    private final KeyboardKeyObserver keyObserver;

    /**
     * @param key      the key which created this state.
     * @param observer the I/O device observer.
     * @throws NullPointerException if {@code button} or {@code observer}
     *                              are {@code null}.
     */
    public KeyPressZ(@NotNull KeyboardKey key,
                     @NotNull IoDeviceObserver observer) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");

        this.keyObserver = new KeyboardKeyObserver(key, this, observer);
    }

    @Override
    public void update() {
        keyObserver.poll();
    }

}
