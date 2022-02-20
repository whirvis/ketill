package io.ketill.glfw.psx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.controller.Trigger1f;
import io.ketill.psx.Ps4Controller;
import org.jetbrains.annotations.NotNull;

import static io.ketill.psx.Ps4Controller.*;

public class GlfwPs4Adapter extends GlfwPsxAdapter<Ps4Controller> {

    protected static final int AXIS_LT = 3, AXIS_RT = 4;

    public static @NotNull Ps4Controller wrangle(long ptr_glfwWindow,
                                                 int glfwJoystick) {
        return new Ps4Controller((c, r) -> new GlfwPs4Adapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwPs4Adapter(@NotNull Ps4Controller controller,
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
        this.mapButton(BUTTON_UP, 14);
        this.mapButton(BUTTON_RIGHT, 15);
        this.mapButton(BUTTON_DOWN, 16);
        this.mapButton(BUTTON_LEFT, 17);

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
