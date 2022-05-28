package io.ketill.pc;

import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is released.
 */
public final class KeyboardKeyReleaseEvent extends IoFeatureReleaseEvent
        implements KeyboardKeyEvent {

    KeyboardKeyReleaseEvent(@NotNull Keyboard keyboard,
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
