package io.ketill.pc;

import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is pressed.
 */
public final class MouseButtonPressEvent extends IoFeaturePressEvent
        implements MouseButtonEvent {

    MouseButtonPressEvent(@NotNull Mouse mouse,
                          @NotNull MouseButton button) {
        super(mouse, button);
    }

    @Override
    public @NotNull Mouse getMouse() {
        return (Mouse) this.getDevice();
    }

    @Override
    public @NotNull MouseButton getButton() {
        return (MouseButton) this.getFeature();
    }

}
