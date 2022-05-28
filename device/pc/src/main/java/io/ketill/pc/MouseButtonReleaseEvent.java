package io.ketill.pc;

import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is released.
 */
public final class MouseButtonReleaseEvent extends IoFeatureReleaseEvent
        implements MouseButtonEvent {

    MouseButtonReleaseEvent(@NotNull Mouse mouse,
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
