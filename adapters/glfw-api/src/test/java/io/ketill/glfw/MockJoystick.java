package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonState;
import io.ketill.controller.Controller;
import io.ketill.controller.ControllerButton;
import io.ketill.controller.StickPos;
import io.ketill.controller.TriggerState;
import org.jetbrains.annotations.NotNull;

class MockJoystick extends Controller {

    static final ControllerButton BUTTON = new ControllerButton("button");
    static final AnalogStick STICK = new AnalogStick("stick");
    static final AnalogTrigger TRIGGER = new AnalogTrigger("trigger");

    final ButtonState button;
    final StickPos stick;
    final TriggerState trigger;

    public MockJoystick(@NotNull AdapterSupplier<?> adapterSupplier) {
        super("mock_joystick", adapterSupplier, null, null, null, null);
        this.button = this.registerFeature(BUTTON).getState();
        this.stick = this.registerFeature(STICK).getState();
        this.trigger = this.registerFeature(TRIGGER).getState();
    }

}
