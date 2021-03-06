package io.ketill.glfw.nx;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.glfw.GlfwStickMapping;
import io.ketill.glfw.GlfwUtils;
import io.ketill.glfw.WranglerMethod;
import io.ketill.nx.NxProController;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.ketill.nx.NxProController.*;

/**
 * An {@link NxProController} adapter using GLFW.
 */
public class GlfwNxProAdapter extends GlfwJoystickAdapter<NxProController> {

    /**
     * Mappings for {@link NxProController#STICK_LS} and
     * {@link NxProController#STICK_RS}.
     */
    /* @formatter:off */
    protected static final @NotNull GlfwStickMapping
            MAPPING_LS = new GlfwStickMapping(0, 1, 10),
            MAPPING_RS = new GlfwStickMapping(2, 3, 11);
    /* @formatter:on */

    /**
     * Mappings for {@link NxProController#TRIGGER_LT} and
     * {@link NxProController#TRIGGER_RT}.
     */
    protected static final int INDEX_ZL = 6, INDEX_ZR = 7;

    /**
     * Wrangles a GLFW joystick into an {@link NxProController}.
     * <p>
     * <b>Thread safety:</b> This method is <i>not</i> thread-safe. It must
     * be called on the thread which created {@code ptr_glfwWindow}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @return the wrangled Switch Pro controller.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    /* @formatter:off */
    @WranglerMethod
    public static @NotNull NxProController
            wrangle(long ptr_glfwWindow, int glfwJoystick) {
        return new NxProController((c, r) -> new GlfwNxProAdapter(c, r,
                ptr_glfwWindow, glfwJoystick));
    }
    /* @formatter:on */

    /**
     * Constructs a new {@code GlfwNxProAdapter}.
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
    public GlfwNxProAdapter(@NotNull NxProController controller,
                            @NotNull MappedFeatureRegistry registry,
                            long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    /**
     * Maps an {@link AnalogTrigger} to a GLFW button.
     * <p>
     * <b>Adapter quirk:</b> Since Switch Pro Controllers use buttons for
     * analog triggers, rather than proper bumpers, the force of an analog
     * trigger will depend solely on if its button is pressed.
     * <p>
     * When the corresponding button for a trigger is pressed, it's force
     * will be {@code 1.0F}. Otherwise, it will be {@code 0.0F}.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
     * No calls to the GLFW library are made.
     *
     * @param trigger    the analog trigger to map.
     * @param glfwButton the GLFW button to map {@code trigger} to.
     * @throws NullPointerException     if {@code trigger} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     * @see #updateProTrigger(TriggerStateZ, int)
     */
    @MappingMethod
    protected void mapProTrigger(@NotNull AnalogTrigger trigger,
                                 int glfwButton) {
        Objects.requireNonNull(trigger, "trigger cannot be null");
        GlfwUtils.requireButton(glfwButton, "glfwButton");
        registry.mapFeature(trigger, glfwButton, this::updateProTrigger);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        this.mapButton(BUTTON_B, 0);
        this.mapButton(BUTTON_A, 1);
        this.mapButton(BUTTON_Y, 2);
        this.mapButton(BUTTON_X, 3);
        this.mapButton(BUTTON_L, 4);
        this.mapButton(BUTTON_R, 5);
        this.mapButton(BUTTON_ZL, INDEX_ZL);
        this.mapButton(BUTTON_ZR, INDEX_ZR);
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

        this.mapStick(STICK_LS, MAPPING_LS);
        this.mapStick(STICK_RS, MAPPING_RS);

        this.mapProTrigger(TRIGGER_LT, INDEX_ZL);
        this.mapProTrigger(TRIGGER_RT, INDEX_ZR);
    }

    @Override
    protected void updateStick(@NotNull StickPosZ state,
                               @NotNull GlfwStickMapping mapping) {
        super.updateStick(state, mapping);
        if (mapping == MAPPING_LS || mapping == MAPPING_RS) {
            state.pos.y *= -1.0F;
        }
    }

    /**
     * Updater for Switch Pro Controller triggers mapped via
     * {@link #mapProTrigger(AnalogTrigger, int)}.
     * <p>
     * <b>Thread safety:</b> This method relies on {@link #isPressed(int)}.
     * Therefore, it is <i>not</i> thread-safe. It must be called on the
     * thread which created {@code ptr_glfwWindow}.
     *
     * @param state      the analog trigger state.
     * @param glfwButton the GLFW button.
     */
    @FeatureAdapter
    protected void updateProTrigger(@NotNull TriggerStateZ state,
                                    int glfwButton) {
        if (this.isPressed(glfwButton)) {
            state.force = 1.0F;
        } else {
            state.force = 0.0F;
        }
    }

}
