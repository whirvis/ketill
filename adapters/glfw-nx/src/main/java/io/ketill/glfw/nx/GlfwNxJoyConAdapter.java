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

abstract class GlfwNxJoyConAdapter<J extends NxJoyCon> extends GlfwJoystickAdapter<J> {

    GlfwNxJoyConAdapter(@NotNull J joycon,
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
    private void updateJoyConStick(@NotNull StickPosZ state,
                                   @NotNull JoyConStickMapping mapping) {
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
