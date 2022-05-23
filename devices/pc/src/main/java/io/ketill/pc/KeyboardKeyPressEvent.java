package io.ketill.pc;

import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is pressed.
 */
public final class KeyboardKeyPressEvent extends IoFeaturePressEvent
        implements KeyboardKeyEvent {

    KeyboardKeyPressEvent(@NotNull Keyboard keyboard,
                          @NotNull KeyboardKey key) {
        super(keyboard, key);
    }

    @Override
    public @NotNull Keyboard getKeyboard() {
        return (Keyboard) this.getDevice();
    }

    @Override
    public @NotNull KeyboardKey getKey() {
        return (KeyboardKey) this.getFeature();
    }

}
