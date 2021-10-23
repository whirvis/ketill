package org.ardenus.engine.input.device.feature.monitor;

import java.util.Objects;

import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.event.ButtonHoldEvent;
import org.ardenus.engine.input.device.event.ButtonPressEvent;
import org.ardenus.engine.input.device.event.ButtonReleaseEvent;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class DeviceButtonState extends PressableState {

	protected final InputDevice device;
	protected final DeviceButton button;

	public DeviceButtonState(InputDevice device, DeviceButton button) {
		this.device = Objects.requireNonNull(device, "device");
		this.button = Objects.requireNonNull(button, "button");
	}
	
	@Override
	public boolean isPressed() {
		Button1bc state = device.getState(button);
		return state.pressed();
	}

	@Override
	public void onPress(boolean held) {
		Input.sendEvent(new ButtonPressEvent(device, button, held));
	}

	@Override
	public void onHold() {
		Input.sendEvent(new ButtonHoldEvent(device, button));
	}

	@Override
	public void onRelease(boolean held) {
		Input.sendEvent(new ButtonReleaseEvent(device, button, held));
	}

}
