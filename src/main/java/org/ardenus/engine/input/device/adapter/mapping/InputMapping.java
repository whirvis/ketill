package org.ardenus.engine.input.device.adapter.mapping;

import java.util.Objects;

import org.ardenus.engine.input.device.InputSource;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;

/**
 * An input source mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a mapped input can not provide a meaningful mapping for an
 * input source. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. Two built-in examples
 * are {@link ButtonMapping} and {@link AnalogMapping}.
 *
 * @param <S>
 *            the mapping type.
 */
public abstract class InputMapping<S extends InputSource<?>> {

	public final S source;

	/**
	 * Constructs a new {@code MappedInput}.
	 * 
	 * @param source
	 *            the input source being mapped to.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 */
	public InputMapping(S source) {
		this.source = Objects.requireNonNull(source);
	}

}
