package io.ketill.glfw;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

/**
 * An interface which defines a function that can create the appropriate
 * adapter and I/O device from a GLFW window pointer and joystick ID. This
 * is required by {@link GlfwJoystickSeeker} so it can create an adapter
 * for joysticks when they are discovered.
 *
 * @param <I> the I/O device type.
 * @see GlfwJoystickSeeker#seekGuid(String, GlfwJoystickWrangler)
 */
@FunctionalInterface
public interface GlfwJoystickWrangler<I extends IoDevice> {

    @NotNull I wrangleDevice(long ptr_glfwWindow, int glfwJoystick);

}