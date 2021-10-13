package org.ardenus.engine.input.device.feature;

/**
 * Represents a key that is present on a keyboard.
 */
public class KeyboardKey extends DeviceButton {

	/**
	 * Constructs a new {@code KeyboardKey}.
	 * 
	 * @param id
	 *            the key ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public KeyboardKey(String id) {
		super(id);
	}

}
