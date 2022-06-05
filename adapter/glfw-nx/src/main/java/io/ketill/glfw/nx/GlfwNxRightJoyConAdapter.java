package io.ketill.glfw.nx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.glfw.WranglerMethod;
import io.ketill.nx.NxRightJoyCon;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import static io.ketill.nx.NxRightJoyCon.*;

/**
 * An {@link NxRightJoyCon} adapter using GLFW.
 */
public class GlfwNxRightJoyConAdapter extends GlfwNxJoyConAdapter<NxRightJoyCon> {

    /**
     * Mapping for {@link NxRightJoyCon#STICK_RS}.
     */
    /* @formatter:off */
    protected static final @NotNull GlfwNxJoyConStickMapping
            MAPPING_RS = new GlfwNxJoyConStickMapping(17,19,16,18, 11);
    /* @formatter:on */

    /**
     * Mapping for {@link NxRightJoyCon#BUTTON_ZR} and
     * {@link  NxRightJoyCon#TRIGGER_RT}.
     */
    protected static final int INDEX_ZR = 15;

    /**
     * Wrangles a GLFW joystick into an {@link NxRightJoyCon}.
     * <p>
     * <b>Thread safety:</b> This method is <i>not</i> thread-safe. It must
     * be called on the thread which created {@code ptr_glfwWindow}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled Switch right Joy-Con.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    /* @formatter:off */
    @WranglerMethod
    public static @NotNull NxRightJoyCon
            wrangle(long ptr_glfwWindow, int glfwJoystick) {
        return new NxRightJoyCon((c, r) -> new GlfwNxRightJoyConAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }
    /* @formatter:on */

    /**
     * Constructs a new {@code GlfwNxRightJoyConAdapter}.
     *
     * @param joycon         the controller which owns this adapter.
     * @param registry       the controller's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code controller} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    public GlfwNxRightJoyConAdapter(@NotNull NxRightJoyCon joycon,
                                    @NotNull MappedFeatureRegistry registry,
                                    long ptr_glfwWindow, int glfwJoystick) {
        super(joycon, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        this.mapButton(BUTTON_A, 0);
        this.mapButton(BUTTON_X, 1);
        this.mapButton(BUTTON_B, 2);
        this.mapButton(BUTTON_Y, 3);
        this.mapButton(BUTTON_SL, 4);
        this.mapButton(BUTTON_SR, 5);
        this.mapButton(BUTTON_PLUS, 9);
        this.mapButton(BUTTON_R_THUMB, 11);
        this.mapButton(BUTTON_HOME, 12);
        this.mapButton(BUTTON_R, 14);
        this.mapButton(BUTTON_ZR, INDEX_ZR);

        this.mapJoyConStick(STICK_RS, MAPPING_RS);

        this.mapJoyConTrigger(TRIGGER_RT, INDEX_ZR);
    }

}
