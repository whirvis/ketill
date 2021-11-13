package org.ardenus.input.feature.monitor;

import java.util.Objects;

import org.ardenus.input.InputDevice;
import org.ardenus.input.event.ButtonHoldEvent;
import org.ardenus.input.event.ButtonPressEvent;
import org.ardenus.input.event.ButtonReleaseEvent;
import org.ardenus.input.feature.Button1bc;
import org.ardenus.input.feature.DeviceButton;

import com.whirvex.event.EventManager;

public class DeviceButtonState extends PressableState {

	protected final InputDevice device;
	protected final EventManager events;
	protected final DeviceButton button;

	public DeviceButtonState(InputDevice device, EventManager events,
			DeviceButton button) {
		this.device = Objects.requireNonNull(device, "device");
		this.events = Objects.requireNonNull(events, "events");
		this.button = Objects.requireNonNull(button, "button");
	}

	@Override
	public boolean isPressed() {
		Button1bc state = device.getState(button);
		return state.pressed();
	}

	@Override
	public void onPress(boolean held) {
		events.send(new ButtonPressEvent(device, button, held));
	}

	@Override
	public void onHold() {
		events.send(new ButtonHoldEvent(device, button));
	}

	@Override
	public void onRelease(boolean held) {
		events.send(new ButtonReleaseEvent(device, button, held));
	}

}
