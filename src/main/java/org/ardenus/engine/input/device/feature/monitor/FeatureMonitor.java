package org.ardenus.engine.input.device.feature.monitor;

import java.util.Objects;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.feature.DeviceFeature;

/**
 * A monitor for the features of an input device.
 * <p>
 * The purpose of a feature monitor is to monitor the state of an input device
 * and its features. What a monitor does in response to a change in state is
 * dependent on the implementation. A common use for monitors is to send events
 * which signal the change of a feature's state.
 */
public abstract class FeatureMonitor {

	protected final InputDevice device;

	/**
	 * @param device
	 *            the device whose features to monitor.
	 * @throws NullPointerException
	 *             if {@code device} is {@code null}.
	 */
	public FeatureMonitor(InputDevice device) {
		this.device = Objects.requireNonNull(device, "device");
	}

	/**
	 * @param device
	 *            the device to check.
	 * @return {@code true} if this monitor is assigned to {@code device},
	 *         {@code false} otherwise.
	 */
	public final boolean isAssignedTo(InputDevice device) {
		return this.device == device;
	}

	/**
	 * Indicates this monitor of a new feature to track.
	 * <p>
	 * Whether or not the monitor actually tracks the state of the feature is
	 * dependant on the implementation. Feature monitors have every right to
	 * ignore a feature that they are told to monitor. This method is only to
	 * indicate when they are registered to the input device, preventing the
	 * need to cycle through every device feature on every update.
	 * 
	 * @param feature
	 *            the feature to track.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 */
	public abstract void monitor(DeviceFeature<?> feature);

	/**
	 * Indicates this monitor of a feature to stop tracking.
	 * 
	 * @param feature
	 *            the feature to stop tracking.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 */
	public abstract void forget(DeviceFeature<?> feature);

	public abstract void update();

}
