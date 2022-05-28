package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonStateZ;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

/**
 * GLFW joystick adapters are an extension of {@link GlfwDeviceAdapter}
 * which specialize in mapping data from GLFW joysticks. An assortment
 * of utility methods are provided for mapping features.
 * <p>
 * <b>Note:</b> The {@code glfwPollEvents()} <i>must</i> be called before
 * the adapter is polled. Failure to do so will result in GLFW returning
 * out-of-date input data to the adapter.
 * <p>
 * Feature adapters like {@link #updateStick(StickPosZ, GlfwStickMapping)}
 * can also be overridden to modify data returned from GLFW. An example of
 * this would be switching the polarity of an axis. This is done in the
 * {@code glfw.xbox} module, and can be seen in {@code GlfwXboxAdapter}.
 * <pre>
 *     &#64;Override
 *     protected void updateStick(&#64;NotNull StickPosZ state,
 *                                &#64;NotNull GlfwStickMapping mapping) {
 *         super.updateStick(state, mapping);
 *         if (mapping == MAPPING_LS || mapping == MAPPING_RS) {
 *             state.pos.y *= -1.0F;
 *         }
 *     }
 * </pre>
 *
 * @param <C> the controller type.
 * @see #mapButton(ControllerButton, int)
 * @see #mapStick(AnalogStick, GlfwStickMapping)
 * @see #mapTrigger(AnalogTrigger, int)
 * @see AdapterSupplier
 * @see GlfwJoystickSeeker
 */
public abstract class GlfwJoystickAdapter<C extends Controller>
        extends GlfwDeviceAdapter<C> {

    /**
     * The GLFW joystick this adapter interfaces with.
     * <p>
     * This field is {@code protected} so it is visible to child classes.
     * This allows them to interface with the GLFW joystick directly.
     */
    protected final int glfwJoystick;

    /*
     * These fields were originally protected, meaning extending classes
     * could access them directly. They were made private in favor of the
     * isPressed() and getAxis() methods, which are safer to use.
     */
    private ByteBuffer buttons;
    private FloatBuffer axes;

    /**
     * Constructs a new {@code GlfwJoystickAdapter}.
     *
     * @param device         the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code device} or {@code registry}
     *                                  are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero).
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    public GlfwJoystickAdapter(@NotNull C device,
                               @NotNull MappedFeatureRegistry registry,
                               long ptr_glfwWindow, int glfwJoystick) {
        super(device, registry, ptr_glfwWindow);
        this.glfwJoystick = GlfwUtils.requireJoystick(glfwJoystick);
    }

    /**
     * @param button     the button to map.
     * @param glfwButton the GLFW button to map {@code button} to.
     * @throws NullPointerException      if {@code button} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code glfwButton} is negative.
     * @see #updateButton(ButtonStateZ, int)
     */
    @MappingMethod
    protected void mapButton(@NotNull ControllerButton button, int glfwButton) {
        Objects.requireNonNull(button, "button cannot be null");
        GlfwUtils.requireButton(glfwButton, "glfwButton");
        registry.mapFeature(button, glfwButton, this::updateButton);
    }

    /**
     * @param stick   the stick to map.
     * @param mapping the GLFW stick mapping for {@code stick}.
     * @throws NullPointerException if {@code stick} or {@code mapping}
     *                              are {@code null}.
     * @see #updateStick(StickPosZ, GlfwStickMapping)
     */
    @MappingMethod
    protected void mapStick(@NotNull AnalogStick stick,
                            @NotNull GlfwStickMapping mapping) {
        Objects.requireNonNull(stick, "stick cannot be null");
        Objects.requireNonNull(mapping, "mapping cannot be null");
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    /**
     * @param trigger  the trigger to map.
     * @param glfwAxis the GLFW axis to map {@code trigger} to.
     * @throws NullPointerException     if {@code trigger} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwAxis} is negative.
     * @see #updateTrigger(TriggerStateZ, int)
     */
    @MappingMethod
    protected void mapTrigger(@NotNull AnalogTrigger trigger, int glfwAxis) {
        Objects.requireNonNull(trigger, "trigger cannot be null");
        GlfwUtils.requireAxis(glfwAxis, "glfwAxis");
        registry.mapFeature(trigger, glfwAxis, this::updateTrigger);
    }

    /**
     * @return the amount of GLFW buttons present on this joystick,
     * {@code -1} if the adapter has not yet been polled.
     */
    protected final int getButtonCount() {
        return buttons != null ? buttons.limit() : -1;
    }

    /**
     * @param glfwButton the ID of the GLFW button to check.
     * @return {@code true} if {@code glfwButton} is currently pressed,
     * {@code false} otherwise (or if it does not exist.)
     * @throws IndexOutOfBoundsException if {@code glfwButton} is negative or
     *                                   not smaller than the button count.
     */
    protected final boolean isPressed(int glfwButton) {
        if (buttons == null) {
            return false; /* buttons have yet to be set */
        }
        GlfwUtils.requireButton(glfwButton, buttons.limit(), "glfwButton");
        return buttons.get(glfwButton) != 0;
    }

    /**
     * @return the amount of GLFW axes present on this joystick,
     * {@code -1} if the adapter has not yet been polled.
     */
    protected final int getAxisCount() {
        return axes != null ? axes.limit() : -1;
    }

    /**
     * @param glfwAxis the ID of the GLFW axis to fetch.
     * @return the current axis value, {@code 0.0F} it does not exist.
     * @throws IndexOutOfBoundsException if {@code glfwAxis} is negative or
     *                                   not smaller than the axis count.
     */
    protected final float getAxis(int glfwAxis) {
        if (axes == null) {
            return 0.0F; /* axes have yet to be set, ignore */
        }
        GlfwUtils.requireAxis(glfwAxis, axes.limit(), "glfwAxis");
        return axes.get(glfwAxis);
    }

    /**
     * Updater for buttons mapped via
     * {@link #mapButton(ControllerButton, int)}.
     *
     * @param state      the button state.
     * @param glfwButton the GLFW button.
     */
    @FeatureAdapter
    protected void updateButton(@NotNull ButtonStateZ state, int glfwButton) {
        state.pressed = this.isPressed(glfwButton);
    }

    /**
     * Updater for analog sticks mapped via
     * {@link #mapStick(AnalogStick, GlfwStickMapping)}.
     *
     * @param state   the analog stick position.
     * @param mapping the GLFW stick mapping.
     */
    @FeatureAdapter
    protected void updateStick(@NotNull StickPosZ state,
                               @NotNull GlfwStickMapping mapping) {
        state.pos.x = this.getAxis(mapping.glfwXAxis);
        state.pos.y = this.getAxis(mapping.glfwYAxis);
        if (mapping.hasZButton) {
            boolean pressed = this.isPressed(mapping.glfwZButton);
            state.pos.z = pressed ? -1.0F : 0.0F;
        } else {
            state.pos.z = 0.0F;
        }
    }

    /**
     * Updater for analog triggers mapped via
     * {@link #mapTrigger(AnalogTrigger, int)}.
     *
     * @param state    the analog trigger state.
     * @param glfwAxis the GLFW axis.
     */
    @FeatureAdapter
    protected void updateTrigger(@NotNull TriggerStateZ state, int glfwAxis) {
        state.force = this.getAxis(glfwAxis);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        this.buttons = glfwGetJoystickButtons(glfwJoystick);
        this.axes = glfwGetJoystickAxes(glfwJoystick);
    }

    @Override
    protected final boolean isDeviceConnected() {
        return glfwJoystickPresent(glfwJoystick);
    }

}
