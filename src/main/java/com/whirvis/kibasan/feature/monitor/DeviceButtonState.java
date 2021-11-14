package com.whirvis.kibasan.feature.monitor;

import java.util.Objects;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.event.ButtonHoldEvent;
import com.whirvis.kibasan.event.ButtonPressEvent;
import com.whirvis.kibasan.event.ButtonReleaseEvent;
import com.whirvis.kibasan.feature.Button1bc;
import com.whirvis.kibasan.feature.DeviceButton;

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
