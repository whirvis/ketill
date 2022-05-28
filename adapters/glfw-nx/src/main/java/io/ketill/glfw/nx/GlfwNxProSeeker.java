package io.ketill.glfw.nx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.nx.NxProController;

import java.util.Collection;

/**
 * An {@link NxProController} seeker using GLFW.
 */
public class GlfwNxProSeeker extends GlfwJoystickSeeker<NxProController> {

    /**
     * Constructs a new {@code GlfwNxProSeeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero.)
     */
    public GlfwNxProSeeker(long ptr_glfwWindow) {
        super(NxProController.class, ptr_glfwWindow);

        String guidsPath = "guids_nx_pro.json";
        Collection<String> nxProGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(nxProGuids, GlfwNxProAdapter::wrangle);
    }

}
