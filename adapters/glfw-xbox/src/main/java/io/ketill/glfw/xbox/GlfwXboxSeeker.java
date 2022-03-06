package io.ketill.glfw.xbox;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.xbox.XboxController;

import java.util.Collection;

public class GlfwXboxSeeker extends GlfwJoystickSeeker<XboxController> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwXboxSeeker(long ptr_glfwWindow) {
        super(XboxController.class, ptr_glfwWindow);

        String guidsPath = "guids_xbox.json";
        Collection<String> xboxGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(xboxGuids, GlfwXboxAdapter::wrangle);
    }

}
