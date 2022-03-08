package io.ketill.glfw.psx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.glfw.GlfwStickMapping;
import io.ketill.psx.PsxController;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static io.ketill.psx.PsxController.*;

/**
 * The base for a Sony PlayStation GLFW controller adapter.
 * <p>
 * <b>Note:</b> This class is <i>not</i> an adapter for a PlayStation 1
 * controller. If a GLFW adapter for PlayStation 1 controllers is added
 * in the future, the class will be named {@code GlfwPs1Adapter}.
 *
 * @see GlfwPs4Adapter
 * @see GlfwPs5Adapter
 */
public abstract class GlfwPsxAdapter<I extends PsxController>
        extends GlfwJoystickAdapter<I> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            MAPPING_LS = new GlfwStickMapping(0, 1, 8),
            MAPPING_RS = new GlfwStickMapping(2, 5, 9);
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
    public GlfwPsxAdapter(@NotNull I controller,
                          @NotNull MappedFeatureRegistry registry,
                          long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        this.mapButton(BUTTON_SQUARE, 0);
        this.mapButton(BUTTON_CROSS, 1);
        this.mapButton(BUTTON_CIRCLE, 2);
        this.mapButton(BUTTON_TRIANGLE, 3);
        this.mapButton(BUTTON_L1, 4);
        this.mapButton(BUTTON_R1, 5);
        this.mapButton(BUTTON_L2, 6);
        this.mapButton(BUTTON_R2, 7);
        this.mapButton(BUTTON_L_THUMB, 8);
        this.mapButton(BUTTON_R_THUMB, 9);

        this.mapStick(STICK_LS, MAPPING_LS);
        this.mapStick(STICK_RS, MAPPING_RS);
    }

    @Override
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(stick, mapping);
        if (mapping == MAPPING_LS || mapping == MAPPING_RS) {
            stick.y *= -1.0F;
        }
    }

}
