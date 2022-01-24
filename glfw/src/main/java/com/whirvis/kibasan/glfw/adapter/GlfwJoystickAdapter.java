package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.kibasan.AnalogStick;
import com.whirvis.kibasan.AnalogTrigger;
import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.DeviceButton;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.Trigger1f;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public abstract class GlfwJoystickAdapter<I extends InputDevice>
        extends GlfwDeviceAdapter<I> {

    protected final int glfwJoystick;
    protected FloatBuffer axes;
    protected ByteBuffer buttons;

    public GlfwJoystickAdapter(long ptr_glfwWindow, int glfwJoystick) {
        super(ptr_glfwWindow);
        this.glfwJoystick = glfwJoystick;
    }

    protected void mapButton(@NotNull MappedFeatureRegistry registry,
                             @NotNull DeviceButton button, int glfwButton) {
        registry.mapFeature(button, glfwButton, this::updateButton);
    }

    protected void mapStick(@NotNull MappedFeatureRegistry registry,
                            @NotNull AnalogStick stick,
                            @NotNull GlfwStickMapping mapping) {
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    protected void mapTrigger(@NotNull MappedFeatureRegistry registry,
                              @NotNull AnalogTrigger trigger, int glfwAxis) {
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
    protected void pollDevice(@NotNull I device) {
        this.axes = glfwGetJoystickAxes(glfwJoystick);
        this.buttons = glfwGetJoystickButtons(glfwJoystick);
    }

    @Override
    protected boolean isDeviceConnected(@NotNull I device) {
        return glfwJoystickPresent(glfwJoystick);
    }

}
