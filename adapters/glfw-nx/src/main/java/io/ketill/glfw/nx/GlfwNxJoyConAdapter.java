package io.ketill.glfw.nx;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.TriggerStateZ;
import io.ketill.glfw.GlfwJoystickAdapter;
import io.ketill.nx.NxJoyCon;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

abstract class GlfwNxJoyConAdapter<I extends NxJoyCon>
        extends GlfwJoystickAdapter<I> {

    /**
     * @param joycon         the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @param glfwJoystick   the GLFW joystick.
     * @throws NullPointerException     if {@code joycon} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code glfwJoystick} is not a
     *                                  valid GLFW joystick.
     */
    GlfwNxJoyConAdapter(@NotNull I joycon,
                        @NotNull MappedFeatureRegistry registry,
                        long ptr_glfwWindow, int glfwJoystick) {
        super(joycon, registry, ptr_glfwWindow, glfwJoystick);
    }

    @MappingMethod
    void mapJoyConStick(@NotNull AnalogStick stick,
                        @NotNull JoyConStickMapping mapping) {
        registry.mapFeature(stick, mapping, this::updateJoyConStick);
    }

    @MappingMethod
    @SuppressWarnings("SameParameterValue")
    void mapJoyConTrigger(@NotNull AnalogTrigger trigger, int glfwButton) {
        registry.mapFeature(trigger, glfwButton, this::updateJoyConTrigger);
    }

    @FeatureAdapter
    private void updateJoyConStick(@NotNull Vector3f pos,
                                   @NotNull JoyConStickMapping mapping) {
        if (this.isPressed(mapping.glfwLeft)) {
            pos.x = -1.0F;
        } else if (this.isPressed(mapping.glfwRight)) {
            pos.x = 1.0F;
        } else {
            pos.x = 0.0F;
        }

        if (this.isPressed(mapping.glfwUp)) {
            pos.y = 1.0F;
        } else if (this.isPressed(mapping.glfwDown)) {
            pos.y = -1.0F;
        } else {
            pos.y = 0.0F;
        }

        if (this.isPressed(mapping.glfwThumb)) {
            pos.z = -1.0F;
        } else {
            pos.z = 0.0F;
        }
    }

    @FeatureAdapter
    private void updateJoyConTrigger(@NotNull TriggerStateZ state,
                                     int glfwButton) {
        if (this.isPressed(glfwButton)) {
            state.force = 1.0F;
        } else {
            state.force = 0.0F;
        }
    }

}
