package io.ketill.glfw.psx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.psx.Ps4Controller;

import java.util.Collection;

public class GlfwPs4Seeker extends GlfwJoystickSeeker<Ps4Controller> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwPs4Seeker(long ptr_glfwWindow) {
        super(Ps4Controller.class, ptr_glfwWindow);

        String guidsPath = "guids_ps4.json";
        Collection<String> xboxGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(xboxGuids, GlfwPs4Adapter::wrangle);
    }

}
