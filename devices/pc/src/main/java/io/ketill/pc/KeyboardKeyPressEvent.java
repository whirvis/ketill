package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is pressed.
 */
public final class KeyboardKeyPressEvent extends IoFeaturePressEvent
        implements KeyboardKeyEvent {

    KeyboardKeyPressEvent(@NotNull IoDevice device,
                          @NotNull KeyboardKey key) {
        super(device, key);
    }

    @Override
    public @NotNull KeyboardKey getKey() {
        return (KeyboardKey) this.getFeature();
    }

}
