package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is held down.
 */
public class KeyboardKeyHoldEvent extends IoFeatureHoldEvent
        implements KeyboardKeyEvent {

    /**
     * @param device the device which emitted this event.
     * @param key    the key which triggered this event.
     * @throws NullPointerException if {@code device} or {@code key}
     *                              are {@code null}.
     */
    public KeyboardKeyHoldEvent(@NotNull IoDevice device,
                                @NotNull KeyboardKey key) {
        super(device, key);
    }

    @Override
    public @NotNull KeyboardKey getKey() {
        return (KeyboardKey) this.getFeature();
    }

}
