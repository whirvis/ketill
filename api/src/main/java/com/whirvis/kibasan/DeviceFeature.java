package com.whirvis.kibasan;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A feature for an {@link InputDevice}.
 * <p>
 * Input device features can represent either input or output. Examples of input
 * features include, but are not limited to: buttons, analog sticks, analog
 * triggers, etc. Examples of output features include, but are not limited to:
 * motor rumble, LED lighting, etc.
 *
 * @param <T>
 *            the container type.
 * @see InputDevice#addFeature(DeviceFeature)
 */
public abstract class DeviceFeature<T> {

	public final String id;
	public final Supplier<T> initial;

	/**
	 * @param id
	 * 			the feature ID.
	 * @param initial
	 * 			a supplier for a container of this feature's initial state.
	 * @throws NullPointerException
	 * 			if {@code id} or {@code initial} are {@code null}.
	 */
	public DeviceFeature(String id, Supplier<T> initial) {
		this.id = Objects.requireNonNull(id, "id");
		this.initial = Objects.requireNonNull(initial, "initial");
	}

}
