package io.ketill.glfw.nx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.glfw.WranglerMethod;
import io.ketill.nx.NxRightJoyCon;
import org.jetbrains.annotations.NotNull;

import static io.ketill.nx.NxRightJoyCon.*;

public class GlfwNxRightJoyConAdapter
        extends GlfwNxJoyConAdapter<NxRightJoyCon> {

    /* @formatter:off */
    protected static final @NotNull JoyConStickMapping
            MAPPING_RS = new JoyConStickMapping(17,19,16,18, 11);
    /* @formatter:on */

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled Switch right Joy-Con.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
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
     * @param controller     the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code controller} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    public GlfwNxRightJoyConAdapter(@NotNull NxRightJoyCon controller,
                                    @NotNull MappedFeatureRegistry registry,
                                    long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
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
        this.mapButton(BUTTON_ZR, 15);

        this.mapJoyConStick(STICK_RS, MAPPING_RS);

        this.mapJoyConTrigger(TRIGGER_ZR, 15);
    }

}
