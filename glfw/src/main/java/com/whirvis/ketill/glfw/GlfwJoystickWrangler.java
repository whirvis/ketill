package com.whirvis.ketill.glfw;

import com.whirvis.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

/**
 * An interface which defines a function that can create the appropriate
 * adapter and I/O device from a GLFW window pointer and joystick ID.
 *
 * @param <I> the I/O device type.
 */
@FunctionalInterface
public interface GlfwJoystickWrangler<I extends IoDevice> {

    @NotNull I wrangleDevice(long ptr_glfwWindow, int glfwJoystick);

}
