package org.ardenus.engine.input.device.seeker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;

/**
 * A scanner for input devices.
 * <p>
 * The purpose of a device seeker is to scan for input devices currently
 * connected to the system. When an input device is detected, the appropriate
 * {@code InputDevice} instance and adapter will be created. This device will
 * then be registered to the device seeker. Once an input device is registered
 * to a seeker, it will be polled in the {@link #poll()} method.
 * <p>
 * TODO
 */
public abstract class DeviceSeeker {

	public final Class<? extends InputDevice> type;
	protected final Set<SeekerListener> listeners;
	private final Set<InputDevice> devices;

	/**
	 * Constructs a new {@code DeviceSeeker}.
	 * 
	 * @param type
	 *            the device type to seek out.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public DeviceSeeker(Class<? extends InputDevice> type) {
		this.type = Objects.requireNonNull(type, "type");
		this.listeners = new HashSet<>();
		this.devices = new HashSet<>();
	}

	/**
	 * Adds a listener to this device seeker.
	 * 
	 * @param listener
	 *            the listener to add.
	 * @throws NullPointerException
	 *             if {@code listener} is {@code null}.
	 */
	public void addListener(SeekerListener listener) {
		Objects.requireNonNull(listener, "listener");
		listeners.add(listener);
	}

	/**
	 * Removes a listener from this input device.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(SeekerListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	protected void callEvent(Consumer<SeekerListener> event) {
		for (SeekerListener listener : listeners) {
			event.accept(listener);
		}
	}

	/**
	 * Returns all devices registered to this seeker.
	 * <p>
	 * <b>Note:</b> Just because a device is registered to the seeker does
	 * <i>not</i> indicate that it is currently connected. It only means that
	 * the seeker has detected its presence and as such has registered it. TODO
	 * 
	 * @return all devices registered to this seeker.
	 */
	public Set<InputDevice> registered() {
		return Collections.unmodifiableSet(devices);
	}

	/**
	 * Registers an input device to this seeker.
	 * <p>
	 * TODO
	 * 
	 * @param device
	 *            the device to register.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 * @throws ClassCastException
	 *             if the class of {@code device} is not equal to {@code type}
	 *             which was specified during construction.
	 */
	protected void register(InputDevice device) {
		Objects.requireNonNull(device, "device");
		if (type != device.getClass()) {
			throw new ClassCastException("device class must equal type");
		}

		if (!devices.contains(device)) {
			devices.add(device);
			this.callEvent(l -> l.onRegister(this, device));
		}
	}

	/**
	 * Unregisters an input device from this seeker.
	 * 
	 * @param device
	 *            the device to unregister.
	 */
	protected void unregister(InputDevice device) {
		if (device != null && devices.contains(device)) {
			devices.remove(device);
			this.callEvent(l -> l.onUnregister(this, device));
		}
	}

	/**
	 * Seeks for input devices.
	 * 
	 * @throws Exception
	 *             if an error occurs.
	 */
	protected abstract void seek() throws Exception;

	/**
	 * Seeks for input devices and polls all registered input devices.
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
