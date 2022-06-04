package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to an {@link AnalogTrigger}.
 */
public interface AnalogTriggerEvent extends ControllerEvent {

    /**
     * Returns the analog trigger which triggered this event.
     *
     * @return the analog trigger which triggered this event.
     */
    @NotNull AnalogTrigger getTrigger();

}
