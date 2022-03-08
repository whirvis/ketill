package io.ketill.glfw.nx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.nx.NxProController;

import java.util.Collection;

public final class GlfwNxProSeeker extends GlfwJoystickSeeker<NxProController> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwNxProSeeker(long ptr_glfwWindow) {
        super(NxProController.class, ptr_glfwWindow);

        String guidsPath = "guids_nx_pro.json";
        Collection<String> nxProGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(nxProGuids, GlfwNxProAdapter::wrangle);
    }

}
