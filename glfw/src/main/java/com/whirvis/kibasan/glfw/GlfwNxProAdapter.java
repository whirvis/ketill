package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.nx.NxProController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.whirvis.kibasan.nx.NxProController.*;

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
        return new NxProController(new GlfwNxProAdapter(ptr_glfwWindow,
                glfwJoystick));
    }

    public GlfwNxProAdapter(long ptr_glfwWindow, int glfwJoystick) {
        super(ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter(@NotNull NxProController device,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapButton(registry, BUTTON_B, 0);
        this.mapButton(registry, BUTTON_A, 1);
        this.mapButton(registry, BUTTON_Y, 2);
        this.mapButton(registry, BUTTON_X, 3);
        this.mapButton(registry, BUTTON_L, 4);
        this.mapButton(registry, BUTTON_R, 5);
        this.mapButton(registry, BUTTON_ZL, 6);
        this.mapButton(registry, BUTTON_ZR, 7);
        this.mapButton(registry, BUTTON_MINUS, 8);
        this.mapButton(registry, BUTTON_PLUS, 9);
        this.mapButton(registry, BUTTON_L_THUMB, 10);
        this.mapButton(registry, BUTTON_R_THUMB, 11);
        this.mapButton(registry, BUTTON_HOME, 12);
        this.mapButton(registry, BUTTON_SCREENSHOT, 13);
        this.mapButton(registry, BUTTON_BUMPER, 14);
        this.mapButton(registry, BUTTON_Z_BUMPER, 15);
        this.mapButton(registry, BUTTON_UP, 16);
        this.mapButton(registry, BUTTON_RIGHT, 17);
        this.mapButton(registry, BUTTON_DOWN, 18);
        this.mapButton(registry, BUTTON_LEFT, 19);

        this.mapStick(registry, STICK_LS, LS_MAPPING);
        this.mapStick(registry, STICK_RS, RS_MAPPING);
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
