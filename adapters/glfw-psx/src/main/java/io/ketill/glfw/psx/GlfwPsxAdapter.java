package io.ketill.glfw.psx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.glfw.GlfwStickMapping;
import io.ketill.psx.PsxController;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static io.ketill.psx.PsxController.*;

public abstract class GlfwPsxAdapter<I extends PsxController> extends GlfwJoystickAdapter<I> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 8),
            RS_MAPPING = new GlfwStickMapping(2, 5, 9);
    /* @formatter:on */

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

        this.mapStick(STICK_LS, LS_MAPPING);
        this.mapStick(STICK_RS, RS_MAPPING);
    }

    @Override
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(stick, mapping);
        if (mapping == LS_MAPPING || mapping == RS_MAPPING) {
            stick.y *= -1.0F;
        }
    }

}
