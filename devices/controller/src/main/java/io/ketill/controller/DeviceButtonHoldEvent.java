package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a {@link DeviceButton} is held down.
 */
public class DeviceButtonHoldEvent extends IoFeatureHoldEvent
        implements DeviceButtonEvent {

    /**
     * @param device the device which emitted this event.
     * @param button the button which triggered this event.
     * @throws NullPointerException if {@code device} or {@code button}
     *                              are {@code null}.
     */
    public DeviceButtonHoldEvent(@NotNull IoDevice device,
                                 @NotNull DeviceButton button) {
        super(device, button);
    }

    @Override
    public @NotNull DeviceButton getButton() {
        return (DeviceButton) this.getFeature();
    }

}
