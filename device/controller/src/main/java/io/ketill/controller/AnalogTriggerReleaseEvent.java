package io.ketill.controller;

import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is released.
 */
public final class AnalogTriggerReleaseEvent extends IoFeatureReleaseEvent
        implements AnalogTriggerEvent {

    AnalogTriggerReleaseEvent(@NotNull Controller controller,
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
