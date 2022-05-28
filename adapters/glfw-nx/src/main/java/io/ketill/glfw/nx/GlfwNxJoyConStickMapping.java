package io.ketill.glfw.nx;

import io.ketill.MappingType;
import io.ketill.controller.AnalogStick;

/**
 * A mapping for an {@link AnalogStick} used by {@link GlfwNxJoyConAdapter}.
 *
 * @see GlfwNxJoyConAdapter#mapJoyConStick(AnalogStick, GlfwNxJoyConStickMapping)
 */
@MappingType
public final class GlfwNxJoyConStickMapping {

    private static int requireButton(int glfwButton, String paramName) {
        if (glfwButton < 0) {
            String msg = paramName + " cannot be negative";
            throw new IllegalArgumentException(msg);
        }
        return glfwButton;
    }

    /**
     * The GLFW button for up.
     */
    public final int glfwUp;

    /**
     * The GLFW button for down.
     */
    public final int glfwDown;

    /**
     * The GLFW button for left.
     */
    public final int glfwLeft;

    /**
     * The GLFW button for right.
     */
    public final int glfwRight;

    /**
     * The GLFW button for the thumb button.
     */
    public final int glfwThumb;

    /**
     * Constructs a new {@code JoyConStickMapping}.
     *
     * @param glfwUp    the GLFW button for up.
     * @param glfwDown  the GLFW button for down.
     * @param glfwLeft  the GLFW button for left.
     * @param glfwRight the GLFW button for right.
     * @param glfwThumb the GLFW button for the thumb button.
     * @throws IllegalArgumentException if {@code glfwUp}, {@code glfwDown},
     *                                  {@code glfwLeft}, {@code glfwRight},
     *                                  or {@code glfwThumb} are negative.
     */
    public GlfwNxJoyConStickMapping(int glfwUp, int glfwDown, int glfwLeft,
                                    int glfwRight, int glfwThumb) {
        this.glfwUp = requireButton(glfwUp, "glfwUp");
        this.glfwDown = requireButton(glfwDown, "glfwDown");
        this.glfwLeft = requireButton(glfwLeft, "glfwLeft");
        this.glfwRight = requireButton(glfwRight, "glfwRight");
        this.glfwThumb = requireButton(glfwThumb, "glfwThumb");
    }

}
