package org.ardenus.engine.input.device.seeker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;

/**
 * A sentry for input devices.
 * <p>
 * The job of a device seeker is to scan for input devices that are connected to
 * the system. When an input device is detected, the appropriate
 * {@code InputDevice} instance and adapter will be created.
 */
public abstract class DeviceSeeker {

	public final Class<? extends InputDevice> type;
	private final Set<InputDevice> devices;

	/**
	 * Constructs a new {@code DeviceSeeker}.
	 * 
	 * @param type
	 *            the class of the input device that this seeker will connect.
	 */
	public DeviceSeeker(Class<? extends InputDevice> type) {
		this.type = type;
		this.devices = new HashSet<>();
	}

	/**
	 * Returns all devices connected via this seeker.
	 * 
	 * @return all devices connected via this seeker.
	 */
	public Set<InputDevice> connected() {
		return Collections.unmodifiableSet(devices);
	}

	/**
	 * Connects an input device to this seeker.
	 * 
	 * @param device
	 *            the device to connect.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 * @throws ClassCastException
	 *             if the class of {@code device} is not equal to {@code type}
	 *             which was specified during construction.
	 */
	protected void connect(InputDevice device) {
		Objects.requireNonNull(device, "device");
		if (type != device.getClass()) {
			throw new ClassCastException("device class must equal type");
		}
		devices.add(device);
	}

	/**
	 * Disconnects an input device from this seeker.
	 * 
	 * @param device
	 *            the device to disconnect.
	 */
	protected void disconnect(InputDevice device) {
		devices.remove(device);
	}

	/**
	 * Seeks for input devices.
	 * 
	 * @throws Exception
	 *             if an error occurs.
	 */
	protected abstract void seek() throws Exception;

	/**
	 * Seeks for input devices and polls all connected input devices.
	 * 
	 * @throws InputException
	 *             if an error occurs while seeking.
	 */
	public void poll() {
		try {
			this.seek();
		} catch (Exception e) {
			throw new InputException(e);
		}

		for (InputDevice device : devices) {
			device.poll();
		}
	}

}
