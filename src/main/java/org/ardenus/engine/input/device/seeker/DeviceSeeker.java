package org.ardenus.engine.input.device.seeker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;

/**
 * A scanner for input devices.
 * <p>
 * The purpose of a device seeker is to scan for input devices currently
 * connected to the system. When an input device is detected, the appropriate
 * {@code InputDevice} instance and adapter will be created. Afterwards, the
 * device will be registered to the device seeker. Once an input device is
 * registered to a seeker, it will be polled in the {@link #poll()} method.
 * <p>
 * <b>Note:</b> For a device seeker to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the device seeker once on every application update.
 */
public abstract class DeviceSeeker {

	protected final Logger log;
	public final Class<? extends InputDevice> type;
	private final Set<SeekerListener> listeners;
	private final Set<InputDevice> devices;

	/**
	 * @param type
	 *            the device type to seek out.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public DeviceSeeker(Class<? extends InputDevice> type) {
		this.log = LogManager.getLogger(this.getClass());
		this.type = Objects.requireNonNull(type, "type");
		this.listeners = new HashSet<>();
		this.devices = new HashSet<>();
	}

	public void addListener(SeekerListener listener) {
		Objects.requireNonNull(listener, "listener");
		listeners.add(listener);
	}

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
	 * <b>Note:</b> Just because a device is registered to the seeker does
	 * <i>not</i> indicate that it is currently connected. It only means that
	 * the seeker has detected its presence and as such has registered it.
	 * 
	 * @return all devices registered to this seeker.
	 */
	public Set<InputDevice> registered() {
		return Collections.unmodifiableSet(devices);
	}

	/**
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
			log.debug("Registered " + device.id + " device");
		}
	}

	protected void unregister(InputDevice device) {
		if (device != null && devices.contains(device)) {
			devices.remove(device);
			this.callEvent(l -> l.onUnregister(this, device));
			log.debug("Unregistered " + device.id + " device");
		}
	}

	protected abstract void seek() throws Exception;

	/**
	 * Polling a device seeker is usually necessary for it to work properly. It
	 * is recommended to poll the device seeker once on every application
	 * update. When a device seeker is polled, it will make a call to its
	 * internal {@link #seek()} method, of which what it does is dependent on
	 * the implementation. Afterwards, it will poll each input device that it
	 * has registered.
	 * 
	 * @throws InputException
	 *             if an error occurs while seeking.
	 */
	public void poll() {
		try {
			this.seek();
			log.trace("Sought out devices");
		} catch (InputException e) {
			throw e; /* prevent wrapping */
		} catch (Exception e) {
			throw new InputException(e);
		}

		long pollStart = System.currentTimeMillis();
		for (InputDevice device : devices) {
			device.poll();
		}
		long pollFinish = System.currentTimeMillis();
		log.trace("Polled " + devices.size() + " devices (took"
				+ (pollFinish - pollStart) + "ms)");
	}

}
