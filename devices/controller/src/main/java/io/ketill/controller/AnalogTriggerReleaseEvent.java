package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is released.
 */
public final class AnalogTriggerReleaseEvent extends IoFeatureReleaseEvent
        implements AnalogTriggerEvent {

    AnalogTriggerReleaseEvent(@NotNull IoDevice device,
                              @NotNull AnalogTrigger trigger) {
        super(device, trigger);
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
