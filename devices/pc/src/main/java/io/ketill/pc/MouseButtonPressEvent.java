package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is pressed.
 */
public final class MouseButtonPressEvent extends IoFeaturePressEvent
        implements MouseButtonEvent {

    MouseButtonPressEvent(@NotNull IoDevice device,
                          @NotNull MouseButton button) {
        super(device, button);
    }

    @Override
    public @NotNull MouseButton getButton() {
        return (MouseButton) this.getFeature();
    }

}
