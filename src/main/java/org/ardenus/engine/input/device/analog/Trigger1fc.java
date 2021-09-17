package org.ardenus.engine.input.device.analog;

/**
 * An interface to a read-only view of a 1-dimensional trigger.
 */
public interface Trigger1fc {

	/**
	 * Returns the force being applied to the trigger.
	 * 
	 * @return the force being applied to the trigger.
	 */
	public float force();

}
