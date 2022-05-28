package io.ketill.glfw.nx;

import io.ketill.MappedFeatureRegistry;
import io.ketill.glfw.WranglerMethod;
import io.ketill.nx.NxLeftJoyCon;
import org.jetbrains.annotations.NotNull;

import static io.ketill.nx.NxLeftJoyCon.*;

/**
 * An {@link NxLeftJoyCon} adapter using GLFW.
 */
public class GlfwNxLeftJoyConAdapter extends GlfwNxJoyConAdapter<NxLeftJoyCon> {

    /**
     * Mapping for {@link NxLeftJoyCon#STICK_LS}.
     */
    /* @formatter:off */
    protected static final @NotNull JoyConStickMapping
            MAPPING_LS = new JoyConStickMapping(19, 17, 18, 16, 10);
    /* @formatter:on */

    /**
     * Mapping for {@link NxLeftJoyCon#BUTTON_ZL} and
     * {@link NxLeftJoyCon#TRIGGER_LT}.
     */
    protected static final int INDEX_ZL = 15;

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled Switch left Joy-Con.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    /* @formatter:off */
    @WranglerMethod
    public static @NotNull NxLeftJoyCon
            wrangle(long ptr_glfwWindow, int glfwJoystick) {
        return new NxLeftJoyCon((c, r) -> new GlfwNxLeftJoyConAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }
    /* @formatter:on */

    /**
     * Constructs a new {@code GlfwNxLeftJoyConAdapter}.
     *
     * @param joycon         the controller which owns this adapter.
     * @param registry       the controller's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code controller} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    public GlfwNxLeftJoyConAdapter(@NotNull NxLeftJoyCon joycon,
                                   @NotNull MappedFeatureRegistry registry,
                                   long ptr_glfwWindow, int glfwJoystick) {
        super(joycon, registry, ptr_glfwWindow, glfwJoystick);
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
        this.mapButton(BUTTON_ZL, INDEX_ZL);

        this.mapJoyConStick(STICK_LS, MAPPING_LS);

        this.mapJoyConTrigger(TRIGGER_LT, INDEX_ZL);
    }

}
