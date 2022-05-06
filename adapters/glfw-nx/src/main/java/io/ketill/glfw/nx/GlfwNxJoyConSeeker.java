package io.ketill.glfw.nx;

import io.ketill.glfw.GlfwJoystickSeeker;
import io.ketill.nx.NxJoyCon;

import java.util.Collection;

public class GlfwNxJoyConSeeker extends GlfwJoystickSeeker<NxJoyCon> {

    public final boolean seekingLeftJoyCons;
    public final boolean seekingRightJoyCons;

    /**
     * @param ptr_glfwWindow     the GLFW window pointer.
     * @param seekNxLeftJoyCons  {@code true} if this GLFW joystick seeker
     *                           should seek out Nintendo Switch left
     *                           Joy-Cons, {@code false} otherwise.
     * @param seekNxRightJoyCons {@code true} if this GLFW joystick seeker
     *                           should seek out Nintendo Switch right
     *                           Joy-Cons, {@code false} otherwise.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if both {@code seekNxLeftJoyCons}
     *                                  and {@code seekNxRightJoyCons} are
     *                                  {@code false}.
     */
    public GlfwNxJoyConSeeker(long ptr_glfwWindow, boolean seekNxLeftJoyCons,
                              boolean seekNxRightJoyCons) {
        super(NxJoyCon.class, ptr_glfwWindow);
        if (!seekNxLeftJoyCons && !seekNxRightJoyCons) {
            throw new IllegalArgumentException("must seek at least one " +
                    "type of Joy-Con");
        }

        this.seekingLeftJoyCons = seekNxLeftJoyCons;
        if (seekNxLeftJoyCons) {
            this.wrangleLeftJoyCons();
        }

        this.seekingRightJoyCons = seekNxRightJoyCons;
        if (seekNxRightJoyCons) {
            this.wrangleRightJoyCons();
        }
    }

    /**
     * Constructs a new {@code GlfwNxJoyConSeeker} with the arguments
     * for both {@code seekNxLeftJoyCons} and {@code seekNxRightJoyCons}
     * being {@code true}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     */
    public GlfwNxJoyConSeeker(long ptr_glfwWindow) {
        this(ptr_glfwWindow, true, true);
    }

    private void wrangleLeftJoyCons() {
        String guidsPath = "guids_nx_joycon_left.json";
        Collection<String> nxLeftGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(nxLeftGuids, GlfwNxLeftJoyConAdapter::wrangle);
    }

    private void wrangleRightJoyCons() {
        String guidsPath = "guids_nx_joycon_right.json";
        Collection<String> nxRightGuids = this.loadJsonGuids(guidsPath);
        this.wrangleGuids(nxRightGuids, GlfwNxRightJoyConAdapter::wrangle);
    }

}
