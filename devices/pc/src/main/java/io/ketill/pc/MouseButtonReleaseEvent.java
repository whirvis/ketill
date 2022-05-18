package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is released.
 */
public final class MouseButtonReleaseEvent extends IoFeatureReleaseEvent
        implements MouseButtonEvent {

    MouseButtonReleaseEvent(@NotNull IoDevice device,
                            @NotNull MouseButton button) {
        super(device, button);
    }

    @Override
    public @NotNull MouseButton getButton() {
        return (MouseButton) this.getFeature();
    }

}
