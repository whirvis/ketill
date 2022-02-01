package com.whirvis.ketill.glfw;

import com.whirvis.ketill.MappedFeatureRegistry;
import com.whirvis.ketill.Trigger1f;
import com.whirvis.ketill.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.whirvis.ketill.xbox.XboxController.*;

public class GlfwXboxAdapter extends GlfwJoystickAdapter<XboxController> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 8),
            RS_MAPPING = new GlfwStickMapping(2, 3, 9);
    /* @formatter:on */

    protected static final int AXIS_LT = 4, AXIS_RT = 5;

    public static @NotNull XboxController wrangle(long ptr_glfwWindow,
                                                  int glfwJoystick) {
        return new XboxController((c, r) -> new GlfwXboxAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwXboxAdapter(@NotNull XboxController controller,
                           @NotNull MappedFeatureRegistry registry,
                           long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_A, 0);
        this.mapButton(BUTTON_B, 1);
        this.mapButton(BUTTON_X, 2);
        this.mapButton(BUTTON_Y, 3);
        this.mapButton(BUTTON_LB, 4);
        this.mapButton(BUTTON_RB, 5);
        this.mapButton(BUTTON_GUIDE, 6);
        this.mapButton(BUTTON_START, 7);
        this.mapButton(BUTTON_L_THUMB, 8);
        this.mapButton(BUTTON_R_THUMB, 9);
        this.mapButton(BUTTON_UP, 10);
        this.mapButton(BUTTON_RIGHT, 11);
        this.mapButton(BUTTON_DOWN, 12);
        this.mapButton(BUTTON_LEFT, 13);

        this.mapStick(STICK_LS, LS_MAPPING);
        this.mapStick(STICK_RS, RS_MAPPING);

        this.mapTrigger(TRIGGER_LT, AXIS_LT);
        this.mapTrigger(TRIGGER_RT, AXIS_RT);
    }

    @Override
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(stick, mapping);
        if (mapping == LS_MAPPING || mapping == RS_MAPPING) {
            stick.y *= -1.0F;
        }
    }

    @Override
    protected void updateTrigger(@NotNull Trigger1f trigger, int glfwAxis) {
        super.updateTrigger(trigger, glfwAxis);
        if (glfwAxis == AXIS_LT || glfwAxis == AXIS_RT) {
            trigger.force += 1.0F;
            trigger.force /= 2.0F;
        }
    }

}
