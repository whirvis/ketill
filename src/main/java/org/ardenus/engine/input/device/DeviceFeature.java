package org.ardenus.engine.input.device;

/**
 * A feature for an {@link InputDevice}.
 *
 * @param <T>
 *            the container type.
 * @see InputDevice#addFeature(DeviceFeature)
 */
public interface DeviceFeature<T> {

	/**
	 * Returns the name of this feature.
	 * 
	 * @return the name of this feature.
	 */
	public String name();

	/**
	 * Creates a container for the initial state of this feature.
	 * 
	 * @return the created container.
	 */
	public T initial();

	/**
	 * Returns if this feature is optional.
	 * <p>
	 * If a feature is not optional, an implementation defined error will be
	 * generated if it is not present.
	 * 
	 * @return {@code true} if this feature is optional, {@code false}
	 *         otherwise.
	 */
	public default boolean optional() {
		return true;
	}

}
