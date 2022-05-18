package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when an {@link AnalogStick} is held down.
 */
public class AnalogTriggerHoldEvent extends IoFeatureHoldEvent
        implements AnalogTriggerEvent {

    /**
     * @param device  the device which emitted this event.
     * @param trigger the analog trigger which triggered this event.
     * @throws NullPointerException if {@code device} or {@code trigger}
     *                              are {@code null}.
     */
    public AnalogTriggerHoldEvent(@NotNull IoDevice device,
                                  @NotNull AnalogTrigger trigger) {
        super(device, trigger);
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
