package io.ketill.pc;

import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is held down.
 */
public final class MouseButtonHoldEvent extends IoFeatureHoldEvent
        implements MouseButtonEvent {

    MouseButtonHoldEvent(@NotNull Mouse mouse,
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
