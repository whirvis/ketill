package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is held down.
 */
public final class AnalogTriggerHoldEvent extends IoFeatureHoldEvent
        implements AnalogTriggerEvent {

    AnalogTriggerHoldEvent(@NotNull IoDevice device,
                           @NotNull AnalogTrigger trigger) {
        super(device, trigger);
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
