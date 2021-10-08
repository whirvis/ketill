package org.ardenus.engine.input.device.adapter.mapping;

import java.util.Objects;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.DeviceFeature;

/**
 * A device feature mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a mapped input can not provide a meaningful mapping for a
 * device feature. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. Two built-in examples
 * are {@link ButtonMapping} and {@link AnalogMapping}.
 *
 * @param <F>
 *            the mapping type.
 */
public abstract class FeatureMapping<F extends DeviceFeature<?>> {

	public final F feature;

	/**
	 * Constructs a new {@code MappedInput}.
	 * 
	 * @param feature
	 *            the device feature being mapped to.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 */
	public FeatureMapping(F feature) {
		this.feature = Objects.requireNonNull(feature);
	}

}
