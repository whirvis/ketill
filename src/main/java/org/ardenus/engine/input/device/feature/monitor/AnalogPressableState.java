package org.ardenus.engine.input.device.feature.monitor;

import java.util.Objects;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.event.StickHoldEvent;
import org.ardenus.engine.input.device.event.StickPressEvent;
import org.ardenus.engine.input.device.event.StickReleaseEvent;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.joml.Vector3fc;

public class AnalogPressableState extends PressableState {

	protected final InputDevice device;
	protected final AnalogStick stick;
	protected final Direction direction;

	public AnalogPressableState(InputDevice device, AnalogStick stick,
			Direction direction) {
		this.device = Objects.requireNonNull(device, "device");
		this.stick = Objects.requireNonNull(stick, "stick");
		this.direction = Objects.requireNonNull(direction, "direction");
	}

	@Override
	public void onPress(boolean held) {
		Input.sendEvent(new StickPressEvent(device, stick, direction, held));
	}

	@Override
	public void onHold() {
		Input.sendEvent(new StickHoldEvent(device, stick, direction));
	}

	@Override
	public void onRelease(boolean held) {
		Input.sendEvent(new StickReleaseEvent(device, stick, direction, held));
	}

	@Override
	public void update(long currentTime) {
		Vector3fc pos = device.getState(stick);
		boolean pressed = AnalogStick.isPressed(pos, direction);
		this.update(currentTime, pressed);
	}

}
