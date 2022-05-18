package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link DeviceButton}.
 */
public interface DeviceButtonEvent {

    /**
     * @return the button which triggered this event.
     */
    @NotNull DeviceButton getButton();

}
