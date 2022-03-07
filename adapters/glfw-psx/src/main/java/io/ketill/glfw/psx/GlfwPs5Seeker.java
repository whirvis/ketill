package io.ketill.glfw.psx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.psx.Ps5Controller;

import java.util.Collection;

public class GlfwPs5Seeker extends GlfwJoystickSeeker<Ps5Controller> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwPs5Seeker(long ptr_glfwWindow) {
        super(Ps5Controller.class, ptr_glfwWindow);

        String guidsPath = "guids_ps5.json";
        Collection<String> xboxGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(xboxGuids, GlfwPs5Adapter::wrangle);
    }

}
