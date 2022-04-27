package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

class MockJoystick extends IoDevice {

    static final DeviceButton BUTTON = new DeviceButton("button");
    static final AnalogStick STICK = new AnalogStick("stick");
    static final AnalogTrigger TRIGGER = new AnalogTrigger("trigger");

    final ButtonState button;
    final StickPos stick;
    final TriggerState trigger;

    public MockJoystick(@NotNull AdapterSupplier<?> adapterSupplier) {
        super("mock_joystick", adapterSupplier);
        this.button = this.registerFeature(BUTTON).containerState;
        this.stick = this.registerFeature(STICK).containerState;
        this.trigger = this.registerFeature(TRIGGER).containerState;
    }

}
