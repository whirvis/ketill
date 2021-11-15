package com.whirvis.kibasan.feature;

import java.util.Objects;

public class PlayerLed implements DeviceFeature<PlayerLed1i> {

	private final String id;

	/**
	 * @param id
	 *            the player LED ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public PlayerLed(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public PlayerLed1i initial() {
		return new PlayerLed1i();
	}

}
