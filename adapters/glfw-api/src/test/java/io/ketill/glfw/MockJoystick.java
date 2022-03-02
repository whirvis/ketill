package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1bc;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.Trigger1fc;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

class MockJoystick extends IoDevice {

    static final DeviceButton BUTTON = new DeviceButton("button");
    static final AnalogStick STICK = new AnalogStick("stick");
    static final AnalogTrigger TRIGGER = new AnalogTrigger("trigger");

    final Button1bc button;
    final Vector3fc stick;
    final Trigger1fc trigger;

    public MockJoystick(@NotNull AdapterSupplier<?> adapterSupplier) {
        super("mock_joystick", adapterSupplier);
        this.button = this.registerFeature(BUTTON).state;
        this.stick = this.registerFeature(STICK).state;
        this.trigger = this.registerFeature(TRIGGER).state;
    }

}
