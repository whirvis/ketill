package com.whirvis.kibasan;

import java.util.Objects;

/**
 * A device feature mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a feature mapping can not usually provide a meaningful mapping
 * for a device feature. It must be extended by a class which provides the info
 * meaningful to the context of a relevant device adapter.
 *
 * @param <F>
 *            the feature type.
 */
public abstract class FeatureMapping<F extends DeviceFeature<?>> {

	public final F feature;

	/**
	 * @param feature
	 *            the feature being mapped to.
	 * @throws NullPointerException
	 *             if {@code feature} is {@code null}.
	 */
	public FeatureMapping(F feature) {
		this.feature = Objects.requireNonNull(feature);
	}

}
