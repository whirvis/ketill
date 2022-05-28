package io.ketill.controller;

import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is held down.
 */
public final class AnalogTriggerHoldEvent extends IoFeatureHoldEvent
        implements AnalogTriggerEvent {

    AnalogTriggerHoldEvent(@NotNull Controller controller,
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
