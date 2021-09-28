package org.ardenus.engine.input.device;

/**
 * A feature for an {@link InputDevice}.
 *
 * @param <T>
 *            the feature container type.
 * @see InputDevice#addFeature(DeviceFeature)
 */
public abstract class DeviceFeature<T> {

	public final boolean optional;

	/**
	 * Constructs a new {@code DeviceFeature}.
	 * 
	 * @param optional
	 *            {@code true} if this feature is optional, {@code false}
	 *            otherwise. If a feature is marked as optional, then a device
	 *            adapter can leave it unimplemented.
	 */
	public DeviceFeature(boolean optional) {
		this.optional = optional;
	}
	
	/**
	 * Creates a container for the initial state of this feature.
	 * 
	 * @return the created container.
	 */
	public abstract T initial();

}
