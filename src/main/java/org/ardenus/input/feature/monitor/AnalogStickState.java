package org.ardenus.input.feature.monitor;

import java.util.Objects;

import org.ardenus.input.Direction;
import org.ardenus.input.InputDevice;
import org.ardenus.input.event.StickHoldEvent;
import org.ardenus.input.event.StickPressEvent;
import org.ardenus.input.event.StickReleaseEvent;
import org.ardenus.input.feature.AnalogStick;
import org.joml.Vector3fc;

import com.whirvex.event.EventManager;

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
