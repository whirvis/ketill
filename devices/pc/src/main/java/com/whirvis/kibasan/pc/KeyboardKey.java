package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class KeyboardKey extends DeviceFeature<Key1bc> {

	/**
	 * @param id
	 *            the key ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public KeyboardKey(String id) {
		super(id, Key1b::new);
	}

}
