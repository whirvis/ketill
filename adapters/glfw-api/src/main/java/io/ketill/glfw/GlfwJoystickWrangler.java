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
 * @see GlfwJoystickSeeker#wrangleGuid(String, GlfwJoystickWrangler)
 */
@FunctionalInterface
public interface GlfwJoystickWrangler<I extends IoDevice> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled device.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    @NotNull I wrangleDevice(long ptr_glfwWindow, int glfwJoystick);

}
