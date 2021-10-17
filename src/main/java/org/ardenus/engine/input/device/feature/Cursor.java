package org.ardenus.engine.input.device.feature;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public class Cursor extends DeviceAnalog<Vector2fc> {

	/**
	 * @param id
	 *            the cursor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Cursor(String id) {
		super(Vector2fc.class, id);
	}

	@Override
	public Vector2f zero() {
		return new Vector2f();
	}

}
