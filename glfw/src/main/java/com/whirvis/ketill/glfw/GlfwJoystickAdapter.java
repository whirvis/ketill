package com.whirvis.ketill.glfw;

import com.whirvis.ketill.AnalogStick;
import com.whirvis.ketill.AnalogTrigger;
import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.DeviceButton;
import com.whirvis.ketill.FeatureAdapter;
import com.whirvis.ketill.IoDevice;
import com.whirvis.ketill.MappedFeatureRegistry;
import com.whirvis.ketill.Trigger1f;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public abstract class GlfwJoystickAdapter<I extends IoDevice>
        extends GlfwDeviceAdapter<I> {

    protected final int glfwJoystick;
    protected FloatBuffer axes;
    protected ByteBuffer buttons;

    public GlfwJoystickAdapter(@NotNull I device,
                               @NotNull MappedFeatureRegistry registry,
                               long ptr_glfwWindow, int glfwJoystick) {
        super(device, registry, ptr_glfwWindow);
        this.glfwJoystick = glfwJoystick;
    }

    protected void mapButton(@NotNull DeviceButton button, int glfwButton) {
        registry.mapFeature(button, glfwButton, this::updateButton);
    }

    protected void mapStick(@NotNull AnalogStick stick,
                            @NotNull GlfwStickMapping mapping) {
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    protected void mapTrigger(@NotNull AnalogTrigger trigger, int glfwAxis) {
        registry.mapFeature(trigger, glfwAxis, this::updateTrigger);
    }

    protected boolean isPressed(int glfwButton) {
        return buttons.get(glfwButton) != 0;
    }

    @FeatureAdapter
    protected void updateButton(@NotNull Button1b button, int glfwButton) {
        button.pressed = this.isPressed(glfwButton);
    }

    @FeatureAdapter
    protected void updateStick(@NotNull Vector3f stick,
                               @NotNull GlfwStickMapping mapping) {
        stick.x = axes.get(mapping.glfwAxisX);
        stick.y = axes.get(mapping.glfwAxisY);
        if (mapping.hasZButton) {
            boolean pressed = this.isPressed(mapping.glfwZButton);
            stick.z = pressed ? -1.0F : 0.0F;
        } else {
            stick.z = -1.0F;
        }
    }

    @FeatureAdapter
    protected void updateTrigger(@NotNull Trigger1f trigger, int glfwAxis) {
        trigger.force = axes.get(glfwAxis);
    }

    @Override
    protected void pollDevice() {
        this.axes = glfwGetJoystickAxes(glfwJoystick);
        this.buttons = glfwGetJoystickButtons(glfwJoystick);
    }

    @Override
    protected boolean isDeviceConnected() {
        return glfwJoystickPresent(glfwJoystick);
    }

}
