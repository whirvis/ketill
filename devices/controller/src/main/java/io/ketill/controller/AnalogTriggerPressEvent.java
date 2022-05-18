package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is pressed.
 */
public final class AnalogTriggerPressEvent extends IoFeaturePressEvent
        implements AnalogTriggerEvent {

    AnalogTriggerPressEvent(@NotNull IoDevice device,
                            @NotNull AnalogTrigger trigger) {
        super(device, trigger);
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
