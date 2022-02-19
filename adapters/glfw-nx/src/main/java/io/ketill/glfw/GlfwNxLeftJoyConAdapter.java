package io.ketill.glfw;

import io.ketill.MappedFeatureRegistry;
import io.ketill.nx.NxLeftJoyCon;
import org.jetbrains.annotations.NotNull;

import static io.ketill.nx.NxLeftJoyCon.*;

public class GlfwNxLeftJoyConAdapter extends GlfwJoyConAdapter<NxLeftJoyCon> {

    /* @formatter:off */
    protected static final @NotNull JoyConStickMapping
            MAPPING_LS = new JoyConStickMapping(19, 17, 18, 16, 10);
    /* @formatter:on */

    public static @NotNull NxLeftJoyCon wrangle(long ptr_glfwWindow,
                                                int glfwJoystick) {
        return new NxLeftJoyCon((c, r) -> new GlfwNxLeftJoyConAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }

    public GlfwNxLeftJoyConAdapter(@NotNull NxLeftJoyCon device,
                                   @NotNull MappedFeatureRegistry registry,
                                   long ptr_glfwWindow, int glfwJoystick) {
        super(device, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_LEFT, 0);
        this.mapButton(BUTTON_DOWN, 1);
        this.mapButton(BUTTON_UP, 2);
        this.mapButton(BUTTON_RIGHT, 3);
        this.mapButton(BUTTON_SL, 4);
        this.mapButton(BUTTON_SR, 5);
        this.mapButton(BUTTON_MINUS, 8);
        this.mapButton(BUTTON_L_THUMB, 10);
        this.mapButton(BUTTON_SNAPSHOT, 13);
        this.mapButton(BUTTON_L, 14);
        this.mapButton(BUTTON_ZL, 15);

        this.mapJoyConStick(STICK_LS, MAPPING_LS);

        this.mapJoyConTrigger(TRIGGER_ZL, 15);
    }

}
