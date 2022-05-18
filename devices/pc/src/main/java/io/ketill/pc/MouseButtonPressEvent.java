package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Mouse} when a {@link MouseButton} is pressed.
 */
public class MouseButtonPressEvent extends IoFeaturePressEvent
        implements MouseButtonEvent {

    /**
     * @param device the device which emitted this event.
     * @param button the button which triggered this event.
     * @throws NullPointerException if {@code device} or {@code button}
     *                              are {@code null}.
     */
    public MouseButtonPressEvent(@NotNull IoDevice device,
                                 @NotNull MouseButton button) {
        super(device, button);
    }

    @Override
    public @NotNull MouseButton getButton() {
        return (MouseButton) this.getFeature();
    }

}
