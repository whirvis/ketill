package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.Trigger1f;
import com.whirvis.kibasan.psx.Ps5Controller;
import org.jetbrains.annotations.NotNull;

import static com.whirvis.kibasan.psx.Ps5Controller.*;

public class GlfwPs5Adapter extends GlfwPsxAdapter<Ps5Controller> {

    protected static final int AXIS_LT = 3, AXIS_RT = 4;

    public static @NotNull Ps5Controller wrangle(long ptr_glfwWindow,
                                                 int glfwJoystick) {
        return new Ps5Controller((c, r) -> new GlfwPs5Adapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwPs5Adapter(@NotNull Ps5Controller controller,
                          @NotNull MappedFeatureRegistry registry,
                          long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();

        this.mapButton(BUTTON_SHARE, 8);
        this.mapButton(BUTTON_OPTIONS, 9);
        this.mapButton(BUTTON_PS, 12);
        this.mapButton(BUTTON_TPAD, 13);
        this.mapButton(BUTTON_MUTE, 14);
        this.mapButton(BUTTON_UP, 15);
        this.mapButton(BUTTON_RIGHT, 16);
        this.mapButton(BUTTON_DOWN, 17);
        this.mapButton(BUTTON_LEFT, 18);

        this.mapTrigger(TRIGGER_LT, AXIS_LT);
        this.mapTrigger(TRIGGER_RT, AXIS_RT);
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
