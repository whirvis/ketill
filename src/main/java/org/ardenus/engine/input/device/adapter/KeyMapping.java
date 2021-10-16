package org.ardenus.engine.input.device.adapter;

import org.ardenus.engine.input.device.feature.KeyboardKey;

public class KeyMapping extends ButtonMapping {

	/**
	 * @param key
	 *            the key being mapped to.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public KeyMapping(KeyboardKey key) {
		super(key);
	}

}
