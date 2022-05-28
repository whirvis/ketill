package io.ketill.glfw.psx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.psx.Ps5Controller;

import java.util.Collection;

/**
 * A {@link Ps5Controller} seeker using GLFW.
 */
public class GlfwPs5Seeker extends GlfwJoystickSeeker<Ps5Controller> {

    /**
     * Constructs a new {@code GlfwPs5Seeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    public GlfwPs5Seeker(long ptr_glfwWindow) {
        super(Ps5Controller.class, ptr_glfwWindow);

        String guidsPath = "guids_ps5.json";
        Collection<String> ps5Guids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(ps5Guids, GlfwPs5Adapter::wrangle);
    }

}
