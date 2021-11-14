package com.whirvis.kibasan.feature.monitor;

import java.util.Objects;

import org.joml.Vector3fc;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.Direction;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.event.StickHoldEvent;
import com.whirvis.kibasan.event.StickPressEvent;
import com.whirvis.kibasan.event.StickReleaseEvent;
import com.whirvis.kibasan.feature.AnalogStick;

public class AnalogStickState extends PressableState {

	protected final InputDevice device;
	protected final EventManager events;
	protected final AnalogStick stick;
	protected final Direction direction;

	public AnalogStickState(InputDevice device, EventManager events,
			AnalogStick stick, Direction direction) {
		this.device = Objects.requireNonNull(device, "device");
		this.events = Objects.requireNonNull(events, "events");
		this.stick = Objects.requireNonNull(stick, "stick");
		this.direction = Objects.requireNonNull(direction, "direction");
	}

	@Override
	public boolean isPressed() {
		Vector3fc pos = device.getState(stick);
		return AnalogStick.isPressed(pos, direction);
	}

	@Override
	public void onPress(boolean held) {
		events.send(new StickPressEvent(device, stick, direction, held));
	}

	@Override
	public void onHold() {
		events.send(new StickHoldEvent(device, stick, direction));
	}

	@Override
	public void onRelease(boolean held) {
		events.send(new StickReleaseEvent(device, stick, direction, held));
	}

}
