package io.ketill.glfw.xbox;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.xbox.XboxController;

import java.util.Collection;

/**
 * An {@link XboxController} seeker using GLFW.
 */
public class GlfwXboxSeeker extends GlfwJoystickSeeker<XboxController> {

    /**
     * Constructs a new {@code GlfwXboxSeeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    public GlfwXboxSeeker(long ptr_glfwWindow) {
        super(XboxController.class, ptr_glfwWindow);

        String guidsPath = "guids_xbox.json";
        Collection<String> xboxGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(xboxGuids, GlfwXboxAdapter::wrangle);
    }

}
