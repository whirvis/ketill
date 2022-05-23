package io.ketill.pc;

import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Keyboard} when a {@link KeyboardKey} is held down.
 */
public final class KeyboardKeyHoldEvent extends IoFeatureHoldEvent
        implements KeyboardKeyEvent {

    KeyboardKeyHoldEvent(@NotNull Keyboard keyboard,
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
