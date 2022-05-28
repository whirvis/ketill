package io.ketill.glfw.psx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.controller.TriggerStateZ;
import io.ketill.glfw.WranglerMethod;
import io.ketill.psx.Ps4Controller;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import static io.ketill.psx.Ps4Controller.*;

/**
 * A {@link Ps4Controller} adapter using GLFW.
 */
public class GlfwPs4Adapter extends GlfwPsxAdapter<Ps4Controller> {

    /**
     * Mappings for {@link Ps4Controller#TRIGGER_LT} and
     * {@link Ps4Controller#TRIGGER_RT}.
     */
    protected static final int AXIS_LT = 3, AXIS_RT = 4;

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled PlayStation 4 controller.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    /* @formatter:off */
    @WranglerMethod
    public static @NotNull Ps4Controller
            wrangle(long ptr_glfwWindow, int glfwJoystick) {
        return new Ps4Controller((c, r) -> new GlfwPs4Adapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }
    /* @formatter:on */

    /**
     * Constructs a new {@code GlfwPs4Adapter}.
     *
     * @param controller     the controller which owns this adapter.
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
    public GlfwPs4Adapter(@NotNull Ps4Controller controller,
                          @NotNull MappedFeatureRegistry registry,
                          long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    @MustBeInvokedByOverriders
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
    protected void updateTrigger(@NotNull TriggerStateZ state, int glfwAxis) {
        super.updateTrigger(state, glfwAxis);
        if (glfwAxis == AXIS_LT || glfwAxis == AXIS_RT) {
            state.force += 1.0F;
            state.force /= 2.0F;
        }
    }

}
