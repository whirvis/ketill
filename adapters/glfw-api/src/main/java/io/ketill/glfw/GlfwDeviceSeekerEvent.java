package io.ketill.glfw;

import io.ketill.IoDeviceSeekerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link GlfwDeviceSeeker}.
 */
public abstract class GlfwDeviceSeekerEvent extends IoDeviceSeekerEvent {

    /**
     * @param seeker the seeker which emitted this event.
     * @throws NullPointerException if {@code seeker} is {@code null}.
     */
    public GlfwDeviceSeekerEvent(@NotNull GlfwDeviceSeeker<?> seeker) {
        super(seeker);
    }

}
