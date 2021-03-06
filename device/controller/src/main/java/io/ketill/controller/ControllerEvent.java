package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link Controller}.
 */
public interface ControllerEvent {

    /**
     * Returns the controller which emitted this event.
     *
     * @return the controller which emitted this event.
     */
    @NotNull Controller getController();

}
