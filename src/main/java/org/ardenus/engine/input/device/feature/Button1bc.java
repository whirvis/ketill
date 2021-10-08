package org.ardenus.engine.input.device.feature;

/**
 * An interface to a read-only view of a 1-dimensional button.
 */
public interface Button1bc {

	/**
	 * Returns if this button is currently pressed.
	 * 
	 * @return {@code true} if this button is currently pressed, {@code false}
	 *         otherwise.
	 */
	public boolean pressed();

}
