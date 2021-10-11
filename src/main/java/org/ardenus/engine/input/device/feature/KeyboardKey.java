package org.ardenus.engine.input.device.feature;

/**
 * Represents a key that is present on a keyboard.
 */
public class KeyboardKey extends DeviceButton {

	/**
	 * Constructs a new {@code KeyboardKey}.
	 * 
	 * @param name
	 *            the key name.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public KeyboardKey(String name) {
		super(name);
	}

}
