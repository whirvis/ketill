package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.InputDevice;

/**
 * An interface used to listen for {@link DeviceSeeker} events.
 */
public interface SeekerListener {

	/**
	 * @param seeker
	 *            the seeker that registered the device.
	 * @param device
	 *            the device that was registered.
	 */
	public default void onRegister(DeviceSeeker seeker, InputDevice device) {
	}

	/**
	 * @param seeker
	 *            the seeker that unregistered the device.
	 * @param device
	 *            the device that was unregistered.
	 */
	public default void onUnregister(DeviceSeeker seeker, InputDevice device) {
	}

}
