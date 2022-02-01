package com.whirvis.ketill.glfw;

import com.whirvis.ketill.InputDevice;
import org.jetbrains.annotations.NotNull;

/**
 * An interface which defines a function that can create the appropriate
 * adapter and input device from a GLFW window pointer and joystick ID.
 *
 * @param <I> the input device type.
 */
@FunctionalInterface
public interface GlfwJoystickWrangler<I extends InputDevice> {

    @NotNull I wrangleDevice(long ptr_glfwWindow, int glfwJoystick);

}
