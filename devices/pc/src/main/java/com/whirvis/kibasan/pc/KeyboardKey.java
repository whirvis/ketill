package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class KeyboardKey implements DeviceFeature<Key1bc> {

	private final String id;

	/**
	 * @param id
	 *            the key ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public KeyboardKey(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Key1bc initial() {
		return new Key1b();
	}

}
