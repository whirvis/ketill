package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link GlfwJoystickSeeker}.
 */
public abstract class GlfwJoystickSeekerEvent extends GlfwDeviceSeekerEvent {

    /**
     * Constructs a new {@code GlfwJoystickSeekerEvent}.
     *
     * @param seeker the seeker which emitted this event.
     * @throws NullPointerException if {@code seeker} is {@code null}.
     */
    public GlfwJoystickSeekerEvent(@NotNull GlfwJoystickSeeker<?> seeker) {
        super(seeker);
    }



}
