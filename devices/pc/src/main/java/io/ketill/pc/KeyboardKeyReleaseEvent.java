package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is released.
 */
public final class KeyboardKeyReleaseEvent extends IoFeatureReleaseEvent
        implements KeyboardKeyEvent {

    KeyboardKeyReleaseEvent(@NotNull IoDevice device,
                            @NotNull KeyboardKey key) {
        super(device, key);
    }

    @Override
    public @NotNull KeyboardKey getKey() {
        return (KeyboardKey) this.getFeature();
    }

}
