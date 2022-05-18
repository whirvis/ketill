package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is pressed.
 */
public class AnalogTriggerPressEvent extends IoFeaturePressEvent
        implements AnalogTriggerEvent {

    /**
     * @param device  the device which emitted this event.
     * @param trigger the analog trigger which triggered this event.
     * @throws NullPointerException if {@code device} or {@code trigger}
     *                              are {@code null}.
     */
    public AnalogTriggerPressEvent(@NotNull IoDevice device,
                                   @NotNull AnalogTrigger trigger) {
        super(device, trigger);
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
