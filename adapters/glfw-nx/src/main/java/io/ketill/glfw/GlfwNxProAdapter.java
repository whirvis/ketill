package io.ketill.glfw;

import io.ketill.MappedFeatureRegistry;
import io.ketill.nx.NxProController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static io.ketill.nx.NxProController.*;

public class GlfwNxProAdapter extends GlfwJoystickAdapter<NxProController> {

    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            LS_MAPPING = new GlfwStickMapping(0, 1, 10),
            RS_MAPPING = new GlfwStickMapping(2, 3, 11);
    /* @formatter:on */

    protected static float normalize(float pos, float min, float max) {
        pos = Math.min(Math.max(pos, min), max);
        float mid = (max - min) / 2.0F;
        return (pos - min - mid) / mid;
    }

    public static @NotNull NxProController wrangle(long ptr_glfwWindow,
                                                   int glfwJoystick) {
        return new NxProController((c, r) -> new GlfwNxProAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwNxProAdapter(@NotNull NxProController controller,
                            @NotNull MappedFeatureRegistry registry,
                            long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_B, 0);
        this.mapButton(BUTTON_A, 1);
        this.mapButton(BUTTON_Y, 2);
        this.mapButton(BUTTON_X, 3);
        this.mapButton(BUTTON_L, 4);
        this.mapButton(BUTTON_R, 5);
        this.mapButton(BUTTON_ZL, 6);
        this.mapButton(BUTTON_ZR, 7);
        this.mapButton(BUTTON_MINUS, 8);
        this.mapButton(BUTTON_PLUS, 9);
        this.mapButton(BUTTON_L_THUMB, 10);
        this.mapButton(BUTTON_R_THUMB, 11);
        this.mapButton(BUTTON_HOME, 12);
        this.mapButton(BUTTON_SCREENSHOT, 13);
        this.mapButton(BUTTON_BUMPER, 14);
        this.mapButton(BUTTON_Z_BUMPER, 15);
        this.mapButton(BUTTON_UP, 16);
        this.mapButton(BUTTON_RIGHT, 17);
        this.mapButton(BUTTON_DOWN, 18);
        this.mapButton(BUTTON_LEFT, 19);

        this.mapStick(STICK_LS, LS_MAPPING);
        this.mapStick(STICK_RS, RS_MAPPING);
    }

    @Override
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(stick, mapping);
        if (mapping == LS_MAPPING) {
            stick.x = normalize(stick.x, -0.70F, 0.70F);
            stick.y = normalize(stick.y, -0.76F, 0.72F);
            stick.y *= -1.0F;
        } else if (mapping == RS_MAPPING) {
            stick.x = normalize(stick.x, -0.72F, 0.72F);
            stick.y = normalize(stick.y, -0.68F, 0.76F);
            stick.y *= -1.0F;
        }
    }

}
