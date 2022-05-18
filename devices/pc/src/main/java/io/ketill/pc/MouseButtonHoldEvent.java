package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is held down.
 */
public final class MouseButtonHoldEvent extends IoFeatureHoldEvent
        implements MouseButtonEvent {

    MouseButtonHoldEvent(@NotNull IoDevice device,
                         @NotNull MouseButton button) {
        super(device, button);
    }

    @Override
    public @NotNull MouseButton getButton() {
        return (MouseButton) this.getFeature();
    }

}
