package io.ketill.glfw.nx;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.nx.NxJoyCon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

abstract class GlfwNxJoyConAdapter<J extends NxJoyCon> extends GlfwJoystickAdapter<J> {

    GlfwNxJoyConAdapter(@NotNull J joycon,
                        @NotNull MappedFeatureRegistry registry,
                        long ptr_glfwWindow, int glfwJoystick) {
        super(joycon, registry, ptr_glfwWindow, glfwJoystick);
    }

    /**
     * Since JoyCons report their analog stick axes as buttons, rather than
     * proper axes, the position of an analog stick will depend solely on
     * if the buttons which correspond to those axes are pressed or not.
     * <p>
     * When the corresponding button for an axis is pressed, it's position
     * will be {@code 1.0F}. Otherwise, it will be {@code 0.0F}.
     *
     * @param stick   the analog stick to map.
     * @param mapping the JoyCon stick mapping for {@code stick}.
     * @throws NullPointerException if {@code stick} or {@code mapping}
     *                              are {@code null}.
     * @see #updateJoyConStick(StickPosZ, GlfwNxJoyConStickMapping)
     */
    @MappingMethod
    protected void mapJoyConStick(@NotNull AnalogStick stick,
                                  @NotNull GlfwNxJoyConStickMapping mapping) {
        Objects.requireNonNull(stick, "stick cannot be null");
        Objects.requireNonNull(mapping, "mapping cannot be null");
        registry.mapFeature(stick, mapping, this::updateJoyConStick);
    }

    /**
     * Since JoyCons use buttons for analog triggers, rather than proper
     * triggers, the force of an analog trigger will depend solely on if
     * its button is pressed.
     * <p>
     * When the corresponding button for a trigger is pressed, it's force
     * will be {@code 1.0F}. Otherwise, it will be {@code 0.0F}.
     *
     * @param trigger    the analog trigger to map.
     * @param glfwButton the GLFW button to map {@code trigger} to.
     * @throws NullPointerException     if {@code trigger} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     * @see #updateJoyConTrigger(TriggerStateZ, int)
     */
    @MappingMethod
    @SuppressWarnings("SameParameterValue")
    protected void mapJoyConTrigger(@NotNull AnalogTrigger trigger,
                                    int glfwButton) {
        Objects.requireNonNull(trigger, "trigger cannot be null");
        registry.mapFeature(trigger, glfwButton, this::updateJoyConTrigger);
    }

    /**
     * Updater for JoyCon sticks mapped via
     * {@link #mapJoyConStick(AnalogStick, GlfwNxJoyConStickMapping)}.
     *
     * @param state   the analog stick position.
     * @param mapping the JoyCon stick mapping.
     */
    @FeatureAdapter
    protected void updateJoyConStick(@NotNull StickPosZ state,
                                     @NotNull GlfwNxJoyConStickMapping mapping) {
        if (this.isPressed(mapping.glfwLeft)) {
            state.pos.x = -1.0F;
        } else if (this.isPressed(mapping.glfwRight)) {
            state.pos.x = 1.0F;
        } else {
            state.pos.x = 0.0F;
        }

        if (this.isPressed(mapping.glfwUp)) {
            state.pos.y = 1.0F;
        } else if (this.isPressed(mapping.glfwDown)) {
            state.pos.y = -1.0F;
        } else {
            state.pos.y = 0.0F;
        }

        if (this.isPressed(mapping.glfwThumb)) {
            state.pos.z = -1.0F;
        } else {
            state.pos.z = 0.0F;
        }
    }

    /**
     * Updater for JoyCon triggers mapped via
     * {@link #mapJoyConTrigger(AnalogTrigger, int)}.
     *
     * @param state      the analog trigger state.
     * @param glfwButton the GLFW button.
     */
    @FeatureAdapter
    protected void updateJoyConTrigger(@NotNull TriggerStateZ state,
                                       int glfwButton) {
        if (this.isPressed(glfwButton)) {
            state.force = 1.0F;
        } else {
            state.force = 0.0F;
        }
    }

}
