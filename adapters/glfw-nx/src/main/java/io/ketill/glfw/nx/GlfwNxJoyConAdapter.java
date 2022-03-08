package io.ketill.glfw.nx;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Trigger1f;
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
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer;
     *                                  if {@code glfwJoystick} is not a
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
    private void updateJoyConStick(@NotNull Vector3f stick,
                                   @NotNull JoyConStickMapping mapping) {
        if (this.isPressed(mapping.glfwLeft)) {
            stick.x = -1.0F;
        } else if (this.isPressed(mapping.glfwRight)) {
            stick.x = 1.0F;
        } else {
            stick.x = 0.0F;
        }

        if (this.isPressed(mapping.glfwUp)) {
            stick.y = 1.0F;
        } else if (this.isPressed(mapping.glfwDown)) {
            stick.y = -1.0F;
        } else {
            stick.y = 0.0F;
        }

        if (this.isPressed(mapping.glfwThumb)) {
            stick.z = -1.0F;
        } else {
            stick.z = 0.0F;
        }
    }

    @FeatureAdapter
    private void updateJoyConTrigger(@NotNull Trigger1f trigger,
                                     int glfwButton) {
        if (this.isPressed(glfwButton)) {
            trigger.force = 1.0F;
        } else {
            trigger.force = 0.0F;
        }
    }

}
