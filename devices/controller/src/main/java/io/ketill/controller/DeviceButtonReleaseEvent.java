package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a {@link DeviceButton} is released.
 */
public class DeviceButtonReleaseEvent extends IoFeatureReleaseEvent
        implements DeviceButtonEvent {

    /**
     * @param device the device which emitted this event.
     * @param button the button which triggered this event.
     * @throws NullPointerException if {@code device} or {@code button}
     *                              are {@code null}.
     */
    public DeviceButtonReleaseEvent(@NotNull IoDevice device,
                                    @NotNull DeviceButton button) {
        super(device, button);
    }

    @Override
    public @NotNull DeviceButton getButton() {
        return (DeviceButton) this.getFeature();
    }

}
