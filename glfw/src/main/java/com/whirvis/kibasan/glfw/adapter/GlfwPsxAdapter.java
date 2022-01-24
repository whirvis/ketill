package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.psx.PsxController;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.whirvis.kibasan.psx.PsxController.*;

public abstract class GlfwPsxAdapter<I extends PsxController>
        extends GlfwJoystickAdapter<I> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 8),
            RS_MAPPING = new GlfwStickMapping(2, 5, 9);
    /* @formatter:on */

    public GlfwPsxAdapter(long ptr_glfwWindow, int glfwJoystick) {
        super(ptr_glfwWindow, glfwJoystick);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter(@NotNull I controller,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapButton(registry, BUTTON_SQUARE, 0);
        this.mapButton(registry, BUTTON_CROSS, 1);
        this.mapButton(registry, BUTTON_CIRCLE, 2);
        this.mapButton(registry, BUTTON_TRIANGLE, 3);
        this.mapButton(registry, BUTTON_L1, 4);
        this.mapButton(registry, BUTTON_R1, 5);
        this.mapButton(registry, BUTTON_L2, 6);
        this.mapButton(registry, BUTTON_R2, 7);
        this.mapButton(registry, BUTTON_L_THUMB, 8);
        this.mapButton(registry, BUTTON_R_THUMB, 9);

        this.mapStick(registry, STICK_LS, LS_MAPPING);
        this.mapStick(registry, STICK_RS, RS_MAPPING);
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
