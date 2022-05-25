package io.ketill.controller;

import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is pressed.
 */
public final class AnalogTriggerPressEvent extends IoFeaturePressEvent
        implements AnalogTriggerEvent {

    AnalogTriggerPressEvent(@NotNull Controller controller,
                            @NotNull AnalogTrigger trigger) {
        super(controller, trigger);
    }

    @Override
    public @NotNull Controller getController() {
        return (Controller) this.getDevice();
    }

    @Override
    public @NotNull AnalogTrigger getTrigger() {
        return (AnalogTrigger) this.getFeature();
    }

}
