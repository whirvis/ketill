package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link ControllerButton}.
 */
public interface ControllerButtonEvent {

    /**
     * @return the button which triggered this event.
     */
    @NotNull ControllerButton getButton();

}
