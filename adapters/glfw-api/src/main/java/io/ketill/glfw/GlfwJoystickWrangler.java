package io.ketill.glfw;

import io.ketill.controller.Controller;
import org.jetbrains.annotations.NotNull;

/**
 * An interface which defines a function that can create the appropriate
 * adapter and controller from a GLFW window pointer and joystick ID. This
 * is required by {@link GlfwJoystickSeeker} so it can create an adapter
 * for joysticks when they are discovered.
 *
 * @param <C> the controller type.
 * @see GlfwJoystickSeeker#wrangleGuid(String, GlfwJoystickWrangler)
 */
@FunctionalInterface
public interface GlfwJoystickWrangler<C extends Controller> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled controller.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    @NotNull C wrangleDevice(long ptr_glfwWindow, int glfwJoystick);

}
