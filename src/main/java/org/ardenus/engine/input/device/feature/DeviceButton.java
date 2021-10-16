package org.ardenus.engine.input.device.feature;

import java.util.Objects;

import org.ardenus.engine.input.Button;
import org.ardenus.engine.input.Direction;

public class DeviceButton extends Button implements DeviceFeature<Button1bc> {

	private final String id;
	public final Direction direction;

	/**
	 * @param id
	 *            the button ID.
	 * @param direction
	 *            the direction this button represents. A {@code null} value is
	 *            permitted, and indicates that this button does not represent a
	 *            direction.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public DeviceButton(String id, Direction direction) {
		this.id = Objects.requireNonNull(id, "id");
		this.direction = direction;
	}

	/**
	 * @param id
	 *            the button ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public DeviceButton(String id) {
		this(id, null);
	}

	@Override
	public final String id() {
		return this.id;
	}

	@Override
	public Button1bc initial() {
		return new Button1b();
	}

}
