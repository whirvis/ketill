package org.ardenus.engine.input.device.adapter;

import org.ardenus.engine.input.device.feature.KeyboardKey;

/**
 * A {@link KeyboardKey} mapping for use with a {@link DeviceAdapter}.
 *
 * @see ButtonMapping
 */
public class KeyMapping extends ButtonMapping {

	/**
	 * Constructs a new {@code KeyMapping}.
	 * 
	 * @param button
	 *            the key being mapped to.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public KeyMapping(KeyboardKey key) {
		super(key);
	}

}
