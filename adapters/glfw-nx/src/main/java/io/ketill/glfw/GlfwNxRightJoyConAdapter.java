package io.ketill.glfw;

import io.ketill.MappedFeatureRegistry;
import io.ketill.nx.NxRightJoyCon;
import org.jetbrains.annotations.NotNull;

import static io.ketill.nx.NxRightJoyCon.*;

public class GlfwNxRightJoyConAdapter extends GlfwJoyConAdapter<NxRightJoyCon> {

    /* @formatter:off */
    protected static final @NotNull JoyConStickMapping
            MAPPING_RS = new JoyConStickMapping(17,19,16,18, 11);
    /* @formatter:on */

    public static @NotNull NxRightJoyCon wrangle(long ptr_glfwWindow,
                                                 int glfwJoystick) {
        return new NxRightJoyCon((c, r) -> new GlfwNxRightJoyConAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwNxRightJoyConAdapter(@NotNull NxRightJoyCon device,
                                    @NotNull MappedFeatureRegistry registry,
                                    long ptr_glfwWindow, int glfwJoystick) {
        super(device, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_A, 0);
        this.mapButton(BUTTON_X, 1);
        this.mapButton(BUTTON_B, 2);
        this.mapButton(BUTTON_Y, 3);
        this.mapButton(BUTTON_SL, 4);
        this.mapButton(BUTTON_SR, 5);
        this.mapButton(BUTTON_PLUS, 9);
        this.mapButton(BUTTON_R_THUMB, 11);
        this.mapButton(BUTTON_HOME, 12);
        this.mapButton(BUTTON_R, 14);
        this.mapButton(BUTTON_ZR, 15);

        this.mapJoyConStick(STICK_RS, MAPPING_RS);

        this.mapJoyConTrigger(TRIGGER_ZR, 15);
    }

}
