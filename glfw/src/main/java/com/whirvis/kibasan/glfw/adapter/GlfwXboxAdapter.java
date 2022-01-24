package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.Trigger1f;
import com.whirvis.kibasan.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.whirvis.kibasan.xbox.XboxController.*;

public class GlfwXboxAdapter extends GlfwJoystickAdapter<XboxController> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 8),
            RS_MAPPING = new GlfwStickMapping(2, 3, 9);
    /* @formatter:on */

    protected static final int AXIS_LT = 4, AXIS_RT = 5;

    public GlfwXboxAdapter(long ptr_glfwWindow, int glfwJoystick) {
        super(ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter(@NotNull XboxController controller,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapButton(registry, BUTTON_A, 0);
        this.mapButton(registry, BUTTON_B, 1);
        this.mapButton(registry, BUTTON_X, 2);
        this.mapButton(registry, BUTTON_Y, 3);
        this.mapButton(registry, BUTTON_LB, 4);
        this.mapButton(registry, BUTTON_RB, 5);
        this.mapButton(registry, BUTTON_GUIDE, 6);
        this.mapButton(registry, BUTTON_START, 7);
        this.mapButton(registry, BUTTON_L_THUMB, 8);
        this.mapButton(registry, BUTTON_R_THUMB, 9);
        this.mapButton(registry, BUTTON_UP, 10);
        this.mapButton(registry, BUTTON_RIGHT, 11);
        this.mapButton(registry, BUTTON_DOWN, 12);
        this.mapButton(registry, BUTTON_LEFT, 13);

        this.mapStick(registry, STICK_LS, LS_MAPPING);
        this.mapStick(registry, STICK_RS, RS_MAPPING);

        this.mapTrigger(registry, TRIGGER_LT, AXIS_LT);
        this.mapTrigger(registry, TRIGGER_RT, AXIS_RT);
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
