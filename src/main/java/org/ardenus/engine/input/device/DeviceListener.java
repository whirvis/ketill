package org.ardenus.engine.input.device;

/**
 * An interface used to listen for {@link InputDevice} events.
 * 
 * @see InputDevice#addListener(DeviceListener)
 */
public interface DeviceListener {

	/**
	 * Called when an input device has connected.
	 * 
	 * @param device
	 *            the connected device.
	 */
	public default void onConnect(InputDevice device) {
	}

	/**
	 * Called when an input device has disconnected.
	 * 
	 * @param device
	 *            the disconnected device.
	 */
	public default void onDisconnect(InputDevice device) {
	}

}
