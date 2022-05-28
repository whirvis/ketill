package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to an {@link AnalogStick}.
 */
public interface AnalogStickEvent extends ControllerEvent {

    /**
     * @return the analog stick which triggered this event.
     */
    @NotNull AnalogStick getStick();

    /**
     * @return the direction of the analog stick.
     */
    @NotNull Direction getDirection();

}