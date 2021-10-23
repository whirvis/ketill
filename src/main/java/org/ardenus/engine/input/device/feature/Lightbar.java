package org.ardenus.engine.input.device.feature;

import org.joml.Vector4f;

public class Lightbar extends DeviceAnalog<Vector4f> {

	/**
	 * @param id
	 *            the lightbar ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Lightbar(String id) {
		super(Vector4f.class, id);
	}

	@Override
	public Vector4f zero() {
		return new Vector4f();
	}

}
